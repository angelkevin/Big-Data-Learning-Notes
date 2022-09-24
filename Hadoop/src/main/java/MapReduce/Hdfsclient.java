package MapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class Hdfsclient {
    public FileSystem fs;

    @Before
    public void initHdfsClent() throws URISyntaxException, IOException, InterruptedException {

        Configuration configuration = new Configuration();
        configuration.set("fs.default.name", "hdfs://192.168.170.133:9000");
        fs = FileSystem.get(configuration);
    }

    @After
    public void close() throws IOException {
        fs.close();
    }

    @Test
    public void mkdir() throws IOException {

        boolean mkdirs = fs.mkdirs(new Path("hdfs://192.168.170.133:9000/zzz"));
        System.out.println(mkdirs);
    }

    public void fileDetail() throws IOException {
        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("/root/"), true);
        while (iterator.hasNext()) {
            LocatedFileStatus next = iterator.next();
            System.out.println(next.getModificationTime());
        }

    }

    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.default.name", "hdfs://192.168.170.133:9000");
        FileSystem fs = FileSystem.get(configuration);
    }
}
