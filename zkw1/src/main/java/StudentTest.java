import java.util.*;

public class StudentTest {
    public static void main(String[] args) {
        Student zkw = new Student(455);


        HashMap<Integer, Student> hashmap = new HashMap<>();
        for (int i = 20070301; i < 20070331; i++) {
            int i1 = (int) (40 * Math.random() + 60);
            hashmap.put(i, new Student(i1));
        }
        List<Map.Entry<Integer, Student>> list = new ArrayList<>(hashmap.entrySet());
        list.sort(new Comparator<Map.Entry<Integer, Student>>() {
            @Override
            public int compare(Map.Entry<Integer, Student> o1, Map.Entry<Integer, Student> o2) {
                return Integer.compare(o1.getValue().getNum(), o2.getValue().getNum());
            }

        });
        for (Map.Entry<Integer, Student> next : list) {
            System.out.println("学号：" + next.getKey() + " 课程：" + next.getValue().getCl() + " 成绩：" + next.getValue().getNum());
        }

    }

}
