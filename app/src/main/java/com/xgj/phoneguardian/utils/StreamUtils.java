package com.xgj.phoneguardian.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/6 0006 11:39
 * @des ${TODO}
 */
public class StreamUtils {


    /**
     * 将流转换为字符串
     * @param inputStream
     * @return
     */
    public static String StreamToString(InputStream inputStream){

        //将字节流转换为字符流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder data = null;
        try {
            //一次读一行
            String temp = bufferedReader.readLine();
            //创建一个缓冲字符串用于拼接字符串
            data = new StringBuilder();
            while (temp!=null){

                //那么追加
                data.append(temp);
                //控制一直循环
                temp = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



        return data.toString();


    }


}
