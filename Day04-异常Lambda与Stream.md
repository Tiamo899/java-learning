# Java Day 4：异常、Lambda与Stream

今天学三个东西：异常处理（程序出错怎么办）、Lambda（简化写法）、Stream（集合的高级操作）。

---

## 1. 异常处理

程序运行时总会出错，异常处理就是"出错了不要崩溃，优雅地处理"。

### 异常体系（了解即可）

```
Throwable
├── Error（严重错误，不用管）
│   └── OutOfMemoryError、StackOverflowError
└── Exception（异常，需要处理）
    ├── 编译时异常（必须处理）
    │   ├── IOException
    │   ├── SQLException
    │   └── FileNotFoundException
    └── 运行时异常（RuntimeException，可以不处理）
        ├── NullPointerException（空指针）
        ├── ArrayIndexOutOfBoundsException（数组越界）
        ├── NumberFormatException（数字格式错误）
        └── ClassCastException（类型转换错误）
```

### try-catch（捕获异常）

```java
try {
    // 可能出错的代码
    int result = 10 / 0;  // ArithmeticException
    System.out.println(result);
} catch (ArithmeticException e) {
    // 出错了怎么处理
    System.out.println("不能除以零！");
    System.out.println("错误信息：" + e.getMessage());
}
// 程序不会崩溃，继续执行
System.out.println("程序继续运行");
```

### 多个catch

```java
try {
    String s = "abc";
    int num = Integer.parseInt(s);     // NumberFormatException
    int[] arr = new int[3];
    arr[5] = 10;                       // ArrayIndexOutOfBoundsException
} catch (NumberFormatException e) {
    System.out.println("数字格式错误：" + e.getMessage());
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("数组越界：" + e.getMessage());
} catch (Exception e) {
    // 兜底，捕获所有其他异常
    System.out.println("其他错误：" + e.getMessage());
}
```

### finally（无论如何都执行）

```java
try {
    int result = 10 / 2;
    System.out.println(result);
} catch (ArithmeticException e) {
    System.out.println("出错了");
} finally {
    // 无论是否出错，finally里的代码都会执行
    System.out.println("这段代码一定会执行");
    // 常用于：关闭资源、释放连接
}
```

### 实际开发常用写法

```java
// 读文件示例
FileReader reader = null;
try {
    reader = new FileReader("test.txt");
    // 读取文件...
} catch (FileNotFoundException e) {
    System.out.println("文件不存在");
} finally {
    try {
        if (reader != null) reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// 更简洁的写法：try-with-resources（自动关闭资源）
try (FileReader reader2 = new FileReader("test.txt")) {
    // 读取文件，用完自动关闭
} catch (FileNotFoundException e) {
    System.out.println("文件不存在");
} catch (IOException e) {
    System.out.println("读取失败");
}
```

### throws（声明异常）

如果方法里不想处理异常，可以甩给调用者：

```java
public static int divide(int a, int b) throws ArithmeticException {
    return a / b;  // 可能抛出异常，但我不管，谁调用谁处理
}

// 调用者必须处理
public static void main(String[] args) {
    try {
        int result = divide(10, 0);
    } catch (ArithmeticException e) {
        System.out.println("除以零了");
    }
}
```

### 自定义异常（了解）

```java
// 自定义异常类
public class AgeException extends RuntimeException {
    public AgeException(String message) {
        super(message);
    }
}

// 使用
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new AgeException("年龄不合法：" + age);
    }
    this.age = age;
}
```

### 练习：写一个方法，接收字符串参数，将其转为整数。如果转换失败返回-1，如果参数为null返回-2。

<details>
<summary>参考答案</summary>

```java
public class SafeParseInt {
    public static int safeParse(String str) {
        if (str == null) {
            return -2;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println(safeParse("123"));    // 123
        System.out.println(safeParse("abc"));    // -1
        System.out.println(safeParse(null));     // -2
        System.out.println(safeParse("3.14"));   // -1
    }
}
```

</details>

---

## 2. Lambda表达式

Lambda是Java 8引入的语法糖，用来简化"只有一个方法的接口"的写法。

### 从匿名内部类到Lambda

```java
// 传统写法：匿名内部类
Comparator<String> comp = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();
    }
};

// Lambda写法（简化版）
Comparator<String> comp2 = (a, b) -> a.length() - b.length();

// 更简化（只有一个参数时可以省括号）
// 推导过程：
// 1. 去掉接口名和方法名
// 2. 参数类型可以省略（编译器能推断）
// 3. 方法体只有一行时可以省大括号和return
```

### Lambda的格式

```java
// 完整格式
(参数类型 参数名) -> { 方法体; return 返回值; }

// 简化1：参数类型省略
(参数名) -> { 方法体; return 返回值; }

// 简化2：只有一个参数时省括号
参数名 -> { 方法体; return 返回值; }

// 简化3：方法体只有一行时省大括号和return
参数名 -> 表达式
```

