package com.jfinalshop.api.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 对称密钥工具类
 *
 * @author: polfdark
 */
public class AesKit {

    private AesKit(){}
    /**
     *
     * @param clearText 明文
     * @param rawKey 密钥
     * @return 密文数组
     *
     */
    public static byte[] encrypt(byte[] clearText, byte[] rawKey) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(clearText);
        } catch (NoSuchAlgorithmException e) {
            // 未找到算法类型
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // Key类型错误
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // 填充错误
            e.printStackTrace();
        } catch (BadPaddingException e) {
            //填充错误
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            //数据块长度非法
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     *
     * @param encrypted 密文
     * @param rawKey 密钥
     * @return 明文数组
     */
    public static byte[] decrypt(byte[] encrypted, byte[] rawKey) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(encrypted);
        } catch (NoSuchAlgorithmException e) {
            // 未找到算法类型
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // Key类型错误
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // 填充错误
            e.printStackTrace();
        } catch (BadPaddingException e) {
            //填充错误
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            //数据块长度非法
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] decrypt(String encrypted,byte[] rawKey){
        return decrypt(HexKit.hexStrToBytes(encrypted),rawKey);
    }

    /**
     *
     * @param seed 种子
     * @param bit 长度
     * @return 密钥
     */
    public static byte[] getRawKey(long seed,int bit) {
        byte[] rawKey = new byte[0];
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
            kgen.init(bit, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            rawKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            //未找到算法
            e.printStackTrace();
        }
        return rawKey;
    }
}
