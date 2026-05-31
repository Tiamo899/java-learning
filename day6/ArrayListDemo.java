import java.util.ArrayList;
public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(100);
        list.add(200);
        list.add(300);
        list.add(400);
        list.add(500);
        int max = list.get(0);
        int min = list.get(0);
        int sum = 0;
        double avg = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
            if (list.get(i) < min) {
                min = list.get(i);
            }
            sum += list.get(i);
            avg = sum / list.size();
        }
        System.out.println("最大值：" + max);
        System.out.println("最小值：" + min);
        System.out.println("平均值：" + avg);

        for(int i=list.size()-1;i>=0;i--){
            if(list.get(i)>avg){
                list.remove(i);
            }
        }
        System.out.println("删除后的列表为"+list);
    }
}
