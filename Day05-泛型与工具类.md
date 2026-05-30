# Java Day 5：泛型与常用工具类

最后一天，补齐几个零散但实用的知识点。今天内容不多，主要是消化吸收前面4天的内容。

---

## 1. 泛型（Generics）

泛型就是"类型参数化"——写代码时不指定具体类型，用的时候再指定。

### 为什么需要泛型

```java
// 没有泛型：什么都能放，取出来要强转，容易出错
ArrayList list = new ArrayList();
list.add("hello");
list.add(123);        // 编译不报错
String s = (String) list.get(1);  // 运行时ClassCastException！

// 有泛型：只能放指定类型，编译时就检查
ArrayList<String> list2 = new ArrayList<>();
list2.add("hello");
// list2.add(123);   // 编译直接报错！
String s2 = list2.get(0);  // 不用强转
```

### 泛型类

```java
// T是类型参数，用的时候指定
public class Box<T> {
    private T content;

    public void set(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }
}

// 使用
Box<String> stringBox = new Box<>();
stringBox.set("hello");
String s = stringBox.get();  // 不用强转

Box<Integer> intBox = new Box<>();
intBox.set(123);
int n = intBox.get();
```

### 泛型方法

```java
// 方法级别的泛型，<T>写在返回类型前面
public static <T> void printArray(T[] arr) {
    for (T item : arr) {
        System.out.print(item + " ");
    }
    System.out.println();
}

// 使用
String[] names = {"张三", "李四", "王五"};
Integer[] nums = {1, 2, 3, 4, 5};

printArray(names);  // 张三 李四 王五
printArray(nums);   // 1 2 3 4 5
```

### 泛型通配符

```java
// ? 表示任意类型
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// ? extends Number — 只接受Number及其子类（Integer、Double等）
public static double sum(List<? extends Number> list) {
    double total = 0;
    for (Number n : list) {
        total += n.doubleValue();
    }
    return total;
}

// 使用
List<Integer> ints = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);

System.out.println(sum(ints));     // 6.0
System.out.println(sum(doubles));  // 6.6
```

### 常用泛型约定

| 字母 | 含义 |
|------|------|
| T | Type（类型） |
| E | Element（元素） |
| K | Key（键） |
| V | Value（值） |
| N | Number（数字） |

### 练习：写一个泛型方法，接收一个List和一个元素，返回该元素在List中出现的次数。

<details>
<summary>参考答案</summary>

```java
import java.util.*;

public class GenericDemo {
    public static <T> int countOccurrences(List<T> list, T target) {
        int count = 0;
        for (T item : list) {
            if (item == null ? target == null : item.equals(target)) {
                count++;
            }
        }
        return count;
    }

    // 或者用Stream更简洁
    public static <T> long countWithStream(List<T> list, T target) {
        return list.stream()
            .filter(item -> item == null ? target == null : item.equals(target))
            .count();
    }

    public static void main(String[] args) {
        List<String> names = Arrays.asList("张三", "李四", "张三", "王五", "张三");
        System.out.println(countOccurrences(names, "张三"));  // 3
        System.out.println(countOccurrences(names, "赵六"));  // 0

        List<Integer> nums = Arrays.asList(1, 2, 3, 2, 2, 4);
        System.out.println(countWithStream(nums, 2));  // 3
    }
}
```

**注意null处理：** 用 `==` 比较null，用 `.equals()` 比较非null值，防止空指针。

</details>

---

## 2. 常用工具类

### Objects工具类

```java
import java.util.Objects;

// 安全的equals（避免空指针）
Objects.equals(null, null);    // true
Objects.equals(null, "abc");   // false
Objects.equals("abc", "abc");  // true

// 判空
Objects.requireNonNull(name, "name不能为null");  // 如果name为null，抛异常

// hash
int hash = Objects.hash(name, age);  // 生成hashCode
```

### Collections工具类

```java
import java.util.Collections;

ArrayList<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));

Collections.sort(list);           // 排序（升序）
Collections.reverse(list);        // 反转
Collections.shuffle(list);        // 随机打乱
Collections.max(list);            // 最大值：9
Collections.min(list);            // 最小值：1
Collections.frequency(list, 1);   // 1出现的次数：2
Collections.fill(list, 0);        // 全部填充为0

// 创建不可修改的集合
List<String> unmodifiable = Collections.unmodifiableList(list);
// unmodifiable.add("x");  // 抛异常！

// 创建只有一个元素的List
List<String> single = Collections.singletonList("hello");

// 创建空集合
List<String> empty = Collections.emptyList();
```

### Arrays工具类

