# Java Day 2：面向对象（OOP）

面向对象是Java的核心思想。Day1你写的是"面向过程"——代码从上到下一行行执行。面向对象是"先把东西造出来，再让东西干活"。

---

## 1. 类和对象

**类 = 模板/图纸，对象 = 根据模板造出来的东西**

比如"人类"是类，"闻康"是对象。类定义了人有什么属性（名字、年龄）和行为（吃饭、说话）。

### 定义类

```java
public class Student {
    // 属性（成员变量）
    String name;
    int age;
    double score;

    // 方法（行为）
    public void study() {
        System.out.println(name + "在学习");
    }

    public void showInfo() {
        System.out.println("姓名：" + name + "，年龄：" + age + "，成绩：" + score);
    }
}
```

### 创建对象

```java
public class Test {
    public static void main(String[] args) {
        // 创建对象：类名 对象名 = new 类名();
        Student s1 = new Student();

        // 给属性赋值（用 . 访问）
        s1.name = "闻康";
        s1.age = 20;
        s1.score = 95.5;

        // 调用方法
        s1.study();       // 闻康在学习
        s1.showInfo();    // 姓名：闻康，年龄：20，成绩：95.5

        // 可以创建多个对象
        Student s2 = new Student();
        s2.name = "张三";
        s2.age = 21;
        s2.score = 88;
        s2.showInfo();
    }
}
```

**关键点：**
- 属性不赋值有默认值：String→null，int→0，double→0.0，boolean→false
- 每个new出来的对象是独立的，互不影响

### 练习：写一个Car类，有品牌、颜色、速度属性，有启动()和加速()方法，创建两个Car对象测试。

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

**Car.java**
```java
public class Car {
    String brand;
    String color;
    int speed;

    public void start() {
        System.out.println(color + "的" + brand + "启动了");
        speed = 0;
    }

    public void accelerate(int amount) {
        speed += amount;
        System.out.println(brand + "加速，当前速度：" + speed + "km/h");
    }

    public void showStatus() {
        System.out.println("品牌：" + brand + "，颜色：" + color + "，速度：" + speed + "km/h");
    }
}
```

**CarTest.java**
```java
public class CarTest {
    public static void main(String[] args) {
        Car car1 = new Car();
        car1.brand = "宝马";
        car1.color = "黑色";
        car1.start();
        car1.accelerate(60);
        car1.accelerate(40);
        car1.showStatus();
        // 输出：黑色的宝马启动了
        //       宝马加速，当前速度：60km/h
        //       宝马加速，当前速度：100km/h
        //       品牌：宝马，颜色：黑色，速度：100km/h

        Car car2 = new Car();
        car2.brand = "特斯拉";
        car2.color = "白色";
        car2.start();
        car2.accelerate(100);
        car2.showStatus();
    }
}
```

</details>

---

## 2. 构造方法

构造方法在 `new` 的时候自动调用，用来初始化属性。

```java
public class Student {
    String name;
    int age;

    // 无参构造（不写也默认有，但写了默认的就没了）
    public Student() {
        System.out.println("无参构造被调用了");
    }

    // 有参构造
    public Student(String name, int age) {
        this.name = name;  // this代表当前对象
        this.age = age;
    }
}
```

### this关键字

```java
public Student(String name, int age) {
    // 参数名和属性名一样时，用this区分
    // this.name = 当前对象的属性
    // name = 参数
    this.name = name;
    this.age = age;
}
```

**什么时候用this：** 当参数名和属性名相同时，必须用this。养成习惯：属性赋值的地方都加this。

### 调用构造方法

```java
Student s1 = new Student();                // 调用无参构造
Student s2 = new Student("闻康", 20);     // 调用有参构造
Student s3 = new Student("张三", 21);     // 调用有参构造

s2.showInfo();  // 姓名：闻康，年龄：20
s3.showInfo();  // 姓名：张三，年龄：21
```

**简化后完整版：**
```java
public class Student {
    String name;
    int age;
    double score;

    public Student() {}

    public Student(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public void showInfo() {
        System.out.println("姓名：" + name + "，年龄：" + age + "，成绩：" + score);
    }
}
```

### 练习：给Student类加有参构造方法，用 `new Student("闻康", 20, 95.5)` 创建对象并调用showInfo。

<details>
<summary>参考答案</summary>

```java
public class StudentTest {
    public static void main(String[] args) {
        Student s = new Student("闻康", 20, 95.5);
        s.showInfo();  // 姓名：闻康，年龄：20，成绩：95.5
    }
}
```

构造方法已经在上面的"简化后完整版"里写了，这里就是调用而已。

</details>

---

## 3. 封装（private + getter/setter）

前面的代码有个问题：任何人都能直接改属性，比如 `s1.age = -100`，这不合理。封装就是"把属性藏起来，只通过方法操作"。

