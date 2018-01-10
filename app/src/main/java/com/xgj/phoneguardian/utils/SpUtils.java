package com.xgj.phoneguardian.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/8 10:23
 * @brief:sp存储数据
 */
public class SpUtils {


    /**
     * 存储字符串到SP中
     * @param key
     * @param value
     */
    public static void putString(String key,String value){
        SharedPreferences sharedPreferences = UiUtils.getContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.commit();
    }

    /**
     * 获取SP中存储的字符串
     *
     * @param key
     * @param defValue 默认值
     * @return
     */
    public static String getString(String key,String defValue){
        SharedPreferences sharedPreferences = UiUtils.getContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }







    /**
     * 主要用于判断是否是第一次进入手机防盗页面
     * 存储布尔值到SP中
     * @param key
     * @param value
     */
    public static void putBoolean(String key,boolean value){
        SharedPreferences sharedPreferences = UiUtils.getContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key,value).commit();
    }

    /**
     * 主要用于判断是否是第一次进入手机防盗页面
     * 获取SP中存储的布尔值
     * @param key
     * @param defValue 默认值
     * @return
     */
    public static boolean getBoolean(String key,boolean defValue){
        SharedPreferences sharedPreferences = UiUtils.getContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }





}
