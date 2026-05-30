# Week 3：数据库与常用功能

Week 2的数据存在内存里，重启就没了。这周学MySQL数据库，让数据持久化。

---

## 1. MySQL基础

### 安装

- 下载：https://dev.mysql.com/downloads/installer/
- 安装时选Developer Default，设置root密码（记住！）
- 安装完用命令行或Navicat/DBeaver连接测试

### 基本SQL

```sql
-- 创建数据库
CREATE DATABASE demo_db DEFAULT CHARSET utf8mb4;

-- 使用数据库
USE demo_db;

-- 创建表
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- 自增主键
    name VARCHAR(50) NOT NULL,          -- 不为空
    age INT DEFAULT 0,                  -- 默认值0
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP  -- 创建时间自动填充
);

-- 增
INSERT INTO user (name, age, email) VALUES ('张三', 20, 'zhangsan@test.com');
INSERT INTO user (name, age, email) VALUES ('李四', 22, 'lisi@test.com');

-- 删
DELETE FROM user WHERE id = 1;

-- 改
UPDATE user SET age = 21, email = 'new@test.com' WHERE id = 2;

-- 查
SELECT * FROM user;                          -- 查所有
SELECT name, age FROM user WHERE age > 20;   -- 条件查询
SELECT * FROM user ORDER BY age DESC;        -- 降序排列
SELECT * FROM user LIMIT 10 OFFSET 0;        -- 分页：第1页，每页10条
SELECT COUNT(*) FROM user;                   -- 总数
SELECT AVG(age) FROM user;                   -- 平均年龄
```

### 多表联查

```sql
-- 假设有order表
CREATE TABLE `order` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,                        -- 外键，关联user表
    product VARCHAR(100),
    amount DECIMAL(10,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 内联查（INNER JOIN）：只返回两表都匹配的数据
SELECT u.name, o.product, o.amount
FROM user u
INNER JOIN `order` o ON u.id = o.user_id;

-- 左联查（LEFT JOIN）：返回左表所有数据，右表匹配不到填NULL
SELECT u.name, o.product, o.amount
FROM user u
LEFT JOIN `order` o ON u.id = o.user_id;
```

### 练习：在MySQL中创建user表，插入3条数据，然后查询年龄大于20的用户。

<details>
<summary>参考答案</summary>

```sql
USE demo_db;

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT DEFAULT 0,
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user (name, age, email) VALUES
('张三', 20, 'zhangsan@test.com'),
('李四', 22, 'lisi@test.com'),
('王五', 19, 'wangwu@test.com');

SELECT * FROM user WHERE age > 20;
-- 结果：李四，22岁
```

</details>

---

## 2. MyBatis-Plus（操作数据库）

MyBatis-Plus是MyBatis的增强版，不用写SQL就能完成基本CRUD。

### 引入依赖（pom.xml）

```xml
<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>

<!-- MySQL驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok（之前已加） -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

### 配置数据库连接（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
    username: root
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印SQL（开发时用）
```

### 实体类映射数据库表

```java
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")  // 映射数据库表名
public class User {
    @TableId(type = IdType.AUTO_INCREMENT)  // 主键自增
    private Integer id;
    private String name;
    private Integer age;
    private String email;
    private LocalDateTime createTime;
}
```

### Mapper接口

```java
package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;

// 继承BaseMapper，自动拥有CRUD方法，一行SQL都不用写
public interface UserMapper extends BaseMapper<User> {
}
```

**别忘了在启动类上加 `@MapperScan`：**
```java
@SpringBootApplication
@MapperScan("com.example.demo.mapper")  // 扫描Mapper接口
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### Service层使用Mapper

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;  // 注入Mapper

    // 新增
    public User create(User user) {
        userMapper.insert(user);  // MyBatis-Plus自动插入，id自动生成
        return user;
    }

    // 根据ID查询
    public User getById(int id) {
        return userMapper.selectById(id);
    }

    // 查询所有
    public List<User> list() {
        return userMapper.selectList(null);  // null表示无条件，查所有
    }

    // 更新
    public User update(User user) {
        userMapper.updateById(user);  // 只更新非null字段
        return user;
    }

    // 删除
    public void delete(int id) {
        userMapper.deleteById(id);
    }
}
```

