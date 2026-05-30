# Java Day 3：常用API与集合

Day1学了基本语法，Day2学了面向对象。今天学Java里最常用的工具类和数据容器——写项目天天用的东西。

---

## 1. String字符串

String是Java里用得最多的类，没有之一。

### 创建和基本操作

```java
// 两种创建方式（面试常考区别）
String s1 = "hello";           // 字符串常量池
String s2 = new String("hello"); // 堆内存新对象

// == 比较的是地址，不是内容！
System.out.println(s1 == s2);          // false
System.out.println(s1.equals(s2));     // true（比较内容，永远用这个）
```

**铁律：** 字符串比较永远用 `.equals()`，不要用 `==`。

### 常用方法

```java
String s = "Hello, World!";

s.length();                 // 13，长度
s.charAt(0);                // 'H'，取某个位置的字符
s.substring(7);             // "World!"，从位置7截取到末尾
s.substring(0, 5);          // "Hello"，从0截取到5（不包含5）
s.indexOf("World");         // 7，查找子串位置，找不到返回-1
s.contains("World");        // true，是否包含
s.startsWith("Hello");      // true，是否以某字符串开头
s.endsWith("!");            // true，是否以某字符串结尾
s.toUpperCase();            // "HELLO, WORLD!"，转大写
s.toLowerCase();            // "hello, world!"，转小写
s.trim();                   // 去掉首尾空格
s.replace("World", "Java"); // "Hello, Java!"，替换
s.split(", ");              // ["Hello", "World!"]，按分隔符拆分成数组
s.isEmpty();                // false，是否为空串
s.isBlank();                // false，是否为空白（Java 11+）
```

### 字符串拼接

```java
// 方式1：+ 号（简单场景用）
String result = "Hello" + ", " + "World";

// 方式2：StringBuilder（循环拼接必须用这个，性能差10倍）
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(", ");
sb.append("World");
String result2 = sb.toString();  // "Hello, World"
```

**什么时候用StringBuilder：** 在循环里拼接字符串时。`+`号每次都会创建新对象，循环1000次就创建1000个垃圾对象。

```java
// 错误示范（性能差）
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // 每次循环都创建新String对象
}

// 正确示范
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

### 类型转换（超常用）

```java
// String → int
int num = Integer.parseInt("123");

// String → double
double d = Double.parseDouble("3.14");

// int → String
String s1 = String.valueOf(123);
String s2 = Integer.toString(123);
String s3 = 123 + "";  // 最简单的方式

// String → char数组
char[] chars = "hello".toCharArray();

// char数组 → String
String s = new String(chars);
```

### 练习：写一个方法，接收一个字符串，统计其中大写字母、小写字母、数字、其他字符各有多少个。

<details>
<summary>参考答案</summary>

```java
public class CharCount {
    public static void count(String str) {
        int upper = 0, lower = 0, digit = 0, other = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                upper++;
            } else if (c >= 'a' && c <= 'z') {
                lower++;
            } else if (c >= '0' && c <= '9') {
                digit++;
            } else {
                other++;
            }
        }

        System.out.println("大写：" + upper);
        System.out.println("小写：" + lower);
        System.out.println("数字：" + digit);
        System.out.println("其他：" + other);
    }

    public static void main(String[] args) {
        count("Hello World 123!");
        // 大写：2
        // 小写：8
        // 数字：3
        // 其他：3（两个空格+一个感叹号）
    }
}
```

**知识点：** `char`可以像数字一样比较，`'A'`到`'Z'`是连续的。

</details>

---

## 2. ArrayList（动态数组）

Day1学的数组长度固定，ArrayList长度可变，是实际开发中最常用的集合。

### 基本操作

```java
import java.util.ArrayList;

// 创建：必须指定泛型（存什么类型）
ArrayList<String> list = new ArrayList<>();

// 增
list.add("张三");           // 添加到末尾
list.add(0, "李四");       // 添加到指定位置

// 删
list.remove("张三");        // 按内容删
list.remove(0);             // 按索引删

// 改
list.set(0, "王五");        // 修改指定位置

// 查
String name = list.get(0);  // 获取指定位置
int size = list.size();     // 元素个数
boolean empty = list.isEmpty(); // 是否为空
boolean has = list.contains("张三"); // 是否包含
int idx = list.indexOf("张三"); // 查找位置，找不到返回-1
```

### 遍历

```java
ArrayList<String> list = new ArrayList<>();
list.add("张三");
list.add("李四");
list.add("王五");

