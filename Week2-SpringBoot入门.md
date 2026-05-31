# Week 2：Spring Boot入门

Spring Boot是Java后端开发的核心框架。不用像以前Spring那样写一堆XML配置，Spring Boot"约定大于配置"，几行代码就能启动一个Web服务。

---

## 1. 创建Spring Boot项目

### 方式1：IDEA直接创建（推荐）

1. 打开IDEA → New Project → Spring Initializr
2. 填写：
   - Name: `my-first-app`
   - Language: Java
   - Type: Maven
   - Package name: `com.example.demo`
   - Java: 17（或21）
   - Packaging: Jar
3. Dependencies（依赖）勾选：
   - Spring Web（Web开发必备）
   - Lombok（简化代码）
4. 点Create

### 方式2：网页生成

1. 打开 https://start.spring.io
2. 填写信息，选依赖，点Generate下载
3. 用IDEA打开下载的项目

### 项目结构

```
my-first-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   └── DemoApplication.java    ← 启动类
│   │   └── resources/
│   │       ├── application.yml          ← 配置文件
│   │       ├── static/                  ← 静态资源
│   │       └── templates/               ← 模板
│   └── test/                            ← 测试
├── pom.xml                              ← Maven依赖配置
└── README.md
```

### 启动类

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // 核心注解，标记这是一个Spring Boot应用
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

右键运行这个类的main方法，看到 `Started DemoApplication in X seconds` 就成功了。默认端口8080，浏览器访问 `http://localhost:8080`。

### 配置文件 application.yml

```yaml
server:
  port: 8080  # 改端口

spring:
  application:
    name: my-first-app  # 应用名称
```

**yml vs properties：** 两种格式都能用，yml更简洁，推荐用yml。

### 练习：创建一个Spring Boot项目，改端口为9090，启动后浏览器能访问。

<details>
<summary>参考答案</summary>

1. 用IDEA或start.spring.io创建项目
2. 修改 `application.yml`：

```yaml
server:
  port: 9090
```

3. 运行启动类
4. 浏览器访问 `http://localhost:9090`，会看到Whitelabel Error Page（正常，因为还没写接口）

</details>

---

## 2. 第一个接口（Controller）

### Hello World

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // 标记这是一个控制器，返回JSON而不是页面
public class HelloController {

