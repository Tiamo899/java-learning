# Java Day 1：语法基础

## 1. 第一个程序

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**逐行解释：**
- `public class Hello` — 定义一个类，类名必须和文件名一样（Hello.java）
- `public static void main(String[] args)` — 程序入口，固定写法
- `System.out.println("xxx")` — 打印一行文字并换行
- `System.out.print("xxx")` — 打印文字但不换行

**运行：**
```
javac Hello.java    // 编译
java Hello          // 运行
```

---

## 2. 变量和数据类型

| 类型 | 说明 | 例子 |
|------|------|------|
| `int` | 整数 | `int age = 20;` |
| `double` | 小数（双精度） | `double score = 98.5;` |
| `char` | 单个字符 | `char gender = '男';` |
| `boolean` | true/false | `boolean isStudent = true;` |
| `long` | 大整数（后面加L） | `long id = 123456789L;` |
| `float` | 小数（后面加f） | `float price = 9.9f;` |
| `String` | 字符串 | `String name = "闻康";` |

```java
// 声明同时赋值
int age = 20;
String name = "闻康";

// 先声明后赋值
double score;
score = 98.5;
```

**命名规则：** 字母/数字/下划线/$，不能数字开头，驼峰命名 `studentName`

---

## 3. 运算符

```java
int a = 10, b = 3;
a + b   // 13    加
a - b   // 7     减
a * b   // 30    乘
a / b   // 3     整数除法丢小数！
a % b   // 1     取余

// 想要小数结果
10.0 / 3        // 3.3333...
(double) a / b  // 强制转换
```

**自增自减：**
```java
int a = 5;
int b = a++;  // b=5, a=6（先赋值再自增）
int c = ++a;  // c=7, a=7（先自增再赋值）
```

**逻辑运算符：**
```java
true && false  // false，与（都true才true）
true || false  // true，或（有一个true就true）
!true          // false，非（取反）
```

---

## 4. 条件判断

```java
int score = 85;

if (score >= 90) {
    System.out.println("优秀");
} else if (score >= 80) {
    System.out.println("良好");
} else if (score >= 60) {
    System.out.println("及格");
} else {
    System.out.println("不及格");
}
```

**三元运算符：**
```java
String result = (age >= 18) ? "成年" : "未成年";
```

---

## 5. 循环

```java
// for循环
for (int i = 1; i <= 10; i++) {
    System.out.println(i);
}

// while循环
int i = 1;
while (i <= 10) {
    System.out.println(i);
    i++;
}

// break：跳出循环    continue：跳过本次
```

---

## 6. 数组

```java
int[] scores = {90, 85, 78, 92, 88};

// for循环遍历
for (int i = 0; i < scores.length; i++) {
    System.out.println(scores[i]);
}

// for-each遍历
for (int score : scores) {
    System.out.println(score);
}
```

---

## 7. 方法

```java
// 定义
public static int add(int a, int b) {
    return a + b;
}

// 调用
int result = add(3, 5);  // 8

// 无返回值
public static void sayHello(String name) {
    System.out.println("你好，" + name);
}
```

---

## Day 1 练习

1. Hello World — 编译运行
2. 计算器 — 输入两个数，输出加减乘除
3. 成绩等级 — 输入分数，输出优秀/良好/及格/不及格
4. 九九乘法表
5. 数组求最大值
6. 求平均分 — 输入5个成绩，输出最高、最低、平均
