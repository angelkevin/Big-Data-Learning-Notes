import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] next = scanner.next().split(",");
        int n = 0;
        ArrayList<String> mylist = new ArrayList<>(Arrays.asList(next));
        ArrayList<String> strings = new ArrayList<>();
        for (String i : mylist) {
            if (Integer.parseInt(i) > 10) {
                System.out.println(i);
                n++;
            } else {
                strings.add(i);
            }
        }
        for (String s : strings) {
            mylist.remove(s);
        }
        System.out.println("有" + n + "个");
    }

}


