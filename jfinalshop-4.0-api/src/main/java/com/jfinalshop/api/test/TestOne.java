package com.jfinalshop.api.test;

import com.jfinalshop.api.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class TestOne {

    public static void main(String[] args){

    /*String URL = "http://localhost:8080/jfinalshop-4.0-api/api/account/login";
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("username","surperman");
    map.put("smsCode","1234");
    map.put("password","1234");
        try {
            String res = HttpUtils.POSTMethod(URL,map);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ok");
    }*/
    	String image = "<p><img src=\"/4.0/201501/65feeba6-e840-486d-a617-7ebe77d1b244.png\"/></p>";
    	String[] str = image.split("/");
    	
    	String res = str[1];
    	String res1 = str[2];
    	String res2 = str[3];
    	String fina = "/"+res +"/"+res1+"/"+res2;
    	String fina1 = fina.substring(0,fina.length()-1);
    	System.out.println("结果"+res+"||"+res1+"||"+res2+"||");
    	System.out.println("图片格式"+fina1);
}
}
