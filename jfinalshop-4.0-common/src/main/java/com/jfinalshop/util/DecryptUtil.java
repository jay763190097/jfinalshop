package com.jfinalshop.util;

/*import com.kuark.payment.sign.AESUtil;
import com.kuark.payment.sign.RSAUtil;
import com.kuark.payment.sign.SignUtil;*/

import com.kuark.payment.sign.AESUtil;
import com.kuark.payment.sign.RSAUtil;
import com.kuark.payment.sign.SignUtil;


import java.security.PrivateKey;

/**
 * Created by lixiaoming on 15/9/10.
 */

//解密工具类
public class DecryptUtil {
    public static byte[] decrypt(String encryptData,PrivateKey privateKey) throws Exception {
        int idx = encryptData.indexOf("|");
        if(idx<0){
            idx = encryptData.indexOf("#");
            String rsaEncrypt = encryptData.substring(0,idx);
            String encryptBody = encryptData.substring(idx+1);
            byte[] aesPwd = RSAUtil.decryptRSA(privateKey, rsaEncrypt);
            byte[] body = AESUtil.decryptPure(aesPwd, SignUtil.hexStrToBytes(encryptBody));
            return body;
        }else{
            String rsaEncrypt = encryptData.substring(0,idx);
            String encryptBody = encryptData.substring(idx+1);
            byte[] aesPwd = RSAUtil.decryptRSA(privateKey, rsaEncrypt);
            byte[] body = AESUtil.decrypt(aesPwd, SignUtil.hexStrToBytes(encryptBody));
            return body;
        }
    }
}
