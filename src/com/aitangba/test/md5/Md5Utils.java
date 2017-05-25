package com.aitangba.test.md5;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by fhf11991 on 2017/5/10.
 */
public class Md5Utils {

    private final static int NEGATIVE_ONE = -1;

    /**
     * Get MD5 of one file:hex string,test OK!
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != NEGATIVE_ONE) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /***
     * Get MD5 of one file！test ok!
     *
     * @param filepath
     * @return
     */
    public static String getFileMD5(String filepath) {
        File file = new File(filepath);
        return getFileMD5(file);
    }

    public static void main(String[] args) {
        System.out.println("文件md5值1：" + getFileMD5("D:\\driver_release_2.5.8.apk"));
    }

}
