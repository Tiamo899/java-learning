# Phase 4：八股文与算法

项目做完后，用4周时间准备面试八股文和算法。每天1小时八股文 + 1-2道算法题。

---

## 八股文速查表

### Java基础

**HashMap底层原理**
- 数组 + 链表 + 红黑树（JDK8+）
- 默认容量16，负载因子0.75，扩容2倍
- put过程：计算hash → 定位桶 → 桶为空直接放 → 桶不为空遍历链表/红黑树 → key相同覆盖 → 不同则尾插 → 链表长度>=8转红黑树
- 线程不安全，多线程用ConcurrentHashMap

**ArrayList vs LinkedList**
- ArrayList：数组，随机访问O(1)，增删O(n)
- LinkedList：链表，随机访问O(n)，头尾增删O(1)
- 99%场景用ArrayList

**String为什么不可变**
- final修饰的char数组（JDK8是char[]，JDK9+是byte[]）
- 不可变 = 线程安全 + 可以缓存hashCode + 字符串常量池

**==和equals区别**
- == 比较基本类型的值或引用类型的地址
- equals比较内容（String重写了equals）

**重载和重写区别**
- 重载（Overload）：同类，方法名相同，参数不同
- 重写（Override）：父子类，方法签名相同，子类覆盖父类

**接口和抽象类区别**
- 接口：纯规范，多实现，只有抽象方法
- 抽象类：可以有普通方法和属性，单继承

---

### Java并发

**线程创建方式**
1. 继承Thread
2. 实现Runnable
3. 实现Callable（有返回值）
4. 线程池（推荐）

**线程池参数（面试高频）**
```java
new ThreadPoolExecutor(
    corePoolSize,      // 核心线程数
    maximumPoolSize,   // 最大线程数
    keepAliveTime,     // 空闲线程存活时间
    TimeUnit,          // 时间单位
    workQueue,         // 任务队列
    threadFactory,     // 线程工厂
    handler            // 拒绝策略
);
```

**四种拒绝策略：**
- AbortPolicy：抛异常（默认）
- CallerRunsPolicy：调用者执行
- DiscardPolicy：丢弃任务
- DiscardOldestPolicy：丢弃最旧任务

**synchronized和ReentrantLock区别**
- synchronized：JVM内置，自动释放，不可中断
- ReentrantLock：API层面，手动释放，可中断，可设置超时，可公平锁

**volatile作用**
- 保证可见性（一个线程修改，其他线程立即可见）
- 禁止指令重排
- 不保证原子性

---

### JVM

**内存模型**
- 堆（Heap）：对象实例，GC主要区域
- 栈（Stack）：局部变量、方法调用
- 方法区（Method Area）：类信息、常量、静态变量
- 程序计数器：当前线程执行的字节码行号

**GC算法**
- 标记-清除：标记垃圾后清除，会产生碎片
- 标记-整理：标记后把存活对象移到一端
- 复制：把内存分两块，存活对象复制到另一块
- 分代收集：新生代用复制，老年代用标记-清除/整理

**垃圾收集器**
- Serial：单线程，Client模式默认
- Parallel：多线程，Server模式默认
- CMS：低停顿，已废弃
- G1：分区收集，JDK9+默认
- ZGC：超低停顿，JDK15+稳定

---

### MySQL

**索引原理**
- B+树结构，叶子节点存储数据
- 非叶子节点只存索引值，不存数据
- 叶子节点用双向链表连接，支持范围查询

**索引失效的情况**
- 对索引列使用函数或运算
- LIKE以%开头
- OR条件中有非索引列
- 类型隐式转换
- 不满足最左前缀原则（联合索引）

**事务隔离级别**
| 级别 | 脏读 | 不可重复读 | 幻读 |
|------|------|-----------|------|
| READ UNCOMMITTED | 有 | 有 | 有 |
| READ COMMITTED | 无 | 有 | 有 |
| REPEATABLE READ（默认） | 无 | 无 | 有* |
| SERIALIZABLE | 无 | 无 | 无 |

*InnoDB的RR级别通过MVCC+间隙锁解决了大部分幻读

**慢查询优化**
1. 开启慢查询日志
2. 用EXPLAIN分析执行计划
3. 看type列：ALL(全表扫描) → index → range → ref → const
4. 优化：加合适索引、避免SELECT *、减少子查询、分页优化

---

### Redis

**五种数据结构及使用场景**
| 结构 | 场景 |
|------|------|
| String | 缓存、计数器、分布式锁 |
| Hash | 对象属性存储 |
| List | 消息队列、最新列表 |
| Set | 去重、共同关注、抽奖 |
| ZSet | 排行榜、延迟队列 |

**缓存三大问题**
- 缓存穿透：查询不存在的数据，每次都打到数据库。解决：布隆过滤器、缓存空值
- 缓存击穿：热点key过期，大量请求打到数据库。解决：互斥锁、永不过期
- 缓存雪崩：大量key同时过期。解决：过期时间加随机值、多级缓存