### 条件查询（QueryWrapper）

```java
// 查询年龄大于20的用户
public List<User> getByAgeGreaterThan(int age) {
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.gt("age", age);  // gt = greater than
    return userMapper.selectList(wrapper);
}

// 查询名字包含"张"的用户
public List<User> searchByName(String name) {
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.like("name", name);  // LIKE '%张%'
    return userMapper.selectList(wrapper);
}

// 多条件查询：年龄>20 且 名字包含"张"
public List<User> complexQuery(int age, String name) {
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.gt("age", age)
           .like("name", name);
    return userMapper.selectList(wrapper);
}

// 排序
public List<User> listByAgeDesc() {
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.orderByDesc("age");
    return userMapper.selectList(wrapper);
}
```

**常用QueryWrapper方法：**

| 方法 | SQL | 说明 |
|------|-----|------|
| eq("col", val) | col = val | 等于 |
| ne("col", val) | col != val | 不等于 |
| gt("col", val) | col > val | 大于 |
| lt("col", val) | col < val | 小于 |
| ge("col", val) | col >= val | 大于等于 |
| le("col", val) | col <= val | 小于等于 |
| like("col", val) | col LIKE '%val%' | 模糊查询 |
| in("col", list) | col IN (...) | 在列表中 |
| orderByDesc("col") | ORDER BY col DESC | 降序 |
| orderByAsc("col") | ORDER BY col ASC | 升序 |

### LambdaQueryWrapper（推荐！避免写错列名）

```java
// 用Lambda，列名写错编译就报错
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.gt(User::getAge, 20)
       .like(User::getName, "张");
return userMapper.selectList(wrapper);
```

### 分页查询

```java
// 1. 配置分页插件
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
```

```java
// 2. 使用分页
public IPage<User> page(int pageNum, int pageSize) {
    Page<User> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.orderByDesc(User::getCreateTime);
    return userMapper.selectPage(page, wrapper);
}
```

返回结果：
```json
{
    "records": [...],    // 当前页数据
    "total": 100,        // 总记录数
    "size": 10,          // 每页大小
    "current": 1,        // 当前页码
    "pages": 10          // 总页数
}
```

### 练习：给UserService添加条件查询和分页功能。

<details>
<summary>参考答案</summary>

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User create(User user) {
        userMapper.insert(user);
        return user;
    }

    public User getById(int id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    public List<User> list() {
        return userMapper.selectList(null);
    }

    public User update(User user) {
        getById(user.getId());  // 先检查是否存在
        userMapper.updateById(user);
        return user;
    }

    public void delete(int id) {
        getById(id);
        userMapper.deleteById(id);
    }

    // 条件查询
    public List<User> search(String name, Integer minAge, Integer maxAge) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(User::getName, name);
        }
        if (minAge != null) {
            wrapper.ge(User::getAge, minAge);
        }
        if (maxAge != null) {
            wrapper.le(User::getAge, maxAge);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectList(wrapper);
    }

    // 分页
    public IPage<User> page(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null);
    }
}
```

**Controller对应接口：**
```java
@GetMapping("/search")
public Result<List<User>> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer minAge,
        @RequestParam(required = false) Integer maxAge) {
    return Result.success(userService.search(name, minAge, maxAge));
}

@GetMapping("/page")
public Result<IPage<User>> page(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize) {
    return Result.success(userService.page(pageNum, pageSize));
}
```

</details>

---

## 3. 参数校验（@Valid）

用户传的参数不能直接用，要先验证。

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 实体类加校验注解

```java
@Data
public class UserCreateDTO {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度2-20")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

**常用校验注解：**

| 注解 | 说明 |
|------|------|
| @NotBlank | 字符串不能为空且不能纯空格 |
| @NotNull | 不能为null |
| @NotEmpty | 集合/字符串不能为空 |
| @Size(min, max) | 长度范围 |
| @Min / @Max | 数值范围 |
| @Email | 邮箱格式 |
| @Pattern | 正则匹配 |

### Controller中使用

```java
@PostMapping
public Result<User> create(@RequestBody @Valid UserCreateDTO dto) {
    User user = new User();
    BeanUtils.copyProperties(dto, user);
    return Result.success(userService.create(user));
}
```

**@Valid加在@RequestBody前面，校验不通过会自动抛异常。**

### 在全局异常处理中捕获校验异常

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<String> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return Result.error(400, message);
}
```

### 练习：给创建用户的接口加上参数校验。

<details>
<summary>参考答案</summary>

```java
// UserCreateDTO.java
@Data
public class UserCreateDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;

    @Email(message = "邮箱格式不正确")
    private String email;
}

