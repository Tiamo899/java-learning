public class LambdaDemo {
    public static void main(String[] args) {
        Calculator2 add = (a, b) -> a + b;
        Calculator2 sub = (a, b) -> a - b;
        Calculator2 mul = (a, b) -> a * b;
        Calculator2 div = (a, b) -> {
            if (b == 0) {throw new IllegalArgumentException("除数不能为0");}
            return (double) a / b;
            };
        System.out.println("加法: " + add.calculate(5, 3));
        System.out.println("减法: " + sub.calculate(5, 3));
        System.out.println("乘法: " + mul.calculate(5, 3));
        System.out.println("除法: " + div.calculate(5, 3));
    }
}