    @GetMapping("/hello")  // 处理GET请求，路径是/hello
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

启动项目，浏览器访问 `http://localhost:8080/hello`，看到 `Hello, Spring Boot!`。

### 常用注解

```java
@RestController          // = @Controller + @ResponseBody（返回JSON）
@Controller              // 返回页面（传统Web开发用）
@GetMapping("/path")     // 处理GET请求
@PostMapping("/path")    // 处理POST请求
@PutMapping("/path")     // 处理PUT请求
@DeleteMapping("/path")  // 处理DELETE请求
@RequestMapping("/path") // 通用映射，可指定method
```

### 接收参数

```java
@RestController
public class UserController {

    // 方式1：路径参数 /user/123
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable int id) {
        return "查询用户：" + id;
    }

    // 方式2：查询参数 /user/search?name=张三
    @GetMapping("/user/search")
    public String searchUser(@RequestParam String name) {
        return "搜索用户：" + name;
    }

    // 方式3：查询参数有默认值 /user/search（不传name时用默认值）
    @GetMapping("/user/search2")
    public String searchUser2(@RequestParam(defaultValue = "匿名") String name) {
        return "搜索用户：" + name;
    }

    // 方式4：接收JSON请求体（POST/PUT用）
    @PostMapping("/user")
    public String createUser(@RequestBody User user) {
        return "创建用户：" + user.getName() + "，年龄：" + user.getAge();
    }
}
```

### User类（实体类）

```java
package com.example.demo.entity;

import lombok.Data;  // Lombok注解，自动生成getter/setter/toString

@Data
public class User {
    private int id;
    private String name;
    private int age;
    private String email;
}
```

**Lombok的@Data：** 加了这个注解，就不用手写getter/setter/toString/equals/hashCode了。编译时自动生成。

### 练习：写一个UserController，实现以下接口：
- GET /user/hello → 返回"用户服务已启动"
- GET /user/{id} → 返回"查询第{id}号用户"
- GET /user/list?name=xxx → 返回"查询名字包含{xxx}的用户"
- POST /user → 接收User对象，返回"用户{name}创建成功"

<details>
<summary>参考答案</summary>

```java
package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")  // 类级别的路径前缀，下面所有接口都以/user开头
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "用户服务已启动";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id) {
        return "查询第" + id + "号用户";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "") String name) {
        return "查询名字包含" + name + "的用户";
    }

    @PostMapping
    public String create(@RequestBody User user) {
        return "用户" + user.getName() + "创建成功";
    }
}
```

**注意 `@RequestMapping("/user")`：** 在类上加这个注解，所有接口路径都自动加前缀 `/user`。比如 `@GetMapping("/hello")` 实际路径是 `/user/hello`。

**测试方式：**
- 浏览器直接访问GET接口
- POST接口用Postman或Apifox测试
- 或者在IDEA里装HTTP Client插件

</details>

---

## 3. 三层架构

实际开发中代码不是全写在Controller里的，而是分三层：

```
请求 → Controller → Service → Mapper → 数据库
响应 ← Controller ← Service ← Mapper ← 数据库
```

| 层 | 职责 | 命名规范 |
|----|------|----------|
| Controller | 接收请求、返回响应 | XxxController |
| Service | 业务逻辑 | XxxService |
| Mapper | 操作数据库 | XxxMapper |

### 依赖注入（IoC）

三层之间怎么传递对象？用Spring的依赖注入。核心思想：**你不用自己new对象，Spring帮你创建和管理。**

```java
// Service层
@Service  // 标记为Service，Spring会自动创建这个对象
public class UserService {

    public String getUserName(int id) {
        // 以后这里会调Mapper查数据库，现在先写死
        if (id == 1) return "张三";
        if (id == 2) return "李四";
        return "未知用户";
    }

    public String createUser(User user) {
        // 以后这里会调Mapper插入数据库
        return "用户" + user.getName() + "创建成功";
    }
}
```

```java
// Controller层
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired  // 自动注入Spring管理的UserService对象
    private UserService userService;

    @GetMapping("/{id}")
    public String getById(@PathVariable int id) {
        return userService.getUserName(id);
    }

    @PostMapping
    public String create(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

**注入方式有三种（面试常考）：**

```java
// 方式1：字段注入（最简单，但不推荐）
@Autowired
private UserService userService;

// 方式2：构造器注入（推荐！）
private final UserService userService;

public UserController(UserService userService) {
    this.userService = userService;
}

// 方式3：Lombok简化构造器注入
@RequiredArgsConstructor  // 自动生成包含final字段的构造器
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
}
```

**为什么推荐构造器注入：**
- 可以用final修饰，保证不可变
- 编译时就能发现缺少依赖
- 方便单元测试（可以手动传入mock对象）

### 练习：把之前的UserController改成三层架构，Controller调Service，Service里写业务逻辑。

<details>
<summary>参考答案</summary>

**UserService.java**
```java
package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    // 模拟数据库，用内存中的List存数据
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    public String getUserName(int id) {
        return users.stream()
            .filter(u -> u.getId() == id)
            .map(User::getName)
            .findFirst()
            .orElse("未知用户");
    }

    public List<User> listUsers(String name) {
        if (name == null || name.isEmpty()) {
            return users;
        }
        return users.stream()
            .filter(u -> u.getName().contains(name))
            .toList();
    }

    public String createUser(User user) {
        user.setId(nextId++);
        users.add(user);
        return "用户" + user.getName() + "创建成功，ID=" + user.getId();
    }
}
```

**UserController.java**
```java
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String getById(@PathVariable int id) {
        return userService.getUserName(id);
    }

    @GetMapping("/list")
    public List<User> list(@RequestParam(defaultValue = "") String name) {
        return userService.listUsers(name);
    }

    @PostMapping
    public String create(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

**测试：**
1. POST `http://localhost:8080/user`，Body: `{"name":"张三","age":20}` → "用户张三创建成功，ID=1"
2. GET `http://localhost:8080/user/1` → "张三"
3. GET `http://localhost:8080/user/list` → [{"id":1,"name":"张三","age":20}]

</details>

---

## 4. 统一返回结果

实际开发中，接口返回的数据都有固定格式，方便前端处理。

### 定义统一结果类

```java
package com.example.demo.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;       // 状态码：200成功，其他失败
    private String message; // 提示信息
    private T data;         // 数据

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
```

### Controller返回统一结果

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<String> getById(@PathVariable int id) {
        String name = userService.getUserName(id);
        return Result.success(name);
    }

    @PostMapping
    public Result<String> create(@RequestBody User user) {
        String msg = userService.createUser(user);
        return Result.success(msg);
    }
}
```

返回的JSON：
```json
{
    "code": 200,
    "message": "success",
    "data": "张三"
}
```

### 练习：给之前的UserController加上统一返回结果。

<details>
<summary>参考答案</summary>

```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<String> getById(@PathVariable int id) {
        return Result.success(userService.getUserName(id));
    }

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(defaultValue = "") String name) {
        return Result.success(userService.listUsers(name));
    }

    @PostMapping
    public Result<String> create(@RequestBody User user) {
        return Result.success(userService.createUser(user));
    }
}
```

</details>

---

## 5. 全局异常处理

如果接口抛异常了，不能把一堆错误信息直接返回给前端。要统一处理。

```java
package com.example.demo.exception;

