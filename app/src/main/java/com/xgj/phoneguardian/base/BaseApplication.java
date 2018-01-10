package com.xgj.phoneguardian.base;

import android.app.Application;
import android.content.Context;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/7 0007 9:42
 * @des ${定义一个全局的盒子，里面放置的对象，属性，方法都是全局可以调用的}
 */
public class BaseApplication extends Application {

    private static Context mContext;


    //注意要在清单文件中配置BaseApplication
    @Override
    public void onCreate() {
        super.onCreate();
        //将获取到的上下文复制给全局
        mContext = this.getApplicationContext();

    }


    public static Context getContext() {
        return mContext;
    }


}