// 方式1：for循环
for (int i = 0; i < list.size(); i++) {
    System.out.println(list.get(i));
}

// 方式2：for-each（更简洁）
for (String name : list) {
    System.out.println(name);
}
```

### 存对象

```java
ArrayList<Student> students = new ArrayList<>();
students.add(new Student("张三", 20, 90));
students.add(new Student("李四", 21, 85));
students.add(new Student("王五", 19, 95));

for (Student s : students) {
    s.showInfo();
}
```

### 练习：用ArrayList存储5个整数，然后找出最大值、最小值、平均值，并删除小于平均值的元素。

<details>
<summary>参考答案</summary>

```java
import java.util.ArrayList;

public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(88);
        list.add(95);
        list.add(42);
        list.add(76);
        list.add(60);

        // 求最大值、最小值、总和
        int max = list.get(0);
        int min = list.get(0);
        int sum = 0;
        for (int num : list) {
            if (num > max) max = num;
            if (num < min) min = num;
            sum += num;
        }
        double avg = (double) sum / list.size();

        System.out.println("最大值：" + max);    // 95
        System.out.println("最小值：" + min);    // 42
        System.out.println("平均值：" + avg);    // 72.2

        // 删除小于平均值的元素（注意：倒序遍历删除，避免索引错乱）
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) < avg) {
                list.remove(i);
            }
        }

        System.out.println("删除后的列表：" + list);  // [88, 95, 76]
    }
}
```

**注意：** 正序遍历时删除元素会导致索引错位，必须倒序遍历，或者用 `removeIf`：
```java
list.removeIf(num -> num < avg);
```

</details>

---

## 3. HashMap（键值对集合）

HashMap用"键-值"对存储数据，像字典一样——通过key找value。

### 基本操作

```java
import java.util.HashMap;

// 创建
HashMap<String, Integer> map = new HashMap<>();

// 增/改（key重复会覆盖旧值）
map.put("张三", 90);
map.put("李四", 85);
map.put("王五", 95);
map.put("张三", 100);  // 覆盖张三的成绩

// 查
int score = map.get("张三");       // 100
int score2 = map.getOrDefault("赵六", 0); // 找不到返回默认值0
boolean has = map.containsKey("张三");    // true
boolean hasVal = map.containsValue(95);   // true
int size = map.size();                    // 3

// 删
map.remove("李四");
```

### 遍历

```java
HashMap<String, Integer> map = new HashMap<>();
map.put("张三", 90);
map.put("李四", 85);
map.put("王五", 95);

// 方式1：遍历所有key
for (String key : map.keySet()) {
    System.out.println(key + " = " + map.get(key));
}

// 方式2：遍历所有键值对（推荐）
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}

// 方式3：遍历所有value（只需要值的时候）
for (int value : map.values()) {
    System.out.println(value);
}
```

**注意：** HashMap是无序的，遍历顺序和插入顺序可能不同。想保持有序用 `LinkedHashMap`。

### 练习：统计一段文字中每个字符出现的次数。

<details>
<summary>参考答案</summary>

```java
import java.util.HashMap;

public class CharFrequency {
    public static void main(String[] args) {
        String text = "hello world";
        HashMap<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') continue;  // 跳过空格

            // getOrDefault：如果key存在返回值，否则返回默认值
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        for (HashMap.Entry<Character, Integer> entry : map.entrySet()) {
            System.out.println("'" + entry.getKey() + "' 出现了 " + entry.getValue() + " 次");
        }
        // 'h' 出现了 1 次
        // 'e' 出现了 1 次
        // 'l' 出现了 3 次
        // 'o' 出现了 2 次
        // 'w' 出现了 1 次
        // 'r' 出现了 1 次
        // 'd' 出现了 1 次
    }
}
```

**核心思路：** `map.getOrDefault(c, 0) + 1` — 如果字符已存在，取出计数+1；如果不存在，从0开始+1。

</details>

---

## 4. LinkedList（链表）

LinkedList也是List，和ArrayList的区别在于底层结构不同。

```java
import java.util.LinkedList;

