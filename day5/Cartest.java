import java.util.concurrent.Callable;

public class Cartest {
    public static void main(String[] args) {
        Car c1=new Car();
        c1.kind="法拉利";
        c1.color="黑色";
        c1.speed=300;
        Car c2=new Car();
        c2.kind="保时捷";
        c2.color="蓝色";
        c2.speed=400;
        c1.start();
        c2.start();
        c1.accelerate(40);
        c2.accelerate(50);

    }
}