### 常见用法

```java
// 1. ArrayList排序
ArrayList<String> list = new ArrayList<>();
list.add("Charlie");
list.add("Alice");
list.add("Bob");

// 传统写法
Collections.sort(list, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// Lambda写法
Collections.sort(list, (a, b) -> a.compareTo(b));

// 更简洁：方法引用
Collections.sort(list, String::compareTo);

// 2. 遍历集合
list.forEach(item -> System.out.println(item));

// 方法引用
list.forEach(System.out::println);

// 3. 线程
// 传统
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("hello");
    }
}).start();

// Lambda
new Thread(() -> System.out.println("hello")).start();
```

### 函数式接口

Lambda只能用在"只有一个抽象方法的接口"上，这种接口叫函数式接口，用 `@FunctionalInterface` 注解标记。

```java
@FunctionalInterface
public interface MyFunction {
    int apply(int a, int b);
}

// 使用Lambda
MyFunction add = (a, b) -> a + b;
MyFunction multiply = (a, b) -> a * b;

System.out.println(add.apply(3, 5));       // 8
System.out.println(multiply.apply(3, 5));  // 15
```

### 练习：用Lambda实现一个计算器接口，支持加减乘除。

<details>
<summary>参考答案</summary>

```java
@FunctionalInterface
interface Calculator2 {
    double calculate(double a, double b);
}

public class LambdaDemo {
    public static void main(String[] args) {
        Calculator2 add = (a, b) -> a + b;
        Calculator2 subtract = (a, b) -> a - b;
        Calculator2 multiply = (a, b) -> a * b;
        Calculator2 divide = (a, b) -> {
            if (b == 0) throw new ArithmeticException("除数不能为0");
            return a / b;
        };

        System.out.println("加：" + add.calculate(10, 3));       // 13.0
        System.out.println("减：" + subtract.calculate(10, 3));  // 7.0
        System.out.println("乘：" + multiply.calculate(10, 3));  // 30.0
        System.out.println("除：" + divide.calculate(10, 3));    // 3.333...
    }
}
```

</details>

---

## 3. Stream流

Stream是Java 8引入的集合操作API，用函数式风格处理集合数据——比for循环简洁得多。

### 创建Stream

```java
// 从集合创建
ArrayList<String> list = new ArrayList<>();
Stream<String> stream = list.stream();

// 从数组创建
int[] arr = {1, 2, 3};
IntStream stream2 = Arrays.stream(arr);

// 直接创建
Stream<Integer> stream3 = Stream.of(1, 2, 3, 4, 5);
```

### 核心操作

```java
ArrayList<Integer> list = new ArrayList<>();
list.add(88);
list.add(95);
list.add(42);
list.add(76);
list.add(60);
list.add(91);
list.add(55);
```

#### filter（过滤）

```java
// 筛选大于80的
list.stream()
    .filter(n -> n > 80)
    .forEach(n -> System.out.println(n));
// 输出：88, 95, 91
```

#### map（转换）

```java
// 每个元素乘以2
list.stream()
    .map(n -> n * 2)
    .forEach(n -> System.out.println(n));
// 输出：176, 190, 84, 152, 120, 182, 110
```

#### sorted（排序）

```java
// 升序
list.stream()
    .sorted()
    .forEach(n -> System.out.println(n));
// 输出：42, 55, 60, 76, 88, 91, 95

// 降序
list.stream()
    .sorted((a, b) -> b - a)
    .forEach(n -> System.out.println(n));
// 输出：95, 91, 88, 76, 60, 55, 42
```

#### distinct（去重）

```java
list.stream()
    .distinct()
    .forEach(n -> System.out.println(n));
```

#### limit / skip（截取）

```java
list.stream()
    .sorted()
    .skip(2)     // 跳过前2个
    .limit(3)    // 只取3个
    .forEach(n -> System.out.println(n));
// 排序后跳过42,55，取60,76,88
```

#### collect（收集结果）

```java
// 收集成List
List<Integer> result = list.stream()
    .filter(n -> n > 80)
    .collect(Collectors.toList());
System.out.println(result);  // [88, 95, 91]

// 收集成Set（自动去重）
Set<Integer> set = list.stream()
    .collect(Collectors.toSet());

// 收集成Map
Map<String, Integer> map = list.stream()
    .collect(Collectors.toMap(
        n -> "分数" + n,  // key
        n -> n             // value
    ));
```

#### 聚合操作

```java
// 求和
int sum = list.stream().mapToInt(n -> n).sum();  // 507

// 平均值
double avg = list.stream().mapToInt(n -> n).average().orElse(0);  // 72.428...

// 最大值
int max = list.stream().mapToInt(n -> n).max().orElse(0);  // 95

// 最小值
int min = list.stream().mapToInt(n -> n).min().orElse(0);  // 42

// 计数
long count = list.stream().filter(n -> n > 80).count();  // 3
```

