# Week 4：进阶功能

这周学Redis缓存、JWT登录认证、AOP切面、多环境配置和部署。

---

## 1. Redis入门

Redis是内存数据库，速度极快，常用来做缓存、会话存储。

### 安装

- Windows：下载 https://github.com/tporadowski/redis/releases 解压运行
- 或用Docker：`docker run -d -p 6379:6379 redis`
- 启动后默认端口6379

### 引入依赖（pom.xml）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 配置（application.yml）

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      # password: 你的密码（如果有）
```

### RedisTemplate使用

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public User getById(int id) {
        // 1. 先查Redis缓存
        String key = "user:" + id;
        User cached = (User) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;  // 缓存命中，直接返回
        }

        // 2. 缓存没命中，查数据库
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 3. 查到后存入Redis，设置过期时间30分钟
        redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
        return user;
    }

    public User update(User user) {
        userMapper.updateById(user);
        // 更新后删除缓存（保证缓存和数据库一致）
        redisTemplate.delete("user:" + user.getId());
        return user;
    }

    public void delete(int id) {
        userMapper.deleteById(id);
        redisTemplate.delete("user:" + id);
    }
}
```

### Redis常用数据结构

```java
// String（最常用）
redisTemplate.opsForValue().set("key", "value");
redisTemplate.opsForValue().set("key", "value", 10, TimeUnit.SECONDS);  // 10秒过期
Object val = redisTemplate.opsForValue().get("key");
redisTemplate.delete("key");

// Hash（存对象的某个字段）
redisTemplate.opsForHash().put("user:1", "name", "张三");
redisTemplate.opsForHash().put("user:1", "age", "20");
redisTemplate.opsForHash().get("user:1", "name");

// List（列表）
redisTemplate.opsForList().leftPush("queue", "任务1");
redisTemplate.opsForList().rightPop("queue");

// Set（集合，自动去重）
redisTemplate.opsForSet().add("tags", "Java", "Spring", "Redis");
redisTemplate.opsForSet().members("tags");

// ZSet（有序集合，带分数排序）
redisTemplate.opsForZSet().add("rank", "张三", 100);
redisTemplate.opsForZSet().add("rank", "李四", 90);
redisTemplate.opsForZSet().reverseRange("rank", 0, -1);  // 按分数降序
```

### 练习：给商品查询接口加上Redis缓存。

<details>
<summary>参考答案</summary>

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public Product getById(int id) {
        String key = "product:" + id;
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        redisTemplate.opsForValue().set(key, product, 30, TimeUnit.MINUTES);
        return product;
    }

    public Product update(Product product) {
        getById(product.getId());
        productMapper.updateById(product);
        redisTemplate.delete("product:" + product.getId());
        return product;
    }

    public void delete(int id) {
        getById(id);
        productMapper.deleteById(id);
        redisTemplate.delete("product:" + id);
    }
}
```

</details>

---

## 2. JWT登录认证

用户登录后，服务器返回一个token（JWT），前端每次请求带上token，服务器验证身份。

### 引入依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### JWT工具类

```java
package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // 密钥（至少32字符，生产环境放配置文件）
    private static final String SECRET = "my-super-secret-key-for-jwt-2024-very-long";
    private static final long EXPIRE = 24 * 60 * 60 * 1000;  // 24小时

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // 生成token
    public static String generateToken(int userId, String username) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("username", username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRE))
            .signWith(getKey())
            .compact();
    }

    // 解析token
    public static Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // 获取用户ID
    public static int getUserId(String token) {
        return Integer.parseInt(parseToken(token).getSubject());
    }

    // 获取用户名
    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    // 验证token是否过期
    public static boolean isExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }
}
```

### 登录接口

```java
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    public String login(LoginDTO dto) {
        // 查数据库验证用户名密码
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername())
               .eq(User::getPassword, dto.getPassword());  // 实际项目要加密！

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成JWT
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
```

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO dto) {
        String token = authService.login(dto);
        return Result.success(token);
    }
}
```

### 拦截器（验证token）

