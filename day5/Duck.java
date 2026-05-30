public class Duck extends Animal implements Animal.Swimmable, Animal.Flyable {
    public Duck(String 小黄鸭, int i) {
        super();
    }

    @Override
    public void fly() {
        System.out.println("鸭子飞");
    }
    @Override
    public void swim() {
        System.out.println("鸭子游");
    }

    @Override
    public void makesound() {
        System.out.println("嘎嘎嘎");
    }
}