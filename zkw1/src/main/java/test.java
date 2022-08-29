import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class test {
    public static void yearandmonth() {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.next();
        String[] strings = s.split(",");
        long year = Long.parseLong(strings[0]);
        String month = strings[1];
        String[] strArr = new String[]{"1", "3", "5", "7", "8", "10", "12"};
        List<String> list = Arrays.asList(strArr);
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0 && month.equals("2")) {
            System.out.println(29);
        } else if (month.equals("2")) {
            System.out.println(28);
        } else if (list.contains(month)) {
            System.out.println(31);
        } else {
            System.out.println(30);
        }
    }

    public static void achivment() {
        Scanner scanner = new Scanner(System.in);
        float achivment = scanner.nextInt();
        if (achivment >= 90) {
            System.out.println("A");
        } else if (achivment >= 80) {
            System.out.println("B");
        } else if (achivment >= 70) {
            System.out.println("C");
        } else if (achivment >= 60) {
            System.out.println("D");
        } else {
            System.out.println("E");
        }
    }

    public static void add_num() {
        int result = 0;
        for (int i = 1; i < 100; i++) {
            if (i % 2 != 0) {
                result += i;
            }
        }
        System.out.println(result);
    }

    public static void five() {
        int sum = 0;
        int n = 0;
        for (int i = 101; i < 201; i++) {
            if (i % 5 == 0) {
                System.out.println(i);
                sum += i;
                n++;
            }

        }
        System.out.println("有" + n + "个,和为" + sum);
    }

    public static void guessnum() {
        for (int i = 5; i > 0; i--) {
            int num = new Scanner(System.in).nextInt();
            if (num == 50 && i == 5) {
                System.out.println("你是个天才！");
                break;
            } else if (num == 50) {
                System.out.println("恭喜你，回答正确！");
                break;
            } else if (num < 50) {
                System.out.println("数字过小，您还有" + (i - 1) + "次机会！");
            } else {
                System.out.println("数字过大，您还有" + (i - 1) + "次机会！");
            }
            if (i == 1) {
                System.out.println("很遗憾，game over!");
            }
        }

    }

    public static void main(String[] args) {
        guessnum();

    }


}


