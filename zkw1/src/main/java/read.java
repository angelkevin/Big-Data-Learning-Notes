import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class read {

    @Test
    public void test() throws IOException {
        File file = new File("D:\\2.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        int len = 0;
        while (len != -1) {

            len = fileInputStream.read();
            System.out.println((char) len);
        }
        fileInputStream.close();


    }
}
