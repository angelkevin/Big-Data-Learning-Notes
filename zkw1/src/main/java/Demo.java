import java.io.File;

public class Demo {
    public static void show(File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            System.out.println(f.getName());
            if (f.isDirectory()) {
                show(f);
            }
        }

    }

    public static void main(String[] args) {
        File file = new File("D:\\java\\zkw1");
        show(file);
    }

}
