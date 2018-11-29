package com.jfinalshop.kit;

import org.apache.commons.lang3.StringUtils;

public class Checker {

    public static boolean checkIntegrity(String... strs) {
        for(String str:strs){
            if(StringUtils.isEmpty(str)){
                return false;
            }
        }
        return true;
    }
}
