package com.xgj.phoneguardian.utils;

import android.util.Log;

/**
 * @author  郭宝
 * @project： SensitivityFactor
 * @package： com.xgj.utils
 * @date： 2016/10/19 9:55
 * @brief:
 */
public class LogUtils {

    private LogUtils(){}


    public static boolean isShow = true;

    public static  void i(String tag,String message){
        if (isShow){
            Log.i(tag,message);
        }
    }


    public static void e(String tag,String message){

        if (isShow){
            Log.e(tag,message);
        }
    }


    public static void v(String tag,String message){

        if (isShow){
            Log.v(tag,message);
        }
    }


    public static void w(String tag,String message){

        if (isShow){
            Log.w(tag,message);
        }
    }



    public static void d(String tag,String message){

        if (isShow){
            Log.d(tag,message);
        }
    }


}