```java
public class Student {
    private String name;   // private修饰，外部不能直接访问
    private int age;

    // getter：获取属性值
    public String getName() {
        return name;
    }

    // setter：设置属性值（可以加验证）
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age >= 0 && age <= 150) {  // 加验证逻辑
            this.age = age;
        } else {
            System.out.println("年龄不合法！");
        }
    }

    public void showInfo() {
        System.out.println("姓名：" + name + "，年龄：" + age);
    }
}
```

**使用方式变了：**
```java
Student s = new Student();
s.setName("闻康");     // 不能直接 s.name = "闻康"
s.setAge(20);          // 不能直接 s.age = -100

s.getName();           // "闻康"
s.getAge();            // 20
s.showInfo();
```

**以后写Java的铁律：** 属性全部private，方法全部public。

### 练习：改造Student类，把name、age、score三个属性都加private，写完整的getter/setter，setAge里加验证（0-150）。

<details>
<summary>参考答案</summary>

```java
public class Student {
    private String name;
    private int age;
    private double score;

    public Student() {}

    public Student(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        } else {
            System.out.println("年龄不合法！");
        }
    }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public void showInfo() {
        System.out.println("姓名：" + name + "，年龄：" + age + "，成绩：" + score);
    }
}
```

测试：
```java
Student s = new Student();
s.setName("闻康");
s.setAge(20);
s.setScore(95.5);
s.showInfo();  // 姓名：闻康，年龄：20，成绩：95.5

s.setAge(-5);  // 年龄不合法！
```

</details>

---

## 4. 继承（extends）

继承就是"子类自动拥有父类的属性和方法"，不用重复写代码。

```java
// 父类（基类）
public class Animal {
    String name;
    int age;

    public void eat() {
        System.out.println(name + "在吃东西");
    }

    public void sleep() {
        System.out.println(name + "在睡觉");
    }
}

// 子类（继承父类）
public class Dog extends Animal {
    // Dog自动有了name、age、eat()、sleep()

    // 子类可以有自己的特有方法
    public void bark() {
        System.out.println(name + "汪汪叫");
    }
}
```

```java
// 测试
Dog dog = new Dog();
dog.name = "旺财";   // 继承来的属性
dog.eat();           // 继承来的方法：旺财在吃东西
dog.bark();          // 自己的方法：旺财汪汪叫
```

```java
// 再来一个子类
public class Cat extends Animal {
    public void meow() {
        System.out.println(name + "喵喵叫");
    }
}
```

**核心思想：** 把共同特征（name、eat、sleep）抽到父类，子类只写自己的特殊部分。

---

## 5. 方法重写（Override）

子类觉得父类的方法不好用，可以重写（覆盖）。

```java
public class Animal {
    public void makeSound() {
        System.out.println("动物发出声音");
    }
}

public class Dog extends Animal {
    @Override  // 注解，告诉编译器这是重写
    public void makeSound() {
        System.out.println("汪汪汪");
    }
}

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("喵喵喵");
    }
}
```

```java
Dog dog = new Dog();
dog.makeSound();  // 汪汪汪（执行子类重写后的方法）

Cat cat = new Cat();
cat.makeSound();  // 喵喵喵
```

**重写规则：**
- 方法名和参数必须一样
- 返回类型必须一样或更小
- 访问权限不能更小（public不能改成private）

---

## 6. 多态

多态就是"同一个方法，不同对象执行结果不同"。

```java
public class Animal {
    public void makeSound() {
        System.out.println("动物发出声音");
    }
}

public class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("汪汪汪");
    }
}

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("喵喵喵");
    }
}
```

```java
// 多态的核心写法：父类类型 对象名 = new 子类类型();
Animal a1 = new Dog();  // Dog是Animal的子类，可以这样赋值
Animal a2 = new Cat();

a1.makeSound();  // 汪汪汪（实际调用Dog的）
a2.makeSound();  // 喵喵喵（实际调用Cat的）

// 甚至可以用数组批量处理
Animal[] animals = {new Dog(), new Cat(), new Dog()};
for (Animal a : animals) {
    a.makeSound();  // 每个对象调自己的版本
}
```

**关键点：** `Animal a = new Dog()`，a的**编译时类型**是Animal（能调Animal的方法），**运行时类型**是Dog（执行Dog重写后的方法）。

**向下转型（子类特有方法）：**
```java
Animal a = new Dog();
// a.bark();    // 报错！Animal没有bark方法

// 想调bark，必须强转
Dog d = (Dog) a;
d.bark();       // 旺财汪汪叫

// 或者一行写
((Dog) a).bark();
```

**instanceof判断类型：**
```java
Animal a = new Dog();
if (a instanceof Dog) {
    Dog d = (Dog) a;
    d.bark();  // 安全调用
}
```

