import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String[] s = scanner.nextLine().split(" ");
            int x = Integer.parseInt(s[0]);
            int y = Integer.parseInt(s[1]);
            ArrayList<Integer> ints = new ArrayList<Integer>();
            while (x<=y){
                int num = 0;
                num = (x/100)*(x/100)*(x/100)+(x/10%10)*(x/10%10)*(x/10%10)+(x%10)*(x%10)*(x%10);
                if (num == x){
                  ints.add(num);
                }
                x++;
            }
            if(ints.size() ==0){
                System.out.println("no");
            }else {
                for (Integer anInt : ints) {
                    System.out.print(anInt+" ");
                }
            }


        }
    }
}
