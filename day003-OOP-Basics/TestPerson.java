public class TestPerson {
    public static void main(String[] args) {
        // 创建对象
        Person p1 = new Person("康", 20);
        Person p2 = new Person("小明", 18);

        // 调用方法
        p1.introduce();
        p2.introduce();

        // 修改属性
        p1.setAge(21);
        System.out.println(p1.getName() + " 的新年龄是：" + p1.getAge());
    }
}
