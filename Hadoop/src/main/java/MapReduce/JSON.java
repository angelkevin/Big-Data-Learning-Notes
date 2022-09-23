package MapReduce;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JSON {
    public static boolean isjson(String log){
        boolean flag = false;
        try {
            JSONObject.parseObject(log);
            flag = true;
        }catch (JSONException ignored){

        }
        return flag;
    }

}