LinkedList<String> list = new LinkedList<>();
list.add("A");
list.add("B");
list.addFirst("X");  // 添加到头部（LinkedList特有）
list.addLast("Y");   // 添加到尾部
list.getFirst();      // 获取头部
list.getLast();       // 获取尾部
list.removeFirst();   // 删除头部
list.removeLast();    // 删除尾部
```

**ArrayList vs LinkedList（面试常考）：**

| | ArrayList | LinkedList |
|--|-----------|------------|
| 底层 | 数组 | 链表 |
| 随机访问（get） | 快 O(1) | 慢 O(n) |
| 头部增删 | 慢 O(n) | 快 O(1) |
| 尾部增删 | 快 O(1) | 快 O(1) |
| 内存 | 连续 | 分散 |

**实际开发：** 99%用ArrayList，除非你明确需要频繁在头部增删。

### 练习：用LinkedList实现一个简单的栈（后进先出LIFO），支持push（压栈）、pop（弹栈）、peek（查看栈顶）。

<details>
<summary>参考答案</summary>

```java
import java.util.LinkedList;

public class SimpleStack<T> {
    private LinkedList<T> list = new LinkedList<>();

    public void push(T item) {
        list.addLast(item);  // 压到末尾
    }

    public T pop() {
        return list.removeLast();  // 从末尾弹出
    }

    public T peek() {
        return list.getLast();  // 查看末尾元素
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public static void main(String[] args) {
        SimpleStack<Integer> stack = new SimpleStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println(stack.pop());   // 3（后进先出）
        System.out.println(stack.peek());  // 2
        System.out.println(stack.pop());   // 2
        System.out.println(stack.size());  // 1
    }
}
```

**注意：** `<T>` 是泛型，表示这个类可以存任意类型。创建时指定具体类型：`SimpleStack<Integer>`。

</details>

---

## 5. 综合练习：通讯录管理系统

用ArrayList或HashMap实现一个通讯录，功能：添加联系人、查找联系人、删除联系人、查看所有联系人。

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

**Contact.java**
```java
public class Contact {
    private String name;
    private String phone;
    private String email;

    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void show() {
        System.out.println("姓名：" + name + "，电话：" + phone + "，邮箱：" + email);
    }
}
```

**ContactManager.java**
```java
import java.util.ArrayList;
import java.util.Scanner;

public class ContactManager {
    private static ArrayList<Contact> contacts = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== 通讯录 =====");
            System.out.println("1. 添加联系人");
            System.out.println("2. 查看所有联系人");
            System.out.println("3. 按姓名查找");
            System.out.println("4. 删除联系人");
            System.out.println("5. 退出");
            System.out.print("请选择：");

            int choice = scanner.nextInt();
            scanner.nextLine();  // 吃掉换行符

            switch (choice) {
                case 1: addContact(); break;
                case 2: showAll(); break;
                case 3: searchContact(); break;
                case 4: deleteContact(); break;
                case 5:
                    System.out.println("再见！");
                    scanner.close();
                    return;
                default:
                    System.out.println("无效选择");
            }
        }
    }

    private static void addContact() {
        System.out.print("姓名：");
        String name = scanner.nextLine();
        System.out.print("电话：");
        String phone = scanner.nextLine();
        System.out.print("邮箱：");
        String email = scanner.nextLine();
        contacts.add(new Contact(name, phone, email));
        System.out.println("添加成功！");
    }

    private static void showAll() {
        if (contacts.isEmpty()) {
            System.out.println("通讯录为空");
            return;
        }
        for (int i = 0; i < contacts.size(); i++) {
            System.out.print((i + 1) + ". ");
            contacts.get(i).show();
        }
    }

    private static void searchContact() {
        System.out.print("请输入姓名：");
        String name = scanner.nextLine();
        boolean found = false;
        for (Contact c : contacts) {
            if (c.getName().equals(name)) {
                c.show();
                found = true;
            }
        }
        if (!found) System.out.println("未找到");
    }

    private static void deleteContact() {
        System.out.print("请输入要删除的姓名：");
        String name = scanner.nextLine();
        boolean removed = contacts.removeIf(c -> c.getName().equals(name));
        System.out.println(removed ? "删除成功" : "未找到");
    }
}
```

**注意 `scanner.nextLine()` 的坑：** `nextInt()` 之后必须加一个 `nextLine()` 来吃掉缓冲区的换行符，否则下次 `nextLine()` 会读到空字符串。

</details>

---

## Day 3 自检

- [ ] 熟练使用String的常用方法（length、substring、equals、split等）
- [ ] 知道StringBuilder什么时候用（循环拼接）
- [ ] 会用ArrayList的增删改查和遍历
- [ ] 会用HashMap的put/get/遍历
- [ ] 知道ArrayList和LinkedList的区别
- [ ] 能用集合写出通讯录管理系统