// UserController.java
@PostMapping
public Result<User> create(@RequestBody @Valid UserCreateDTO dto) {
    User user = new User();
    BeanUtils.copyProperties(dto, user);  // DTO转Entity
    return Result.success(userService.create(user));
}
```

测试：POST /user，Body: `{"name":"","age":20}` → `{"code":400,"message":"姓名不能为空"}`

</details>

---

## 4. 接口文档（Knife4j/Swagger）

开发完接口要给前端看文档，用Knife4j自动生成。

### 引入依赖

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>
```

### 配置（application.yml）

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs

knife4j:
  enable: true
```

### 使用

浏览器访问 `http://localhost:8080/doc.html`，看到接口文档页面。

给接口加描述注解：
```java
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable @Parameter(description = "用户ID") int id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> create(@RequestBody @Valid UserCreateDTO dto) {
        // ...
    }
}
```

---

## 5. 自动填充（createTime/updateTime）

数据库表一般有create_time和update_time字段，让MyBatis-Plus自动填充。

### 实体类

```java
@Data
public class User {
    @TableId(type = IdType.AUTO_INCREMENT)
    private Integer id;
    private String name;
    private Integer age;
    private String email;

    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时都填充
    private LocalDateTime updateTime;
}
```

### 配置自动填充处理器

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

---

## 6. 综合练习：商品管理系统

实现一个完整的商品CRUD：
- POST /product — 创建商品
- GET /product/{id} — 查询单个
- GET /product/list — 查询所有
- GET /product/search?name=xxx&minPrice=10&maxPrice=100 — 条件查询
- GET /product/page?pageNum=1&pageSize=10 — 分页
- PUT /product — 更新
- DELETE /product/{id} — 删除

商品字段：id、name、price、stock（库存）、category（分类）、createTime、updateTime

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

**Product.java**
```java
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO_INCREMENT)
    private Integer id;

    @NotBlank(message = "商品名不能为空")
    private String name;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负")
    private Integer stock;

    private String category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

**建表SQL：**
```sql
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    category VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**ProductMapper.java**
```java
public interface ProductMapper extends BaseMapper<Product> {
}
```

**ProductService.java**
```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public Product create(Product product) {
        productMapper.insert(product);
        return product;
    }

    public Product getById(int id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    public List<Product> list() {
        return productMapper.selectList(null);
    }

    public List<Product> search(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectList(wrapper);
    }

    public IPage<Product> page(int pageNum, int pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productMapper.selectPage(page, null);
    }

    public Product update(Product product) {
        getById(product.getId());
        productMapper.updateById(product);
        return product;
    }

    public void delete(int id) {
        getById(id);
        productMapper.deleteById(id);
    }
}
```

**ProductController.java**
```java
@Tag(name = "商品管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "创建商品")
    @PostMapping
    public Result<Product> create(@RequestBody @Valid Product product) {
        return Result.success(productService.create(product));
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable int id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "查询所有")
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    @Operation(summary = "条件查询")
    @GetMapping("/search")
    public Result<List<Product>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return Result.success(productService.search(name, minPrice, maxPrice));
    }

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<IPage<Product>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.page(pageNum, pageSize));
    }

    @Operation(summary = "更新商品")
    @PutMapping
    public Result<Product> update(@RequestBody @Valid Product product) {
        return Result.success(productService.update(product));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable int id) {
        productService.delete(id);
        return Result.success();
    }
}
```

</details>

---

## Week 3 自检

- [ ] 会写基本的SQL（增删改查、联表查询）
- [ ] 能配置MyBatis-Plus并使用BaseMapper的CRUD方法
- [ ] 会用QueryWrapper/LambdaQueryWrapper做条件查询
- [ ] 能实现分页查询
- [ ] 能用@Valid做参数校验
- [ ] 会配置Knife4j接口文档
- [ ] 能独立完成商品管理系统的全部接口
