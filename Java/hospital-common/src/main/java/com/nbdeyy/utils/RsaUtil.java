package com.nbdeyy.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

@Slf4j
public class RsaUtil {

    // 密钥长度
    private final static int KEY_SIZE = 2048;

    // RSA算法
    private final static String ALGORITHM = "RSA";

    private static final String HASH_ALGORITHM = "SHA-256";

    // 签名算法 - 使用SHA256
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    // 字符编码
    private final static String ENCODING = "UTF-8";

    // RSA最大加密明文大小（2048位密钥）
    private static final int MAX_ENCRYPT_BLOCK = 245;

    // RSA最大解密密文大小（2048位密钥）
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * 生成密钥对
     *
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("publicKey", publicKeyString);
        keyMap.put("privateKey", privateKeyString);

        return keyMap;
    }
    public static String getStringPublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    /**
     * 从字符串获取公钥
     */
    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从字符串获取私钥
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 公钥加密
     */
    public static String encrypt(String plainText, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] data = plainText.getBytes(ENCODING);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;

        while (inputLen - offset > 0) {
            byte[] cache;
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            offset += MAX_ENCRYPT_BLOCK;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 私钥解密
     */
    public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;

        while (inputLen - offset > 0) {
            byte[] cache;
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            offset += MAX_DECRYPT_BLOCK;
        }

        byte[] decryptedData = out.toByteArray();
        out.close();

        return new String(decryptedData, ENCODING);
    }

    /**
     * 私钥签名（使用SHA256）
     */
    public static String sign(String plainText, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(plainText.getBytes(ENCODING));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 公钥验签（使用SHA256）
      * @param timestamp 前端传来的时间戳字符串形式
     * @param clientPublicKey 客户端公钥字符串(去掉pem格式头尾)
     * @param content 加密后的json内容
     * @param signature 前端对(content+timestamp)进行签名后的内容
     * @return
     * @throws Exception
     */
    public static boolean verify(String timestamp, String clientPublicKey, String content, String signature) throws Exception {
        // 复现前端签名过程，然后对比前端传过来的签名数据

        // 待签名数据拼接
        String plainText = content + timestamp;

        // 1.先对数据进行SHA256哈希(与前端一致)
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashBytes = digest.digest(plainText.getBytes(ENCODING));

        // 2. 将哈希值转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        String hashHex = hexString.toString();

        // 准备公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(clientPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 3. 使用SHA256WithRSA算法验证签名
        Signature signatureObj = Signature.getInstance(SIGN_ALGORITHM);
        signatureObj.initVerify(publicKey);
        // 4. 对哈希字符进行验证
        signatureObj.update(hashHex.getBytes(ENCODING));

        return signatureObj.verify(Base64.getDecoder().decode(signature));
    }
}