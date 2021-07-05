package com.hl.hos.utils;

import org.springframework.util.DigestUtils;

public class MD5Util
{
    /**
     * 检查签名是否合法
     * @param sign
     * @return
     */
    public static boolean check_sign(String sign,String timestamp)
    {
        //先检查签名是否被篡改
        String rule = "hyx,"+timestamp;
        String my_sign = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(rule.getBytes()).getBytes());
        if(!my_sign.equals(sign))
            return false;//签名被篡改
        //检查时间是否在有效期内
        Long minute = (System.currentTimeMillis() - Long.valueOf(timestamp)) / (1000 * 60);//返回与当前时间的相差分钟
        if(minute>1)
            return false; //超过一分钟签名失效
        return true;
    }


    /**
     * 获取Md5
     * @param str
     * @return
     */
    public static String getMd5(String str)
    {

        String my_sign = DigestUtils.md5DigestAsHex(str.getBytes());
        return my_sign;
    }
}
