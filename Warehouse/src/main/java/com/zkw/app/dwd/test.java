package com.zkw.app.dwd;

import com.alibaba.fastjson.JSON;

public class test {

    public static void main(String[] args) {
        String value ="{\"common\":{\"ar\":\"370000\",\"ba\":\"iPhone\",\"ch\":\"Appstore\",\"is_new\":\"1\",\"md\":\"iPhone Xs Max\",\"mid\":\"mid_689247\",\"os\":\"iOS 13.3.1\",\"uid\":\"637\",\"vc\":\"v2.1.134\"},\"page\":{\"during_time\":6490,\"item\":\"34,9,29\",\"item_type\":\"sku_ids\",\"last_page_id\":\"trade\",\"page_id\":\"payment\"},\"ts\":1666359405000}";

        System.out.println(JSON.parseObject(value));
    }
}
