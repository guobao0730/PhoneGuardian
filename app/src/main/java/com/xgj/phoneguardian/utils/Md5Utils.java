package com.xgj.phoneguardian.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/8 11:30
 * @brief: MD5加密处理
 */
public class Md5Utils {

    /**
     * 利用MD5进行加密处理的方法
     * @param str
     * @return
     */
    public static String md5(String str){
        //实例化缓冲字符串用于拼接
        StringBuilder mess = new StringBuilder();
        try {
            //获取MD5加密器
            MessageDigest md = MessageDigest.getInstance("MD5");
            //将传入的字符串转换为字节数组
            byte[] bytes = str.getBytes();
            //对该字节数据进行加密算法
            byte[] digest = md.digest(bytes);

            for (byte b : digest){
                //再把每个字节转成16进制数（二次加密）
                int d = b & 0xff;// 0x000000ff
                String hexString = Integer.toHexString(d);
                if (hexString.length() == 1) {//字节的高4位为0
                    hexString = "0" + hexString;
                }
                mess.append(hexString);//把每个字节对应的2位十六进制数当成字符串拼接一起

            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return mess + "";
    }
}
