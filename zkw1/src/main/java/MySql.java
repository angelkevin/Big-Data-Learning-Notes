import java.sql.SQLException;

public class MySql {

    public static void main(String[] args) throws SQLException {
        String sql = "select * from jobs where f_id=? ";
        DBUtil dbUtil = new DBUtil();
        dbUtil.query(sql,57);
        String sql1="update jobs set  f_degree = ? where f_id =?";
        dbUtil.update(sql1,"专科以上",56);
        dbUtil.query(sql,56);
        String sql2 = "select * from jobs limit ?,?";
        dbUtil.query(sql2, 5, 5);

    }
}