```java
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                             Object handler) throws Exception {
        // OPTIONS请求（预检请求）直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录");
        }

        // 去掉Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        try {
            int userId = JwtUtil.getUserId(token);
            // 把用户信息存到request里，后面Controller可以用
            request.setAttribute("userId", userId);
            request.setAttribute("username", JwtUtil.getUsername(token));
            return true;
        } catch (Exception e) {
            throw new BusinessException(401, "token无效或已过期");
        }
    }
}
```

```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/**")           // 拦截所有路径
            .excludePathPatterns(             // 排除不需要登录的路径
                "/auth/login",
                "/auth/register",
                "/doc.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            );
    }
}
```

### 获取当前登录用户

```java
// Controller中获取当前登录用户信息
@GetMapping("/me")
public Result<User> getCurrentUser(HttpServletRequest request) {
    int userId = (int) request.getAttribute("userId");
    return Result.success(userService.getById(userId));
}
```

### 练习：实现注册+登录+获取个人信息三个接口。

<details>
<summary>参考答案</summary>

```java
// AuthController.java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid LoginDTO dto) {
        authService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }
}

// AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    public void register(LoginDTO dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());  // 实际项目要BCrypt加密！
        userMapper.insert(user);
    }

    public String login(LoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername())
               .eq(User::getPassword, dto.getPassword());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}

// UserController.java（需要登录才能访问的接口）
@GetMapping("/me")
public Result<User> me(HttpServletRequest request) {
    int userId = (int) request.getAttribute("userId");
    return Result.success(userService.getById(userId));
}
```

**测试流程：**
1. POST /auth/register `{"username":"test","password":"123456"}` → 注册成功
2. POST /auth/login `{"username":"test","password":"123456"}` → 返回token
3. GET /user/me，Header: `Authorization: Bearer <token>` → 返回用户信息

</details>

---

## 3. AOP（面向切面编程）

AOP就是在不修改原代码的情况下，给方法"加功能"。比如：记录日志、权限校验、性能监控。

### 核心概念

| 概念 | 说明 |
|------|------|
| 切面（Aspect） | 要加的功能（如日志记录） |
| 切入点（Pointcut） | 在哪些方法上加 |
| 通知（Advice） | 在方法执行的哪个时机加（前/后/环绕） |
| 连接点（JoinPoint） | 具体被拦截的方法 |

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 示例：记录接口调用日志

```java
package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect      // 标记为切面
@Component
public class LogAspect {

    // 切入点：controller包下所有类的所有方法
    @Pointcut("execution(* com.example.demo.controller..*.*(..))")
    public void controllerPointcut() {}

    // 环绕通知：方法执行前后都执行
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info(">>> {}.{}() 开始执行", className, methodName);

        // 执行原方法
        Object result = joinPoint.proceed();

        long cost = System.currentTimeMillis() - start;
        log.info("<<< {}.{}() 执行完成，耗时 {}ms", className, methodName, cost);

        return result;
    }
}
```

### 示例：自定义注解 + AOP实现接口限流

```java
// 自定义注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int value() default 10;  // 每秒最多请求次数
}
```

```java
// AOP切面
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = joinPoint.getSignature().toShortString();
        long now = System.currentTimeMillis();
        int maxRequests = rateLimit.value();

        List<Long> times = requestLog.computeIfAbsent(key, k -> new ArrayList<>());
        // 清理1秒前的记录
        times.removeIf(t -> now - t > 1000);

        if (times.size() >= maxRequests) {
            throw new BusinessException(429, "请求过于频繁");
        }

        times.add(now);
        return joinPoint.proceed();
    }
}
```

```java
// 使用自定义注解
@RateLimit(5)  // 每秒最多5次
@GetMapping("/search")
public Result<List<User>> search(...) {
    // ...
}
```

---

## 4. 多环境配置

开发、测试、生产环境的数据库地址、端口等配置不同。

### 配置文件结构

```
resources/
├── application.yml              # 公共配置
├── application-dev.yml          # 开发环境
├── application-test.yml         # 测试环境
└── application-prod.yml         # 生产环境
```

### application.yml（公共配置）

```yaml
spring:
  profiles:
    active: dev  # 默认激活dev环境
  application:
    name: my-app

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### application-dev.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_dev?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
```