```java
import java.util.Arrays;

int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

Arrays.sort(arr);                    // 排序
System.out.println(Arrays.toString(arr));  // [1, 1, 2, 3, 4, 5, 6, 9]

int idx = Arrays.binarySearch(arr, 5);  // 二分查找（必须先排序），返回索引

int[] copy = Arrays.copyOf(arr, 5);     // 复制前5个元素
int[] range = Arrays.copyOfRange(arr, 2, 6);  // 复制索引2到5

boolean equal = Arrays.equals(arr, copy);  // 比较两个数组是否相等

Arrays.fill(arr, 0);  // 全部填充为0
```

### Math类

```java
Math.abs(-10);       // 10，绝对值
Math.max(10, 20);    // 20，最大值
Math.min(10, 20);    // 10，最小值
Math.pow(2, 10);     // 1024.0，2的10次方
Math.sqrt(144);      // 12.0，平方根
Math.ceil(3.2);      // 4.0，向上取整
Math.floor(3.8);     // 3.0，向下取整
Math.round(3.5);     // 4，四舍五入
Math.random();       // [0, 1)的随机小数

// 生成[a, b]的随机整数
int random = (int) (Math.random() * (b - a + 1)) + a;
```

### 日期时间（Java 8+ 新API）

```java
import java.time.*;

// 当前日期
LocalDate today = LocalDate.now();          // 2026-05-25
int year = today.getYear();                  // 2026
int month = today.getMonthValue();           // 5
int day = today.getDayOfMonth();             // 25

// 当前时间
LocalTime now = LocalTime.now();             // 14:30:25.123

// 当前日期时间
LocalDateTime dateTime = LocalDateTime.now(); // 2026-05-25T14:30:25.123

// 创建指定日期
LocalDate birthday = LocalDate.of(2003, 6, 15);

// 日期运算
LocalDate nextWeek = today.plusDays(7);
LocalDate lastMonth = today.minusMonths(1);

// 比较
today.isBefore(nextWeek);   // true
today.isAfter(lastMonth);   // true

// 格式化
import java.time.format.DateTimeFormatter;
String formatted = today.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
// "2026年05月25日"

// 解析
LocalDate parsed = LocalDate.parse("2026-05-25");
```

### 练习：写一个方法，生成一个包含n个随机整数（范围[a,b]）的ArrayList，并用Collections排序后返回。

<details>
<summary>参考答案</summary>

```java
import java.util.ArrayList;
import java.util.Collections;

public class RandomList {
    public static ArrayList<Integer> generateRandomList(int n, int a, int b) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int random = (int) (Math.random() * (b - a + 1)) + a;
            list.add(random);
        }
        Collections.sort(list);
        return list;
    }

    public static void main(String[] args) {
        ArrayList<Integer> result = generateRandomList(10, 1, 100);
        System.out.println(result);
        // 例如：[12, 23, 35, 41, 56, 62, 71, 78, 85, 93]
    }
}
```

**随机数公式解析：**
- `Math.random()` 生成 [0, 1) 的小数
- 乘以 `(b - a + 1)` 放大到 [0, b-a+1)
- 加上 `a` 平移到 [a, b+1)
- `(int)` 强转去掉小数，得到 [a, b] 的整数

</details>

---

## 3. 综合练习：单词频率统计器

读入一段英文文本，统计每个单词出现的次数，按出现频率降序输出。

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

```java
import java.util.*;
import java.util.stream.Collectors;

public class WordFrequency {
    public static void main(String[] args) {
        String text = "the quick brown fox jumps over the lazy dog " +
                      "the dog barked at the fox and the fox ran away";

        // 转小写，按空格拆分
        String[] words = text.toLowerCase().split("\\s+");

        // 用HashMap统计
        HashMap<String, Integer> map = new HashMap<>();
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }

        // 按频率降序输出
        System.out.println("=== 单词频率（降序）===");
        map.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .forEach(entry -> 
                System.out.println(entry.getKey() + " : " + entry.getValue()));

        // 输出：
        // the : 5
        // fox : 3
        // dog : 2
        // quick : 1
        // brown : 1
        // jumps : 1
        // over : 1
        // lazy : 1
        // barked : 1
        // at : 1
        // and : 1
        // ran : 1
        // away : 1

        // 也可以只输出前5个
        System.out.println("\n=== Top 5 ===");
        map.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(5)
            .forEach(entry -> 
                System.out.println(entry.getKey() + " : " + entry.getValue()));
    }
}
```

**知识点回顾：**
- `split("\\s+")` — 按一个或多个空格拆分
- `toLowerCase()` — 统一转小写，避免"The"和"the"算两个词
- `getOrDefault(word, 0) + 1` — 计数的经典写法
- Stream排序+limit — 取Top N

</details>

---

## 4. 综合练习：简易记账本

实现一个控制台记账本，支持：记录收入/支出、按日期查询、按类别统计、查看总收支。

