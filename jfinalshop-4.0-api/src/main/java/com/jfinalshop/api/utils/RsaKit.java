package com.jfinalshop.api.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 非对称密钥工具类
 *
 * @author: polfdark
 */
public class RsaKit {

    private RsaKit(){}
    /**
     *
     * @param priData 私钥数组
     * @return 私钥实例
     */
    public static PrivateKey loadPriKey(byte[] priData)  {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(priData);
        KeyFactory keyf = null;
        PrivateKey privateKey = null;
        try {
            keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     *
     * @param pubData 公钥数组
     * @return 公钥实例
     */
    public static PublicKey loadPubKey(byte[] pubData){
        X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(pubData);
        KeyFactory keyFactory = null;
        PublicKey publicKey = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(bobPubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            //找不到算法
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            //导入key失败
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     *
     * @param pubData
     * @return
     */
    public static PublicKey loadPubKey(String pubData){
        return loadPubKey(HexKit.hexStrToBytes(pubData));
    }

    /**
     *
     * @param priData
     * @return
     */
    public static PrivateKey loadPriKey(String priData){
        return loadPriKey(HexKit.hexStrToBytes(priData));
    }

    public static byte[] encrypt(byte[] data,PublicKey publicKey){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(1, publicKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] decrypt(byte[] data,PrivateKey privateKey){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(2, privateKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] decrypt(String data,PrivateKey privateKey){
       return decrypt(HexKit.hexStrToBytes(data),privateKey);
    }

    public static KeyPair generateRSAKeyPair()
    {
        return generateRSAKeyPair(1024);
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength
     *            密钥长度，范围：512～2048<br>
     *            一般1024
     * @return
     */
    public static KeyPair generateRSAKeyPair(int keyLength)
    {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

/*    public static boolean verify(String clearText,String signText,String publicKey){
        Signature signature = null;
        byte[] signByte = HexKit.hexStrToBytes(signText);
        try {
            signature = Signature.getInstance("SHA/DSA");
            signature.initVerify(RsaKit.loadPubKey(publicKey));
            signature.update(clearText.getBytes("utf-8"));
            return signature.verify(signByte);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }*/

/*    public static String sign(String clearText,String privateKey){
        PrivateKey privatekey = RsaKit.loadPriKey(privateKey);
        String result = null;
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA/DSA");
            signature.initSign(privatekey);
            MessageDigest messagedigest = MessageDigest.getInstance("SHA");
            byte[] text = messagedigest.digest(clearText.getBytes());
            signature.update(text);
            byte[] signByte = signature.sign();
            result = HexKit.bytesToHexStr(signByte);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return result;
    }
    */

    public static String sign(String clearText,String privateKey){
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(HexKit.hexStrToBytes(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
            Signature signet = Signature.getInstance("MD5withRSA");
            signet.initSign(myprikey);
            signet.update(clearText.getBytes("utf-8"));
            byte[] signed = signet.sign();
            return HexKit.bytesToHexStr(signed);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verify(String clearText,String signText,String publicKey){
        try {
            // 解密由base64编码的公钥,并构造X509EncodedKeySpec对象
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(HexKit.hexStrToBytes(publicKey));
            // RSA对称加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            // 解密由base64编码的数字签名
            byte[] signed = HexKit.hexStrToBytes(signText);
            Signature signatureChecker = Signature.getInstance("MD5withRSA");
            signatureChecker.initVerify(pubKey);
            signatureChecker.update(clearText.getBytes("utf-8"));
            // 验证签名是否正常
            if (signatureChecker.verify(signed))
                return true;
            else
                return false;
        } catch (Throwable e) {
            System.out.println("校验签名失败");
            e.printStackTrace();
            return false;
        }
    }
}
