package com.aitangba.test.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fhf11991 on 2017/5/22.
 */
public class UrlTest {

    public static void main(String[] args) {
        System.out.printf(getHD("http://pic4.40017.cn/zzy/rimage/2017/01/12/04/wnqa6H.jpg"));
    }

    private final static String THUMBNAIL_SUFFIX = "_175x110_00"; //后缀00表示没有水印
    private final static String HD_SUFFIX = "_375x235_00"; //后缀00表示没有水印
    private final static String REGEX = "(.png)|.jpg";

    public static String getHD(String url) {
        return getMatcher(url, HD_SUFFIX);
    }

    private static String getMatcher(String url, String suffix) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find() && matcher.groupCount() == 1) {
            String str = matcher.group(matcher.groupCount() - 1);
            return url.replace(str, suffix + str);
        } else {
            return url;
        }
    }
}