<details>
<summary>参考答案</summary>

**Record.java**
```java
import java.time.LocalDate;

public class Record {
    private LocalDate date;
    private String category;  // 餐饮、交通、工资等
    private double amount;    // 正数=收入，负数=支出
    private String note;

    public Record(LocalDate date, String category, double amount, String note) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }

    public void show() {
        String type = amount >= 0 ? "收入" : "支出";
        System.out.printf("%s | %s | %s | %.2f | %s%n",
            date, type, category, Math.abs(amount), note);
    }
}
```

**AccountBook.java**
```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AccountBook {
    private static ArrayList<Record> records = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== 简易记账本 =====");
            System.out.println("1. 记录收入");
            System.out.println("2. 记录支出");
            System.out.println("3. 查看所有记录");
            System.out.println("4. 按日期查询");
            System.out.println("5. 按类别统计");
            System.out.println("6. 查看总收支");
            System.out.println("7. 退出");
            System.out.print("请选择：");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addRecord(true); break;
                case 2: addRecord(false); break;
                case 3: showAll(); break;
                case 4: searchByDate(); break;
                case 5: statsByCategory(); break;
                case 6: showSummary(); break;
                case 7:
                    System.out.println("再见！");
                    scanner.close();
                    return;
                default: System.out.println("无效选择");
            }
        }
    }

    private static void addRecord(boolean isIncome) {
        System.out.print("日期（yyyy-MM-dd，直接回车=今天）：");
        String dateStr = scanner.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr, fmt);

        System.out.print("类别（如餐饮/交通/工资）：");
        String category = scanner.nextLine();

        System.out.print("金额：");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("备注：");
        String note = scanner.nextLine();

        if (!isIncome) amount = -amount;
        records.add(new Record(date, category, amount, note));
        System.out.println("记录成功！");
    }

    private static void showAll() {
        if (records.isEmpty()) {
            System.out.println("暂无记录");
            return;
        }
        System.out.println("--- 所有记录 ---");
        records.stream()
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .forEach(Record::show);
    }

    private static void searchByDate() {
        System.out.print("请输入日期（yyyy-MM-dd）：");
        LocalDate date = LocalDate.parse(scanner.nextLine(), fmt);

        List<Record> result = records.stream()
            .filter(r -> r.getDate().equals(date))
            .collect(Collectors.toList());

        if (result.isEmpty()) {
            System.out.println("该日期无记录");
        } else {
            result.forEach(Record::show);
        }
    }

    private static void statsByCategory() {
        Map<String, Double> stats = records.stream()
            .collect(Collectors.groupingBy(
                Record::getCategory,
                Collectors.summingDouble(Record::getAmount)
            ));

        System.out.println("--- 按类别统计 ---");
        stats.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                String type = entry.getValue() >= 0 ? "收入" : "支出";
                System.out.printf("%s: %s %.2f%n", 
                    entry.getKey(), type, Math.abs(entry.getValue()));
            });
    }

    private static void showSummary() {
        double income = records.stream()
            .mapToDouble(Record::getAmount)
            .filter(a -> a > 0)
            .sum();

        double expense = records.stream()
            .mapToDouble(Record::getAmount)
            .filter(a -> a < 0)
            .sum();

        System.out.printf("总收入：%.2f%n", income);
        System.out.printf("总支出：%.2f%n", Math.abs(expense));
        System.out.printf("净收入：%.2f%n", income + expense);
    }
}
```

**知识点回顾：**
- `LocalDate.parse()` — 解析日期字符串
- `Collectors.groupingBy()` + `Collectors.summingDouble()` — 分组求和
- `mapToDouble().filter().sum()` — 条件求和
- 方法引用 `Record::show` 等价于 `r -> r.show()`

</details>

---

## Day 5 自检

- [ ] 理解泛型的作用，能写泛型类和泛型方法
- [ ] 会用Objects、Collections、Arrays工具类
- [ ] 会用Math生成随机数
- [ ] 会用Java 8的日期API（LocalDate、LocalDateTime）
- [ ] 能独立完成记账本之类的综合小项目

---

## Phase 1 总结

到这里，Java基础阶段就结束了。5天你学了：

| Day | 内容 | 重点 |
|-----|------|------|
| Day 1 | 语法基础 | 变量、循环、数组、方法 |
| Day 2 | 面向对象 | 类、继承、多态、接口 |
| Day 3 | 常用API与集合 | String、ArrayList、HashMap |
| Day 4 | 异常、Lambda、Stream | try-catch、Lambda简化、Stream操作 |
| Day 5 | 泛型与工具类 | 泛型、Collections、日期API |

**接下来进入Phase 2：Spring Boot。** 当你能独立写出学生管理系统、记账本这类控制台项目时，就可以开始了。