import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // 全局异常处理
public class GlobalExceptionHandler {

    // 处理所有RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();  // 打印日志
        return Result.error(e.getMessage());
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("服务器内部错误");
    }
}
```

### 自定义业务异常

```java
package com.example.demo.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

```java
// 在全局异常处理中添加
@ExceptionHandler(BusinessException.class)
public Result<String> handleBusinessException(BusinessException e) {
    return Result.error(e.getCode(), e.getMessage());
}
```

```java
// Service中使用
public String getUserName(int id) {
    String name = ...;
    if (name == null) {
        throw new BusinessException(404, "用户不存在");
    }
    return name;
}
```

### 练习：添加全局异常处理，并在Service中使用自定义异常。

<details>
<summary>参考答案</summary>

```java
// UserService.java 中添加
public String getUserName(int id) {
    return users.stream()
        .filter(u -> u.getId() == id)
        .map(User::getName)
        .findFirst()
        .orElseThrow(() -> new BusinessException(404, "用户不存在，ID=" + id));
}
```

现在访问一个不存在的用户ID，返回：
```json
{
    "code": 404,
    "message": "用户不存在，ID=999",
    "data": null
}
```

而不是一堆Java异常堆栈信息。

</details>

---

## 6. 综合练习：完整用户管理接口

基于三层架构，实现完整的用户CRUD：
- POST /user — 创建用户
- GET /user/{id} — 根据ID查询
- GET /user/list — 查询所有
- PUT /user — 更新用户
- DELETE /user/{id} — 删除用户

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

**User.java**（实体类）
```java
@Data
public class User {
    private int id;
    private String name;
    private int age;
    private String email;
}
```

**Result.java**（统一结果，用上面的）

**BusinessException.java**（自定义异常，用上面的）

**GlobalExceptionHandler.java**（全局异常处理，用上面的）

**UserService.java**
```java
@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    public User create(User user) {
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    public User getById(int id) {
        return users.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    public List<User> list() {
        return users;
    }

    public User update(User user) {
        User existing = getById(user.getId());
        existing.setName(user.getName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());
        return existing;
    }

    public void delete(int id) {
        boolean removed = users.removeIf(u -> u.getId() == id);
        if (!removed) {
            throw new BusinessException(404, "用户不存在");
        }
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

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        return Result.success(userService.create(user));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable int id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }

    @PutMapping
    public Result<User> update(@RequestBody User user) {
        return Result.success(userService.update(user));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable int id) {
        userService.delete(id);
        return Result.success();
    }
}
```

**测试用Apifox/Postman：**
```
POST http://localhost:8080/user
Body: {"name":"张三","age":20,"email":"zhangsan@test.com"}
→ {"code":200,"message":"success","data":{"id":1,"name":"张三","age":20,"email":"zhangsan@test.com"}}

GET http://localhost:8080/user/1
→ {"code":200,"message":"success","data":{"id":1,"name":"张三","age":20,"email":"zhangsan@test.com"}}

PUT http://localhost:8080/user
Body: {"id":1,"name":"张三丰","age":21,"email":"zsf@test.com"}
→ {"code":200,"message":"success","data":{"id":1,"name":"张三丰","age":21,"email":"zsf@test.com"}}

DELETE http://localhost:8080/user/1
→ {"code":200,"message":"success","data":null}
```

</details>

---

## Week 2 自检

- [ ] 能独立创建Spring Boot项目并启动
- [ ] 理解@RestController、@GetMapping、@PostMapping等注解
- [ ] 能用@PathVariable、@RequestParam、@RequestBody接收参数
- [ ] 理解三层架构（Controller-Service），能正确分层
- [ ] 理解依赖注入@Autowired，知道构造器注入更优
- [ ] 能写出统一返回结果类Result
- [ ] 能写全局异常处理
- [ ] 能独立完成用户CRUD接口