### 链式操作（Stream的精髓）

```java
// 需求：找出成绩大于80的学生，按成绩降序排列，取前3名的姓名
ArrayList<Student> students = new ArrayList<>();
students.add(new Student("张三", 20, 90));
students.add(new Student("李四", 21, 75));
students.add(new Student("王五", 19, 95));
students.add(new Student("赵六", 22, 88));
students.add(new Student("钱七", 20, 82));
students.add(new Student("孙八", 21, 60));

List<String> top3 = students.stream()
    .filter(s -> s.getScore() > 80)        // 筛选成绩>80
    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))  // 降序
    .limit(3)                               // 取前3
    .map(Student::getName)                  // 只取姓名
    .collect(Collectors.toList());          // 收集成List

System.out.println(top3);  // [王五, 张三, 赵六]
```

### 练习：有以下学生数据，用Stream完成：1）找出所有及格（>=60）的学生；2）按成绩降序排列；3）输出"姓名: 成绩"格式。

<details>
<summary>参考答案</summary>

```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamDemo {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("张三", 20, 90));
        students.add(new Student("李四", 21, 55));
        students.add(new Student("王五", 19, 78));
        students.add(new Student("赵六", 22, 42));
        students.add(new Student("钱七", 20, 88));
        students.add(new Student("孙八", 21, 60));

        // 方式1：直接遍历输出
        System.out.println("=== 及格学生（降序）===");
        students.stream()
            .filter(s -> s.getScore() >= 60)
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .forEach(s -> System.out.println(s.getName() + ": " + s.getScore()));
        // 输出：
        // 张三: 90.0
        // 钱七: 88.0
        // 王五: 78.0
        // 孙八: 60.0

        // 方式2：收集成格式化字符串List
        List<String> result = students.stream()
            .filter(s -> s.getScore() >= 60)
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .map(s -> s.getName() + ": " + s.getScore())
            .collect(Collectors.toList());

        System.out.println(result);
        // [张三: 90.0, 钱七: 88.0, 王五: 78.0, 孙八: 60.0]
    }
}
```

</details>

---

## 4. 综合练习：学生成绩分析系统

输入若干学生的姓名和成绩（用Scanner），然后用Stream实现：
1. 输出所有学生（按成绩降序）
2. 输出最高分、最低分、平均分
3. 输出及格和不及格的学生名单
4. 按成绩等级分组（90+优秀、80+良好、60+及格、<60不及格）

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

```java
import java.util.*;
import java.util.stream.Collectors;

public class ScoreAnalysis {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("输入学生数量：");
        int n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.print("姓名：");
            String name = scanner.nextLine();
            System.out.print("成绩：");
            double score = scanner.nextDouble();
            scanner.nextLine();
            students.add(new Student(name, 0, score));
        }

        // 1. 所有学生按成绩降序
        System.out.println("\n=== 成绩排名 ===");
        students.stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .forEach(s -> System.out.println(s.getName() + ": " + s.getScore()));

        // 2. 统计信息
        DoubleSummaryStatistics stats = students.stream()
            .mapToDouble(Student::getScore)
            .summaryStatistics();

        System.out.println("\n=== 统计 ===");
        System.out.println("最高分：" + stats.getMax());
        System.out.println("最低分：" + stats.getMin());
        System.out.printf("平均分：%.2f%n", stats.getAverage());

        // 3. 及格/不及格
        System.out.println("\n=== 及格学生 ===");
        students.stream()
            .filter(s -> s.getScore() >= 60)
            .forEach(s -> System.out.println(s.getName()));

        System.out.println("\n=== 不及格学生 ===");
        students.stream()
            .filter(s -> s.getScore() < 60)
            .forEach(s -> System.out.println(s.getName()));

        // 4. 按等级分组
        System.out.println("\n=== 等级分组 ===");
        Map<String, List<Student>> groups = students.stream()
            .collect(Collectors.groupingBy(s -> {
                double score = s.getScore();
                if (score >= 90) return "优秀";
                else if (score >= 80) return "良好";
                else if (score >= 60) return "及格";
                else return "不及格";
            }));

        for (Map.Entry<String, List<Student>> entry : groups.entrySet()) {
            System.out.println(entry.getKey() + "：" + 
                entry.getValue().stream()
                    .map(Student::getName)
                    .collect(Collectors.joining("、")));
        }
    }
}
```

**关键知识点：**
- `DoubleSummaryStatistics` — 一步到位的统计工具（max/min/sum/average/count）
- `Collectors.groupingBy()` — 按条件分组，返回Map
- `Collectors.joining()` — 把多个字符串用分隔符拼接

</details>

---

## Day 4 自检

- [ ] 能用try-catch处理常见异常
- [ ] 知道什么时候用throws甩给调用者
- [ ] 能用Lambda简化匿名内部类
- [ ] 熟练使用Stream的filter、map、sorted、collect
- [ ] 能用Stream链式操作处理集合数据
