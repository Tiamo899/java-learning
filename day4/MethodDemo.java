public class MethodDemo {
    public static int findmax(int []arr){
        int max=arr[0];
        for (int i=1;i<arr.length;i++){
            if (max <arr[i]){
                max=arr[i];
            }
        }
        return max;
    }
    public static void main(String[] args) {
        int []scores={80,90,100,95,85};
        int max=findmax(scores);
        System.out.println("最高分是："+max);
    }
}
