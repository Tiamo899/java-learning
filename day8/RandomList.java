import java.util.ArrayList;
import java.util.Collections;
public class RandomList {
    public static ArrayList<Integer> generateRandomList(int a,int b,int n){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int random = (int) (Math.random() * (b - a + 1)) + a;
            list.add(random);
        }
        Collections.sort(list);
        return list;
    }
    public static void main(String[] args) {
        ArrayList<Integer> list = generateRandomList(1, 100, 10);
        for (int i = 0; i < list.size(); i++) {//逐个获取元素并打印
            System.out.println(list.get(i));
        }
    }
}