**Redis为什么快**
- 纯内存操作
- 单线程避免上下文切换
- IO多路复用
- 高效数据结构

---

### Spring

**IoC（控制反转）**
- 对象的创建和管理交给Spring容器
- 通过依赖注入（DI）把对象注入到需要的地方
- 好处：解耦、方便测试、统一管理

**AOP（面向切面编程）**
- 在不修改原代码的情况下给方法加功能
- 底层：JDK动态代理（接口）或CGLIB（类）
- 应用：日志、权限、事务、缓存

**Bean生命周期**
1. 实例化（反射创建对象）
2. 属性赋值（依赖注入）
3. 初始化（@PostConstruct、InitializingBean）
4. 使用
5. 销毁（@PreDestroy、DisposableBean）

**Bean作用域**
- singleton（默认）：单例
- prototype：每次获取都创建新对象
- request：每个HTTP请求一个
- session：每个会话一个

**@Autowired和@Resource区别**
- @Autowired：Spring注解，按类型注入
- @Resource：JDK注解，按名称注入

---

### Spring Boot

**自动配置原理**
1. @SpringBootApplication = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
2. @EnableAutoConfiguration通过spring.factories加载自动配置类
3. 配置类通过@Conditional系列注解判断是否生效

**starter是什么**
- 一组依赖的集合 + 自动配置
- 引入starter就自动配置好相关组件
- 例：spring-boot-starter-web自动配置Tomcat、Spring MVC

---

## 算法准备

### 刷题策略

- 目标：LeetCode Hot 100中的Easy和Medium，共刷30-50道
- 每天1-2道，坚持比一次性刷完更重要
- 先自己想10-15分钟，想不出来看题解，理解后自己写一遍

### 必刷题型

#### 数组（最基础）

| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 1 | 两数之和 | Easy | HashMap |
| 26 | 删除有序数组中的重复项 | Easy | 双指针 |
| 53 | 最大子数组和 | Medium | 动态规划 |
| 121 | 买卖股票的最佳时机 | Easy | 贪心 |
| 283 | 移动零 | Easy | 双指针 |

#### 字符串

| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 3 | 无重复字符的最长子串 | Medium | 滑动窗口 |
| 20 | 有效的括号 | Easy | 栈 |
| 125 | 验证回文串 | Easy | 双指针 |

#### 链表

| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 206 | 反转链表 | Easy | 迭代/递归 |
| 21 | 合并两个有序链表 | Easy | 递归 |
| 141 | 环形链表 | Easy | 快慢指针 |
| 142 | 环形链表II | Medium | 快慢指针 |

#### 二叉树

| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 94 | 二叉树的中序遍历 | Easy | 递归/迭代 |
| 104 | 二叉树的最大深度 | Easy | 递归/层序遍历 |
| 102 | 二叉树的层序遍历 | Medium | BFS |
| 226 | 翻转二叉树 | Easy | 递归 |

#### 动态规划

| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 70 | 爬楼梯 | Easy | 入门 |
| 198 | 打家劫舍 | Medium | 状态转移 |
| 322 | 零钱兑换 | Medium | 完全背包 |
| 300 | 最长递增子序列 | Medium | 经典DP |

#### 排序算法（手写）

```java
// 冒泡排序
public void bubbleSort(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        for (int j = 0; j < arr.length - 1 - i; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

// 快速排序
public void quickSort(int[] arr, int left, int right) {
    if (left >= right) return;
    int pivot = arr[left];
    int i = left, j = right;
    while (i < j) {
        while (i < j && arr[j] >= pivot) j--;
        while (i < j && arr[i] <= pivot) i++;
        if (i < j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    arr[left] = arr[i];
    arr[i] = pivot;
    quickSort(arr, left, i - 1);
    quickSort(arr, i + 1, right);
}

// 二分查找
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}
```

---

## 面试高频问题

**自我介绍模板**
> 面试官好，我叫XXX，来自XXX大学软件工程专业。我熟悉Java基础、Spring Boot框架和MySQL数据库。独立开发了一个个人博客系统，使用Spring Boot + MyBatis-Plus + Redis + JWT实现，包含用户认证、文章管理、评论互动等功能。我对技术有热情，学习能力强，希望能加入贵公司实习。

**为什么选择Java？**
> Java生态成熟，企业级应用广泛，Spring框架让开发效率很高。Java的强类型系统和丰富的工具链让我能写出更可靠的代码。

**项目中遇到的最大挑战？**
> （提前准备一个真实的故事，比如：JWT token过期处理、分页查询性能优化、Redis缓存一致性等）

**你有什么问题想问我们？**
> - 团队的技术栈是什么？
> - 实习生会参与什么项目？
> - 公司对实习生有什么期望？