### application-prod.yml

```yaml
server:
  port: 80

spring:
  datasource:
    url: jdbc:mysql://your-server:3306/demo_prod?useSSL=false&serverTimezone=Asia/Shanghai
    username: prod_user
    password: ${DB_PASSWORD}  # 从环境变量读取，不写在代码里
  data:
    redis:
      host: your-redis-server
      port: 6379
      password: ${REDIS_PASSWORD}
```

### 切换环境

```yaml
# application.yml中改
spring:
  profiles:
    active: prod  # 改成prod

# 或者启动时指定
java -jar app.jar --spring.profiles.active=prod
```

---

## 5. 项目部署

### 打包

```bash
mvn clean package -DskipTests  # 跳过测试打包
# 生成 target/xxx.jar
```

### 部署到Linux服务器

```bash
# 上传jar包到服务器
scp target/my-app.jar user@server:/app/

# SSH登录服务器运行
ssh user@server
cd /app

# 前台运行（关终端就停）
java -jar my-app.jar

# 后台运行（推荐）
nohup java -jar my-app.jar --spring.profiles.active=prod > app.log 2>&1 &

# 查看日志
tail -f app.log

# 停止进程
ps aux | grep my-app.jar
kill <PID>
```

### Docker部署（更推荐）

```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/my-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 构建镜像
docker build -t my-app:1.0 .

# 运行容器
docker run -d -p 8080:8080 --name my-app my-app:1.0

# 查看日志
docker logs -f my-app

# 停止容器
docker stop my-app
```

---

## 6. 综合练习：完整的登录系统

实现：注册 → 登录 → 获取个人信息 → 更新个人信息 → 修改密码，所有接口需要JWT认证（除注册和登录外）。

<details>
<summary>参考答案</summary>

**User.java**（加username和password字段）
```java
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO_INCREMENT)
    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer age;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

**AuthController.java**
```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid LoginDTO dto) {
        authService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }
}
```

**UserController.java**
```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<User> me(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        return Result.success(userService.getById(userId));
    }

    @PutMapping("/me")
    public Result<User> updateMe(HttpServletRequest request, @RequestBody UserUpdateDTO dto) {
        int userId = (int) request.getAttribute("userId");
        return Result.success(userService.updateProfile(userId, dto));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request, 
                                        @RequestBody @Valid PasswordDTO dto) {
        int userId = (int) request.getAttribute("userId");
        userService.changePassword(userId, dto);
        return Result.success();
    }
}
```

**PasswordDTO.java**
```java
@Data
public class PasswordDTO {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String newPassword;
}
```

**UserService.java**
```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User getById(int id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setPassword(null);  // 不返回密码
        return user;
    }

    public User updateProfile(int userId, UserUpdateDTO dto) {
        User user = new User();
        user.setId(userId);
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        userMapper.updateById(user);
        return getById(userId);
    }

    public void changePassword(int userId, PasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException("原密码错误");
        }
        User update = new User();
        update.setId(userId);
        update.setPassword(dto.getNewPassword());
        userMapper.updateById(update);
    }
}
```

</details>

---

## Week 4 自检

- [ ] 会用RedisTemplate做基本的缓存操作
- [ ] 能实现JWT生成和解析
- [ ] 能写拦截器验证token
- [ ] 理解AOP的概念，能写简单的切面
- [ ] 会配置多环境（dev/test/prod）
- [ ] 能把Spring Boot项目打包部署到服务器

---

## Phase 2 总结

到这里，Spring Boot核心阶段结束。3周你学了：

| Week | 内容 | 核心技能 |
|------|------|----------|
| Week 2 | Spring Boot入门 | Controller、三层架构、统一返回、异常处理 |
| Week 3 | 数据库与常用功能 | MyBatis-Plus、条件查询、分页、参数校验、接口文档 |
| Week 4 | 进阶功能 | Redis缓存、JWT登录、AOP、多环境配置、部署 |

**接下来进入Phase 3：独立项目。** 当你能独立完成上面的综合练习时，就可以开始做自己的项目了。
