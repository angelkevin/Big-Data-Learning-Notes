import java.sql.*;
import java.util.ArrayList;

public class DBUtil {

    private String url = "jdbc:mysql://192.168.170.133:3306/myemployees?useUnicode=true&characterEncoding=utf8";
    private String user = "root";
    private String password = "123456";

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public Connection getConn() throws SQLException {

        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }


    public void add(String sql, Object... objects) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object o : objects) {
            preparedStatement.setObject(i++, o);
        }
        int execute = preparedStatement.executeUpdate();
        if (execute > 0) {
            System.out.println("添加成功");
        } else {
            System.out.println("添加失败");
        }

    }


    public void update(String sql, Object... objects) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object o : objects) {
            preparedStatement.setObject(i++, o);
            System.out.println(o);
        }

        boolean execute = preparedStatement.execute();
        if (execute) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }

    }


    public void update(String sql) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        int execute = preparedStatement.executeUpdate();
        if (execute > 0) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }

    }

    public void drop(String sql, Object... objects) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object o : objects) {
            preparedStatement.setObject(i++, o);
        }
        int execute = preparedStatement.executeUpdate();
        if (execute > 0) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }

    }


    public ArrayList<ArrayList> query(String sql, Object... objects) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object o : objects) {
            preparedStatement.setObject(i++, o);
        }
        resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<String> columnName = new ArrayList<>();

        for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
            columnName.add(metaData.getColumnName(j));
        }

        ArrayList<ArrayList> objects2 = new ArrayList<>();

        while (resultSet.next()) {
            ArrayList<Object> objects1 = new ArrayList<>();
            for (String s : columnName) {
                String x = (String) resultSet.getObject(s);
                objects1.add(x);
            }
            //System.out.println(objects1);
            objects2.add(objects1);
        }


        return objects2;

    }


    public ArrayList<ArrayList> queryall(String sql) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<String> columnName = new ArrayList<>();

        for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
            columnName.add(metaData.getColumnName(j));
        }

        ArrayList<ArrayList> objects2 = new ArrayList<>();

        while (resultSet.next()) {
            ArrayList<Object> objects1 = new ArrayList<>();
            for (String s : columnName) {
                Object x = resultSet.getObject(s);
                objects1.add(x);
            }
            objects2.add(objects1);
        }

        for (Object o : objects2) {
             System.out.println(o);
        }

        return objects2;

    }

    public ArrayList<String> columnName() throws SQLException {
        String sql = "SELECT * from jobs";
        preparedStatement = connection.prepareStatement(sql);
        resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<String> columnName = new ArrayList<>();

        for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
            columnName.add(metaData.getColumnName(j));
        }
        System.out.println(columnName);
        return columnName;
    }

    public void free() throws SQLException {
        connection.close();
        preparedStatement.close();
        resultSet.close();
    }


}
