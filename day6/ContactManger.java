import java.util.ArrayList;
import java.util.Scanner;
public class ContactManger {

        private static  ArrayList<Contact> contacts = new ArrayList<>();
        private static  Scanner scanner = new Scanner(System.in);
        public static void main(String[] args) {
            while (true) {
                System.out.println("1. 添加联系人");
                System.out.println("2. 删除联系人");
                System.out.println("3. 查看所有联系人");
                System.out.println("4. 按姓名查找联系人");
                System.out.println("5. 退出");
                System.out.print("请选择操作：");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1: addContact();break;
                    case 2: deleteContact();break;
                    case 3: showAll(); break;
                    case 4: searchContact();break;
                    case 5:
                        System.out.println("再见！");
                        scanner.close();
                        return;
                    default:
                        System.out.println("无效选择");
                }
            }
        }

    private static void showAll() {
        if (contacts.isEmpty()) {
            System.out.println("通讯录为空");
            return;
        }
        for (int i = 0; i < contacts.size(); i++) {
            System.out.print((i + 1) + ". ");
            System.out.println(contacts.get(i).show());

        }
    }

    private static void searchContact() {
        System.out.print("请输入姓名：");
        String name = scanner.nextLine();
        boolean found = false;
        for (Contact c : contacts) {
            if (c.getName().equals(name)) {
                c.show();
                found = true;
                System.out.println("已找到，姓名"+ name+"的电话是"+c.getPhone()+"邮箱是"+c.getEmail());
            }
        }
        if (!found) System.out.println("未找到");
    }

    private static void deleteContact() {
        System.out.print("请输入要删除的姓名：");
        String name = scanner.nextLine();
        boolean removed = contacts.removeIf(c -> c.getName().equals(name));
        System.out.println(removed ? "删除成功" : "未找到");
    }
    private static void addContact() {
        System.out.print("姓名：");
        String name = scanner.nextLine();
        System.out.print("电话：");
        String phone = scanner.nextLine();
        System.out.print("邮箱：");
        String email = scanner.nextLine();
        contacts.add(new Contact(name, phone, email));
        System.out.println("添加成功！");
    }
}

