package com.jfinalshop.Utils;

/**
 *
 * hexString操作类
 *
 * @author: polfdark
 *
 * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
 */
public class HexKit {

    private static final char[] bcdLookup = "0123456789abcdef".toCharArray();

    private HexKit(){}
    /**
     *
     * @param bcd bcd数组
     * @return hex字符串
     */
    public static final String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            s.append(bcdLookup[(bcd[i] >>> 4 & 0xF)]);
            s.append(bcdLookup[(bcd[i] & 0xF)]);
        }
        return s.toString();
    }

    /**
     *
     * @param clearText 明文
     * @return hex数组
     */
    public static final byte[] hexStrToBytes(String clearText) {
        byte[] bytes = new byte[clearText.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ((byte) Integer.parseInt(clearText.substring(2 * i, 2 * i + 2), 16));
        }
        return bytes;
    }
}
