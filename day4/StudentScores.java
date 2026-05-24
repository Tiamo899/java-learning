import java.util.Scanner;
public class StudentScores {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int score[]=new int[5];
        for(int i=0;i<5;i++){
            System.out.print("请输入第"+(i+1)+"个学生的成绩：");
            score[i]=scanner.nextInt();
        }
        int max=score[0];
        for(int i=1;i<5;i++){
            if(score[i]>max){
                max=score[i];
            }
        }
        System.out.println("最高分是："+max);
        int min=score[0];
        for (int i=1;i<5;i++){
            if (score[i]<min){
                min = score[i];
            }
        }
        System.out.println("最低分是："+min);
        int sum=0;
        for(int num: score){
            sum+=num;
        }
        System.out.println("平均分是："+(double)sum/5);
    }
}
