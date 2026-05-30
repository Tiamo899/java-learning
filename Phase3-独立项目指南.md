# Phase 3：独立项目指南

前4周学的知识点都是零散的，现在要把它们串成一个完整项目。这是简历上最重要的东西。

---

## 项目选择

三选一，难度递增：

| 项目 | 难度 | 适合 | 亮点 |
|------|------|------|------|
| 个人博客系统 | ★★☆ | 最稳妥的选择 | 功能清晰，容易讲清楚 |
| AI问答助手 | ★★★ | 和毕设相关，可复用 | 接入大模型，有技术亮点 |
| 在线协作文档 | ★★★★ | 想挑战的 | WebSocket实时通信 |

**推荐先做博客系统。** 功能明确，不会做着做着迷失方向。

---

## 博客系统功能清单

### 核心功能（必须做）

| 模块 | 功能 | 涉及技术 |
|------|------|----------|
| 用户 | 注册、登录、获取个人信息、修改信息 | JWT、参数校验 |
| 文章 | 发布、编辑、删除、查询（分页+条件） | MyBatis-Plus、分页 |
| 分类 | 文章分类管理（CRUD） | 关联查询 |
| 评论 | 文章下发表评论 | 一对多关系 |

### 加分功能（做了更好）

| 功能 | 说明 |
|------|------|
| 文章标签 | 多对多关系，一个文章多个标签 |
| 文章点赞/收藏 | Redis计数 |
| 文章搜索 | 按标题/内容模糊搜索 |
| 文件上传 | 头像上传到本地或OSS |
| 浏览量统计 | Redis HyperLogLog或简单计数 |

---

## 数据库设计

### user表

```sql
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(200),
    email VARCHAR(100),
    bio VARCHAR(500) COMMENT '个人简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### article表

```sql
CREATE TABLE article (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(500) COMMENT '摘要',
    cover_image VARCHAR(200) COMMENT '封面图',
    category_id INT COMMENT '分类ID',
    user_id INT NOT NULL COMMENT '作者ID',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '0=草稿 1=已发布',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### category表

```sql
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### comment表

```sql
CREATE TABLE comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL,
    user_id INT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    parent_id INT DEFAULT 0 COMMENT '父评论ID，0=顶级评论',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 项目结构

```
blog-system/
├── src/main/java/com/example/blog/
│   ├── BlogApplication.java           # 启动类
│   ├── common/
│   │   ├── Result.java                # 统一返回
│   │   └── PageResult.java            # 分页返回
│   ├── config/
│   │   ├── MybatisPlusConfig.java     # MP分页插件
│   │   ├── WebConfig.java             # 拦截器配置
│   │   └── RedisConfig.java           # Redis配置
│   ├── entity/
│   │   ├── User.java
│   │   ├── Article.java
│   │   ├── Category.java
│   │   └── Comment.java
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── ArticleMapper.java
│   │   ├── CategoryMapper.java
│   │   └── CommentMapper.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── ArticleService.java
│   │   ├── CategoryService.java
│   │   └── CommentService.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── ArticleController.java
│   │   ├── CategoryController.java
│   │   └── CommentController.java
│   ├── dto/
│   │   ├── LoginDTO.java
│   │   ├── ArticleCreateDTO.java
│   │   └── ArticleQueryDTO.java
│   ├── exception/
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── interceptor/
│   │   └── JwtInterceptor.java
│   └── util/
│       └── JwtUtil.java
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

---

## 开发顺序（重要！）

不要想到哪写到哪，按这个顺序来：

### 第1步：搭建框架（Day 1）
1. 创建Spring Boot项目，引入所有依赖
2. 配置application.yml（MySQL、Redis）
3. 写好Result、BusinessException、GlobalExceptionHandler
4. 写好JwtUtil、JwtInterceptor、WebConfig
5. 启动项目，确认能跑起来

### 第2步：用户模块（Day 1-2）
1. 建user表
2. 写User实体类、UserMapper
3. 写AuthService（注册+登录）
4. 写AuthController
5. 写UserController（个人信息）
6. 测试：注册 → 登录 → 获取个人信息

### 第3步：分类模块（Day 2）
1. 建category表
2. 写Category的实体+Mapper+Service+Controller
3. CRUD接口

### 第4步：文章模块（Day 3-4）
1. 建article表
2. 写Article的实体+Mapper+Service+Controller
3. 实现发布、编辑、删除、分页查询、条件查询
4. 文章和分类的关联查询

### 第5步：评论模块（Day 4-5）
1. 建comment表
2. 写Comment的实体+Mapper+Service+Controller
3. 发表评论、查看文章评论列表
4. 支持回复评论（parent_id）

### 第6步：加分功能（Day 5-7）
- 浏览量统计（Redis计数）
- 文章标签（多对多）
- 文件上传（头像）
- 文章点赞

### 第7步：部署上线（Day 7）
1. 配置prod环境
2. mvn clean package
3. 部署到服务器
4. 测试线上访问

---

## 接口清单（写完对照）

```
# 认证
POST   /auth/register          注册
POST   /auth/login             登录

# 用户
GET    /user/me                获取当前用户信息
PUT    /user/me                更新个人信息
PUT    /user/password          修改密码

# 分类
POST   /category               创建分类
GET    /category/list           分类列表
PUT    /category               更新分类
DELETE /category/{id}           删除分类

# 文章
POST   /article                发布文章
GET    /article/{id}            文章详情
GET    /article/list            文章列表（分页+条件）
PUT    /article                编辑文章
DELETE /article/{id}            删除文章
GET    /article/user/{userId}   某用户的文章

# 评论
POST   /comment                发表评论
GET    /comment/article/{id}    文章评论列表
DELETE /comment/{id}            删除评论
```

---

## 简历上怎么写

### 项目描述（参考）

```
个人博客系统
技术栈：Spring Boot 3 + MyBatis-Plus + MySQL + Redis + JWT + Knife4j
项目描述：一个支持用户注册登录、文章发布管理、分类标签、评论互动的博客系统。
主要工作：
- 使用JWT实现用户认证，拦截器统一校验token
- 使用MyBatis-Plus实现文章分页查询和多条件动态筛选
- 使用Redis缓存热门文章，减少数据库压力
- 使用AOP记录接口调用日志，统计接口耗时
- 使用Knife4j生成API文档，方便前后端联调
```

### 面试时能讲清楚

- 项目整体架构（三层架构）
- 用户认证流程（JWT生成 → 拦截器验证）
- 文章分页查询怎么实现的
- Redis缓存了什么，缓存和数据库怎么保持一致
- 遇到了什么问题，怎么解决的

---

## 常见问题

**Q: 前端怎么办？**
A: 三种选择：
1. 纯后端，用Swagger/Knife4j测试接口（最省时间）
2. 用若依框架的前端模板
3. 自己写Vue前端（时间充裕的话）

**Q: 项目没想法怎么办？**
A: 去GitHub搜"SpringBoot博客"，看别人怎么做的，但不要抄，理解后自己写。

**Q: 做到什么程度算完成？**
A: 核心功能全部能跑通，能部署到服务器，面试时能把每个功能讲清楚。
