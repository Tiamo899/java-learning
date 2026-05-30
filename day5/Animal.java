public abstract class Animal {
    String name;
    int age;
    public abstract void makesound();
    public void showInfo(){
        System.out.println("名字: " + name + " 年龄: " + age);
    }
    public static class Dog extends Animal{

        public Dog(String 旺财, int i) {
            super();
        }

        @Override
        public void makesound() {
            System.out.println("汪汪汪");
        }
    }
    public static class Cat extends Animal{

        public Cat(String 招财猫, int i) {
            super();
        }

        @Override
        public void makesound() {
            System.out.println("喵喵喵");
        }
    }
    public interface Flyable{
        void fly();
    }
    public interface Swimmable{
        void swim();
    }
}
