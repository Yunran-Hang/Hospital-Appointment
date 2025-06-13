package com.nbdeyy.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Md5Util {
    // 固定盐值
    private static final String SALT = "NINGBODIERYIYUAN";

    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            log.error(Md5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。", nsaex);
        }
    }

    /**
     * 生成字符串的md5校验值（自动加盐）
     *
     * @param s 原始字符串
     * @return 加盐并加密后的字符串
     */
    public static String getMD5StringWithSalt(String s) {
        return getMD5String((s + SALT).getBytes());
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配（考虑盐值）
     *
     * @param password   要校验的字符串
     * @param md5PwdStr 已知的md5校验码
     * @return 是否匹配
     */
    public static boolean checkPasswordWithSalt(String password, String md5PwdStr) {
        String s = getMD5StringWithSalt(password);
        log.info("password: {}",password);
        log.info("md5PwdStr = {}", md5PwdStr);
        log.info("MD5Util.getMD5String(password) with salt = {}", s);
        return s.equals(md5PwdStr);
    }

    // 现有的getMD5String方法保持不变
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static void main(String[] args) {
        System.out.println(getMD5StringWithSalt("123456"));
    }
}