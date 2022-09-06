import org.junit.jupiter.api.Test;

import java.io.*;

public class Demo1 {
    @Test
    public void test() throws IOException {
        File file = new File("D:\\b.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        String aPath = "D:\\a.doc";
        FileOutputStream fileOutputStream = new FileOutputStream(aPath);


        int len = 0;
        while (len != -1) {
            len = fileInputStream.read();
            System.out.println(len);
            fileOutputStream.write(len);
        }
        fileOutputStream.flush();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
