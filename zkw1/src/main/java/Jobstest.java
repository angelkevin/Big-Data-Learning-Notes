import java.sql.SQLException;
import java.util.ArrayList;

public class Jobstest {


    public String job_name(String f_jobname) {
        String jobname = f_jobname.toLowerCase();
        if (jobname.contains("java")) {
            return "java开发";
        } else if (jobname.contains("大数据")) {
            return "大数据工程师";
        } else if (jobname.contains("测试")) {
            return "软件测试";
        } else if (jobname.contains("招聘") || jobname.contains("行政")) {
            return "HR专员";
        } else if (jobname.contains("培训") || jobname.contains("讲 师") || jobname.contains("老师")) {
            return "培训讲师";
        } else if (jobname.contains("ui") || jobname.contains("视 觉") || jobname.contains("交互")) {
            return "视觉交互";
        } else if (jobname.contains("c#")) {
            return "C开发";
        } else if (jobname.contains("it") || jobname.contains("开发")
                || jobname.contains("需求分析")) {
            return "it开发";
        } else if (jobname.contains("数据库")) {
            return "数据开发";
        } else if (jobname.contains("etl")) {
            return "etl工程";
        } else {
            return "销售";
        }
    }


    public String ave_f_salary(String f_salary) {
        float salary;
        if (f_salary.contains("万/月")) {
            String[] s = f_salary.split("万");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2 * 10000;
        } else if (f_salary.contains("万/年")) {
            String[] s = f_salary.split("万");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 12 * 10000;
        } else if (f_salary.contains("万")) {
            String[] s = f_salary.split("万");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2 * 10000;
        } else if (f_salary.contains("元以上")) {
            String[] s = f_salary.split("元");
            salary = Float.parseFloat(s[0]);
        } else if (f_salary.contains("元")) {
            String[] s = f_salary.split("元");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2;
        } else if (f_salary.contains("千/月")) {
            String[] s = f_salary.split("千");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2 * 1000;
        } else if (f_salary.contains("K")) {
            String[] s = f_salary.split("K");
            String[] strings = s[0].split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2 * 1000;
        } else if (f_salary.contains("～")) {
            String[] strings = f_salary.split("～");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2;
        } else if (f_salary.contains("面议")) {
            return "面议";


        } else {
            String[] strings = f_salary.split("-");
            salary = (Float.parseFloat(strings[0]) + Float.parseFloat(strings[1])) / 2;
        }
        return String.valueOf(salary);
    }

    public String degree(String f_degree) {
        String d = null;
        if (f_degree.equals("不限学历")) {
            d = "大专以上";
        } else {
            return f_degree;
        }

        return d;
    }

    public String web(String f_website) {
        if (f_website == null) {
            return "其他";
        } else {
            return f_website;
        }
    }

    public String hit(String f_hit) {
        if (f_hit == null) {
            return f_hit;
        } else {
            String[] strings = f_hit.split("次");
            if (strings[0].contains("万")) {
                String[] s = strings[0].split("万");
                return String.valueOf(Float.parseFloat(s[0]) * 10000);
            } else {
                return strings[0];
            }
        }
    }

    public String update(String f_updatetime, String f_collecttime) {
        if (f_updatetime == null && f_collecttime != null) {
            return f_collecttime.replace("/", "-");
        } else if (f_updatetime != null) {
            String[] strings = f_updatetime.split("于");
            return strings[1];
        } else {
            return "2021-12-12";
        }
    }

    public String exp(String f_exp, String f_salary) {
        if (f_exp == null) {
            return f_salary;
        } else if (!f_exp.equals("不限经验")) {
            String[] strings = f_exp.split("年");
            String[] split = strings[0].split("-");
            if (f_exp.equals("应届毕业生")) {
                return f_exp;
            } else if (f_exp.contains("一")) {
                return f_exp.replace("一", "1");

            } else if (Integer.parseInt(split[0]) > 5) {
                return "5年以上";
            } else if (Integer.parseInt(split[0]) > 3) {
                return "(3-5]年";

            } else if (Integer.parseInt(split[0]) > 1) {
                return "(1-3]年";
            } else {
                return "1年以下";
            }
        } else {
            if (f_salary.equals("面议")) {
                return f_salary;
            } else if (Float.parseFloat(f_salary) < 10000) {
                return "一年以下";
            } else if (Float.parseFloat(f_salary) > 30000) {
                return "五年以上";

            } else {
                return "(1-3]年";
            }
        }

    }


    public static void main(String[] args) throws SQLException {
        Jobs jobs = new Jobs();
        DBUtil dbUtil = new DBUtil();
        dbUtil.getConn();
        Jobstest jobstest = new Jobstest();
        ArrayList columnName = dbUtil.columnName();
        String sql = "select distinct * from jobs";
        ArrayList<ArrayList> queryall = dbUtil.queryall(sql);
        ArrayList<ArrayList> arrayLists = new ArrayList<>();
        for (ArrayList arrayList : queryall) {
            ArrayList<String> objects = new ArrayList<>();
            jobs.setF_id((String) arrayList.get(0));
            jobs.setF_jobname(jobstest.job_name((String) arrayList.get(1)));
            jobs.setF_degree(jobstest.degree((String) arrayList.get(3)));
            jobs.setF_salary(jobstest.ave_f_salary((String) arrayList.get(4)));
            jobs.setF_exp(jobstest.exp((String) arrayList.get(2), jobs.getF_salary()));
            jobs.setF_company((String) arrayList.get(5));
            jobs.setF_hit(jobstest.hit((String) arrayList.get(6)));
            jobs.setF_industry((String) arrayList.get(7));
            jobs.setF_scale((String) arrayList.get(8));
            jobs.setF_city((String) arrayList.get(9));
            jobs.setF_website(jobstest.web((String) arrayList.get(10)));
            jobs.setF_updatetime(jobstest.update((String) arrayList.get(11), (String) arrayList.get(12)));
            jobs.setF_collecttime((String) arrayList.get(12));
            jobs.setF_location((String) arrayList.get(13));
            jobs.setF_lng((String) arrayList.get(14));
            jobs.setF_lat((String) arrayList.get(15));
            jobs.setF_official((String) arrayList.get(16));
            jobs.setF_logo((String) arrayList.get(17));

            objects.add(jobs.getF_id());
            objects.add(jobs.getF_jobname());
            objects.add(jobs.getF_exp());
            objects.add(jobs.getF_degree());
            objects.add(jobs.getF_salary());
            objects.add(jobs.getF_company());
            objects.add(jobs.getF_hit());
            objects.add(jobs.getF_industry());
            objects.add(jobs.getF_scale());
            objects.add(jobs.getF_city());
            objects.add(jobs.getF_website());
            objects.add(jobs.getF_updatetime());
            objects.add(jobs.getF_collecttime());
            objects.add(jobs.getF_location());
            objects.add(jobs.getF_lng());
            objects.add(jobs.getF_lat());
            objects.add(jobs.getF_official());
            objects.add(jobs.getF_logo());

            arrayLists.add(objects);


        }
        System.out.println(arrayLists.size());
        for (ArrayList list :arrayLists){
            for (int i = 1; i < columnName.size(); i++) {
                String sql1 ="UPDATE jobs SET " + columnName.get(i)+ " = " + "'" + list.get(i)+"'"+" WHERE f_id = " + list.get(0)+";";
                System.out.println(sql1);
                dbUtil.update(sql1);

            }

        }


    }


}


