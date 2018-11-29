package com.jfinalshop.api.utils;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * aes and rsa protocol
 * @author: polfdark
 *
 */
public class AARProtocol {

    /**
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String encrypt(byte[] data,String publicKey){
        PublicKey pub = RsaKit.loadPubKey(publicKey);
        byte[] aesKey = AesKit.getRawKey(System.currentTimeMillis(),128);
        byte[] encrypt = AesKit.encrypt(data,aesKey);
        byte[] aesEncrypt = RsaKit.encrypt(aesKey,pub);
        return HexKit.bytesToHexStr(aesEncrypt)+"|"+HexKit.bytesToHexStr(encrypt);
    }

    /**
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String encrypt(String data,String publicKey){
        byte[] dataByte = new byte[0];
        try
        {
            dataByte = data.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        PublicKey pub = RsaKit.loadPubKey(publicKey);
        byte[] aesKey = AesKit.getRawKey(System.currentTimeMillis(),128);
        byte[] encrypt = new byte[0];
        encrypt = AesKit.encrypt(dataByte,aesKey);
        byte[] aesEncrypt = RsaKit.encrypt(aesKey,pub);
        return HexKit.bytesToHexStr(aesEncrypt)+"|"+HexKit.bytesToHexStr(encrypt);
    }

    public static String decrypt(String request, PrivateKey privateKey){
        int idx = request.indexOf("|");
        String rsaEncrypt = request.substring(0, idx);
        String encryptBody = request.substring(idx + 1);
        byte[] aesKey = RsaKit.decrypt(rsaEncrypt,privateKey);
        byte[] body = AesKit.decrypt(encryptBody,aesKey);
        return new String(body);
    }

    public static String decrypt(String request, String privateKey){
        int idx = request.indexOf("|");
        String rsaEncrypt = request.substring(0, idx);
        String encryptBody = request.substring(idx + 1);
        PrivateKey pri = RsaKit.loadPriKey(privateKey);
        byte[] aesKey = RsaKit.decrypt(rsaEncrypt,pri);
        byte[] body = AesKit.decrypt(encryptBody,aesKey);
        try {
            return new String(body,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
