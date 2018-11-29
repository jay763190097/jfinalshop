package com.jfinalshop.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/22.
 */
public class Md5Util {

    private static final char[] bcdLookup = "0123456789abcdef".toCharArray();

    public static byte[] md5(byte[] clearText) {
        try {
            MessageDigest mdInst = null;
            mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(clearText);
            return mdInst.digest();
        } catch (NoSuchAlgorithmException var2) {
            return new byte[0];
        }
    }

    public static String md5(String text) throws UnsupportedEncodingException {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(text.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("encode error");
        }

        byte[] bytes = msgDigest.digest();
        byte tb;
        char low;
        char high;
        char tmpChar;
        String md5Str = new String();

        for (int i = 0; i < bytes.length; i++) {
            tb = bytes[i];
            tmpChar = (char) ((tb >>> 4) & 0x000f);
            if (tmpChar >= 10) {
                high = (char) (('a' + tmpChar) - 10);
            } else {
                high = (char) ('0' + tmpChar);
            }

            md5Str += high;
            tmpChar = (char) (tb & 0x000f);

            if (tmpChar >= 10) {
                low = (char) (('a' + tmpChar) - 10);
            } else {
                low = (char) ('0' + tmpChar);
            }

            md5Str += low;
        }
        return md5Str;
    }

    public static String md5For(LinkedHashMap<String,String> param){
        LinkedHashMap<String,String> maps = (LinkedHashMap<String, String>) param.clone();
        maps.remove("mac");
        try{
            return md5(buildWithQueryString(maps)).toUpperCase();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(LinkedHashMap<String,String> param){
        LinkedHashMap<String,String> maps = (LinkedHashMap<String, String>) param.clone();
        maps.remove("mac");
        try{
            byte[] temp = md5(buildWithQueryString(maps).toUpperCase().getBytes("UTF-8"));
            return bytesToHexStr(temp).toUpperCase();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);

        for(int i = 0; i < bcd.length; ++i) {
            s.append(bcdLookup[bcd[i] >>> 4 & 15]);
            s.append(bcdLookup[bcd[i] & 15]);
        }

        return s.toString();
    }

    public static String buildWithQueryString(Map<String, String> queryParas) {
        if(queryParas != null && !queryParas.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator i$ = queryParas.entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)i$.next();
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();
                sb.append(key).append('=').append(value).append('&');
            }

            return sb.substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

}
