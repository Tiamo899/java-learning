public class CalculatorDemo {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println("加法: " + calc.add(5, 3));
        System.out.println("减法: " + calc.subtract(5, 3));
        System.out.println("乘法: " + calc.multiply(5, 3));
        System.out.println("除法: " + calc.divide(5, 3));
    }
}
