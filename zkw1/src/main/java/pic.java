import java.io.*;

public class pic {

    public static void main(String[] args) throws IOException {
        String src = "D:\\1.png";
        String target = "C:\\Users\\Administrator\\Desktop\\1.png";
        copypic(src, target);
    }

    public static void copypic(String src, String target) throws IOException {
        File srcFile = new File(src);
        File targetFile = new File(target);
        InputStream in = new FileInputStream(srcFile);
        OutputStream out = new FileOutputStream(targetFile);
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
        in.close();
        out.close();

    }
}

