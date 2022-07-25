package org.example;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsClient {
    private FileSystem fs;

    @Before
    public void initHdfsClent() throws URISyntaxException, IOException, InterruptedException {

        Configuration configuration = new Configuration();
        URI uri = new URI("hdfs://hadoop:8020");
        String user = "root";
        fs = FileSystem.get(uri, configuration, user);
    }
    @After
    public void close() throws IOException {
        fs.close();
    }
    @Test
    public void mkdir() throws IOException {

        fs.mkdirs(new Path("hdfs:/file"));
    }
    public void fileDetail() throws IOException {
        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("/root/"), true);
        while (iterator.hasNext()) {
            LocatedFileStatus next = iterator.next();
            System.out.println(next.getModificationTime());
        }
    }
}
