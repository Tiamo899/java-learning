public class Car {
    String kind;
    String color;
    int speed;
    public  void start(){
        System.out.println(color+"的"+kind+"启动中...");
        speed=0;
    }
    public void accelerate(int amount){
        speed+=amount;
        System.out.println(kind+"加速了"+"速度为"+speed+"...");
    }
}
