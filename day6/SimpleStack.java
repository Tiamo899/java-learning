import java.util.LinkedList;
public class SimpleStack<T>{
    LinkedList<T> list = new LinkedList<>();
    public void push(T t){
        list.addLast(t);
    }
    
    public T pop(){
        return list.removeLast();
    }
    public T peek(){
        return list.getLast();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }

    public int size(){
        return list.size();
    }
    public static void main(String[] args) {
        SimpleStack<Integer> stack = new SimpleStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println(stack.pop());
        System.out.println(stack.peek());
        System.out.println(stack.size());
        System.out.println(stack.isEmpty());
    }
}
