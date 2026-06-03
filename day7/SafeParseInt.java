public class SafeParseInt {
    public static int safeParseInt(String str) {
        if (str == null){
            return -2;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }


        }public static void main(String[] args) {
        System.out.println(safeParseInt("123"));
        System.out.println(safeParseInt(null));
        System.out.println(safeParseInt("abc"));
    }
    }


