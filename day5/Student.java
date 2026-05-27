public class Student {
    private String name;
    private int age;
    private double score;
    public Student(){
    }
    public Student(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }
    public void show(){
        System.out.println("姓名"+name+"年龄 "+age+"分数 "+score);
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public int getAge(){
        return age;
    }
    public void setAge(int age){
        if(age<0||age>150){
            System.out.println("年龄不合法");
            return;
        }else {
        this.age=age;}
    }
    public double getScore(){
        return score;
    }
    public void setScore(double score){

        this.score=score;
    }
}