---

## 7. 抽象类

有些方法父类不想写具体实现（因为不知道怎么写），就声明成抽象方法，让子类必须实现。

```java
public abstract class Shape {
    String name;

    // 抽象方法：没有方法体，子类必须重写
    public abstract double area();

    // 普通方法可以有
    public void show() {
        System.out.println("这是" + name + "，面积是：" + area());
    }
}
```

```java
public class Circle extends Shape {
    double radius;

    public Circle(double radius) {
        this.radius = radius;
        this.name = "圆形";
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

```java
public class Rectangle extends Shape {
    double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
        this.name = "矩形";
    }

    @Override
    public double area() {
        return width * height;
    }
}
```

```java
// 测试
Circle c = new Circle(5);
c.show();  // 这是圆形，面积是：78.53981633974483

Rectangle r = new Rectangle(4, 6);
r.show();  // 这是矩形，面积是：24.0
```

**抽象类的特点：**
- 用 `abstract` 修饰
- 不能被 `new`（不能 `new Shape()`）
- 有抽象方法，子类必须全部重写
- 可以有普通方法和属性

---

## 8. 接口（interface）

接口是"纯粹的规范"，比抽象类更彻底——只能有抽象方法（和常量）。一个类可以实现多个接口（Java单继承，多实现）。

```java
// 定义接口
public interface Flyable {
    void fly();  // 默认 public abstract
}

public interface Swimmable {
    void swim();
}
```

```java
// 实现接口：implements
public class Duck extends Animal implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println(name + "在飞");
    }

    @Override
    public void swim() {
        System.out.println(name + "在游泳");
    }
}
```

```java
Duck duck = new Duck();
duck.name = "唐老鸭";
duck.eat();    // 继承Animal
duck.fly();    // 实现Flyable
duck.swim();   // 实现Swimmable

// 接口也可以多态
Flyable f = new Duck();
f.fly();       // 唐老鸭在飞
```

**接口 vs 抽象类：**

| | 接口 | 抽象类 |
|--|------|--------|
| 关键字 | interface | abstract class |
| 继承 | 多实现（implements） | 单继承（extends） |
| 方法 | 全是抽象方法（默认） | 可以有普通方法 |
| 属性 | 只能是常量 | 普通属性 |
| 设计思想 | "能做什么" | "是什么" |

**什么时候用接口：** 当你要定义"这个东西能做什么事"的时候。比如Flyable表示"能飞的"，Swimmable表示"能游泳的"，任何动物都可以实现这些接口。

---

## 综合练习1：动物园类体系

```java
// 1. 创建Animal抽象类
//    - 属性：name, age
//    - 抽象方法：makeSound()
//    - 普通方法：showInfo()

// 2. 创建Dog和Cat继承Animal
//    - 各自重写makeSound()
//    - 各自添加一个特有方法

// 3. 创建Flyable接口和Swimmable接口

// 4. 创建Duck类，继承Animal，实现两个接口

// 5. 在main方法中创建各种对象，用多态遍历输出
```

> 先自己写，写完再往下看答案。这个练习把前面所有知识点串起来了。

<details>
<summary>参考答案</summary>

**Animal.java**
```java
public abstract class Animal {
    protected String name;
    protected int age;

    public Animal() {}

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public abstract void makeSound();

    public void showInfo() {
        System.out.println("名字：" + name + "，年龄：" + age);
    }
}
```

**Flyable.java**
```java
public interface Flyable {
    void fly();
}
```

**Swimmable.java**
```java
public interface Swimmable {
    void swim();
}
```

**Dog.java**
```java
public class Dog extends Animal {
    public Dog(String name, int age) {
        super(name, age);  // 调用父类构造方法
    }

    @Override
    public void makeSound() {
        System.out.println(name + "：汪汪汪！");
    }

    public void fetch() {
        System.out.println(name + "在捡球");
    }
}
```

**Cat.java**
```java
public class Cat extends Animal {
    public Cat(String name, int age) {
        super(name, age);
    }

    @Override
    public void makeSound() {
        System.out.println(name + "：喵喵喵！");
    }

    public void scratch() {
        System.out.println(name + "在磨爪子");
    }
}
```

**Duck.java**
```java
public class Duck extends Animal implements Flyable, Swimmable {
    public Duck(String name, int age) {
        super(name, age);
    }

    @Override
    public void makeSound() {
        System.out.println(name + "：嘎嘎嘎！");
    }

    @Override
    public void fly() {
        System.out.println(name + "在飞");
    }

