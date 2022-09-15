public class Student {
    private String cl="Java程序设计课程";
    int num;

    public String getCl() {
        return cl;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Student(int num) {
        this.num = num;
    }
}
