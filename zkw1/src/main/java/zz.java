import java.awt.*;
import java.util.Scanner;

public class zz {
    public static void main(String[] args) throws AWTException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String[] s = scanner.nextLine().split(" ");
            Double result = 0.0;
            Double x = Double.parseDouble(s[0]);
            int y = Integer.parseInt(s[1]);
            for (int i = 0; i < y; i++) {
                result = result+x;
                x = Math.sqrt(x);
            }
            System.out.printf("%.2f\n",result);


        }


    }
}
