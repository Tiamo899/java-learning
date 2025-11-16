public class Person {
    private String name;
    private int age;

    // 构造方法
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // getter 方法
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // setter 方法
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // 自定义方法
    public void introduce() {
        System.out.println("我是 " + name + "，今年 " + age + " 岁。");
    }
}
