import java.util.ArrayList;
import java.util.Scanner;

public class GGGG {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String next = scanner.next();
            if (next.equals("#end")) {
                break;
            }

            if (strings.contains(next)) {
                System.out.println("已经包含，请继续输入");
            } else {
                strings.add(next);
            }
        }
        for (String s :strings){
            System.out.println(s);
        }
    }
}
