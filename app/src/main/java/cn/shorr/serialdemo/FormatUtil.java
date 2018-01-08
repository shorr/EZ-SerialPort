package cn.shorr.serialdemo;

import java.io.UnsupportedEncodingException;

public class FormatUtil {


    /**
     * 字符串转换成十六进制字符串
     *
     * @param str
     * @param charsetName
     * @return
     */
    public static String strToHexStr(String str, String charsetName) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = new byte[0];
        try {
            bs = str.getBytes(charsetName);
            int bit;
            for (int i = 0; i < bs.length; i++) {
                bit = (bs[i] & 0x0f0) >> 4;
                sb.append(chars[bit]);
                bit = bs[i] & 0x0f;
                sb.append(chars[bit]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    /**
     * byte[]转换为16进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串补齐
     *
     * @param source     源字符串
     * @param fillLength 补齐长度
     * @param fillChar   补齐的字符
     * @param isLeftFill true为左补齐，false为右补齐
     * @return
     */
    public static String stringFill(String source, int fillLength, char fillChar, boolean isLeftFill) {
        try {
            int sourceLen = source.getBytes("GBK").length;
            if (source == null || sourceLen >= fillLength) return source;
            StringBuilder result = new StringBuilder(fillLength);
            int len = fillLength - sourceLen;
            if (isLeftFill) {
                for (; len > 0; len--) {
                    result.append(fillChar);
                }
                result.append(source);
            } else {
                result.append(source);
                for (; len > 0; len--) {
                    result.append(fillChar);
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}