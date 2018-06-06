package com.aitangba.test.datasecurity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fhf11991 on 2018/6/6
 */
public class AESCoder {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String CODE_MODEL = "AES/CBC/PKCS5Padding";

    public static void main(String[] args) {
        try {
            String key = "ThisIsASecretKey";
            String content = "1234567890123456";
            System.out.println("原文：" + content);
            String cipherText = encrypt(key, "1234567890123456");
            System.out.println("16进制的密文：" + cipherText);
            System.out.println("decrypted value:" + (decrypt(key, cipherText)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * AES加密
     * @param secretKeyStr
     * @param content
     * @return 16进制数据
     * @throws GeneralSecurityException
     */
    public static String encrypt(String secretKeyStr, String content) throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        keyGenerator.init(128, new SecureRandom(secretKeyStr.getBytes(CHARSET)));// 利用用户密码作为随机数初始化出
        SecretKey secretKey = keyGenerator.generateKey();// 根据用户密码，生成一个密钥
        byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回

        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance(CODE_MODEL);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));

        return parseByte2HexStr(cipher.doFinal(content.getBytes(CHARSET)));
    }

    /**
     * AES解密
     * @param secretKeyStr
     * @param encryptedContentStr
     * @return
     * @throws GeneralSecurityException
     */
    public static String decrypt(String secretKeyStr, String encryptedContentStr) throws GeneralSecurityException {
        byte[] encryptedContent = parseHexStr2Byte(encryptedContentStr);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(secretKeyStr.getBytes(CHARSET));
        keyGenerator.init(128, random);
        SecretKey secretKey = keyGenerator.generateKey();// 根据用户密码，生成一个密钥
        byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥

        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance(CODE_MODEL);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        byte[] original = cipher.doFinal(encryptedContent);
        return new String(original, CHARSET);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
