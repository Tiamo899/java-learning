import java.util.HashMap;
public class CharFrequency {
    public static void main(String[] args) {
        String str = "hello world";
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = 0; i < str.length(); i++){
            char c = str.charAt(i);
            if (c == ' ') continue;
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        for (HashMap.Entry<Character, Integer> entry : map.entrySet()){
            System.out.println(entry.getKey() + "出现的次数是 " + entry.getValue());
        }
    }
}
