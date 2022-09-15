import java.sql.SQLException;

public class ccc {
    public static void main(String[] args) throws SQLException {
        DBUtil dbUtil = new DBUtil();
        dbUtil.getConn();

        String[] sql = {"select f_jobname, avg(f_salary) as avg from jobs where f_jobname = '大数据工程师';",
                " select f_jobname, avg(f_salary) as avg from jobs group by f_jobname;",
                " select f_exp, avg(f_salary) as avg from jobs group by f_exp;",
                " select f_city, avg(f_salary) as avg from jobs group by f_city;",
                " select f_jobname, f_exp, avg(f_salary) as avg from jobs group by f_jobname, f_exp;",
                " select f_jobname, f_city, avg(f_salary) as avg from jobs group by f_jobname, f_city;",
                " select f_jobname, f_city, f_exp, avg(f_salary) as avg from jobs where f_jobname = '大数据工程师' or f_jobname = 'etl工程' group by f_jobname, f_city, f_exp;",
                " select f_jobname, f_city, f_exp, avg(f_salary) as avg from jobs group by f_jobname, f_city, f_exp;",
                " select f_jobname, f_degree, count(f_degree) as different_degree from jobs group by f_jobname, f_degree;",
                " select f_city,f_jobname,sum(f_hit) as sum from jobs group by f_city, f_jobname order by sum desc  limit 0,10 ;",
                " select f_city, count(f_jobname) as num, f_updatetime from jobs where f_jobname = '大数据工程师' or f_jobname = 'etl工程' group by f_city, f_updatetime;",
                " select f_city, count(f_company)  as num from jobs where f_jobname = '大数据工程师' or f_jobname = 'etl工程' group by f_city;"};
        for (String s : sql) {
            dbUtil.queryall(s);
        }
        dbUtil.free();
    }
}
