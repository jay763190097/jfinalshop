package com.jfinalshop.api.utils;

import com.jfinalshop.util.RandomUtils;


public class TokenUtil {
    /**
     * 生成token号码
     * @return token号码
     */
    public static String generateToken() {
        return RandomUtils.randomCustomUUID().concat(RandomUtils.randomString(6));
    }
}
