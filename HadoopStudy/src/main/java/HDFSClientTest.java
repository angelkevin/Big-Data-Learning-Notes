import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFSClientTest {
    private FileSystem fs;

    /**
     * 获取客户端对象,创建连接
     */
    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI("hdfs://hadoop01:9000");
        //创建一个配置文件
        Configuration configuration = new Configuration();
        //用户
        String user = "root";
        //获取客户端对象
        fs = FileSystem.get(uri, configuration, user);

    }

    /**
     * 关闭连接
     */
    @After
    public void close() throws IOException {
        fs.close();
    }


    /**
     * 创建目录
     */
    @Test
    public void TestMkdir() throws IOException, URISyntaxException, InterruptedException {
        Path path = new Path("/kevin/test");
        fs.mkdirs(path);
    }

    /**
     * 上传文件
     */
    @Test
    public void TestPut() throws IOException {
        //参数一:是否删除原数据
        //参数二:是否覆盖
        //参数三:原数据
        //参数四:目标目录
        Path path = new Path("/kevin/test");
        fs.copyFromLocalFile(false, true, new Path("C:\\Users\\22154\\Desktop\\MobaXterm.log"), path);

    }

    /**
     * 下载文件
     */
    @Test
    public void TestGet() throws IOException {
        //参数一:是否删除原数据
        //参数二:原文件的路径
        //参数三:目标地址路径
        //参数四:是否开启校验
        fs.copyToLocalFile(false, new Path("/kevin/tes/MobaXterm.log"), new Path("D:\\java\\HadoopStudy"), false);
    }

    /**
     * 删除文件
     */
    @Test
    public void TestDelete() throws IOException {
        //参数一:路径
        //参数二:是否递归删除
        //删除非空目录要递归删除
        fs.delete(new Path("/kevin/tes/MobaXterm.log"), false);

    }

    /**
     * 移动文件,重命名文件,目录更名
     */
    @Test
    public void TestMove() throws IOException {
        //参数一:原文件路径
        //参数二:目标文件路径
        fs.rename(new Path("/kevin/tes/MobaXterm.log"), new Path("/kevin/tes/Moba.log"));


    }

    /**
     * 获取文件的详细信息
     */
    @Test
    public void FileDetail() throws IOException {
        //参数一:目录
        //参数二:是否递归
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while (listFiles.hasNext()) {

            LocatedFileStatus fileStatus = listFiles.next();

            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getPath().getName());
            System.out.println(fileStatus.getGroup());

        }

    }

    /**
     * 判断是不是文件或者文件夹
     */
    @Test
    public void TestFile() throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path("/"));

        for (FileStatus fileStatus : fileStatuses) {
            fileStatus.isFile();
            fileStatus.isDirectory();
        }

    }
}