    @Override
    public void swim() {
        System.out.println(name + "在游泳");
    }
}
```

**ZooTest.java（主测试类）**
```java
public class ZooTest {
    public static void main(String[] args) {
        Dog dog = new Dog("旺财", 3);
        Cat cat = new Cat("咪咪", 2);
        Duck duck = new Duck("唐老鸭", 5);

        // 多态数组
        Animal[] animals = {dog, cat, duck};

        System.out.println("=== 动物园全体叫 ===");
        for (Animal a : animals) {
            a.makeSound();
        }

        System.out.println("\n=== 特有方法 ===");
        dog.fetch();
        cat.scratch();
        duck.fly();
        duck.swim();

        System.out.println("\n=== 接口多态 ===");
        Flyable flyer = duck;
        flyer.fly();

        Swimmable swimmer = duck;
        swimmer.swim();
    }
}
```

运行结果：
```
=== 动物园全体叫 ===
旺财：汪汪汪！
咪咪：喵喵喵！
唐老鸭：嘎嘎嘎！

=== 特有方法 ===
旺财在捡球
咪咪在磨爪子
唐老鸭在飞
唐老鸭在游泳

=== 接口多态 ===
唐老鸭在飞
唐老鸭在游泳
```

**注意super关键字：** 子类构造方法里用 `super(name, age)` 调用父类的有参构造，省得重复赋值。不写super的话，会自动调父类的无参构造。

</details>

---

## 综合练习2：学生管理系统（纯控制台版）

```java
// 用ArrayList存Student对象，实现：
// 1. 添加学生
// 2. 查看所有学生
// 3. 按姓名查找
// 4. 删除学生
```

> 先自己写，写完再往下看答案。

<details>
<summary>参考答案</summary>

**Student.java**（用上面封装后的版本）

**StudentManager.java**
```java
import java.util.ArrayList;
import java.util.Scanner;

public class StudentManager {
    public static void main(String[] args) {
        ArrayList<Student> list = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 学生管理系统 =====");
            System.out.println("1. 添加学生");
            System.out.println("2. 查看所有学生");
            System.out.println("3. 按姓名查找");
            System.out.println("4. 删除学生");
            System.out.println("5. 退出");
            System.out.print("请选择：");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("请输入姓名：");
                    String name = scanner.next();
                    System.out.print("请输入年龄：");
                    int age = scanner.nextInt();
                    System.out.print("请输入成绩：");
                    double score = scanner.nextDouble();
                    list.add(new Student(name, age, score));
                    System.out.println("添加成功！当前共 " + list.size() + " 名学生");
                    break;

                case 2:
                    if (list.isEmpty()) {
                        System.out.println("暂无学生信息");
                    } else {
                        System.out.println("--- 学生列表 ---");
                        for (int i = 0; i < list.size(); i++) {
                            System.out.print((i + 1) + ". ");
                            list.get(i).showInfo();
                        }
                    }
                    break;

                case 3:
                    System.out.print("请输入要查找的姓名：");
                    String searchName = scanner.next();
                    boolean found = false;
                    for (Student s : list) {
                        if (s.getName().equals(searchName)) {
                            s.showInfo();
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("未找到该学生");
                    }
                    break;

                case 4:
                    System.out.print("请输入要删除的姓名：");
                    String deleteName = scanner.next();
                    boolean removed = list.removeIf(s -> s.getName().equals(deleteName));
                    if (removed) {
                        System.out.println("删除成功！当前共 " + list.size() + " 名学生");
                    } else {
                        System.out.println("未找到该学生");
                    }
                    break;

                case 5:
                    System.out.println("退出系统");
                    scanner.close();
                    return;

                default:
                    System.out.println("无效选择，请重新输入");
            }
        }
    }
}
```

运行效果：
```
===== 学生管理系统 =====
1. 添加学生
2. 查看所有学生
3. 按姓名查找
4. 删除学生
5. 退出
请选择：1
请输入姓名：闻康
请输入年龄：21
请输入成绩：95.5
添加成功！当前共 1 名学生

===== 学生管理系统 =====
请选择：2
--- 学生列表 ---
1. 姓名：闻康，年龄：21，成绩：95.5

===== 学生管理系统 =====
请选择：5
退出系统
```

**关键知识点回顾：**
- `ArrayList<Student>` — 泛型集合，只能存Student对象
- `list.add()` — 添加元素
- `list.removeIf(s -> s.getName().equals(name))` — Lambda表达式删除满足条件的元素
- `list.isEmpty()` — 判断是否为空
- `scanner.close()` — 关闭Scanner释放资源

</details>

---

## Day 2 自检

- [ ] 能独立写出一个类（属性+构造方法+getter/setter+方法）
- [ ] 理解继承，能写出父子类关系
- [ ] 理解多态，能用 `父类类型 对象 = new 子类类型()` 写代码
- [ ] 理解接口，知道什么时候用extends，什么时候用implements
- [ ] 能写出学生管理系统的控制台版本
