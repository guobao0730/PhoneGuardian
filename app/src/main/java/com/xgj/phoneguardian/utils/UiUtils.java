package com.xgj.phoneguardian.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.widget.Toast;

import com.xgj.phoneguardian.base.BaseApplication;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/7 0007 9:44
 * @des ${TODO}
 */
public class UiUtils {
    /***
     * 获取上下文
     * @return
     */
    public static Context getContext(){
        return BaseApplication.getContext();
    }

    /**
     * 显示Toast
     */
    public static void showToast(String content){
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 去除标题栏
     */
    public static void removerTitle(Activity activity){
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


}
