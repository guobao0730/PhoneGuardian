package com.xgj.phoneguardian.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xgj.phoneguardian.service.UpdateWidgetService;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.SpUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.receiver
 * @date： 2017/10/10 10:36
 * @brief: 窗体小部件所需要配置的广播
 * AppWidgetProvider 该类其实是BroadcastReceiver的子类
 * 因为该窗体小部件需要每隔半小时就更新一次UI数据，而这个窗体小部件是脱离于Activity存在的，它不会因为Activity的退出或者应用退出就不更新数据了，
 * 这个时候就需要通过监听窗体小部件的生命周期，然后通过其生命周期的特性开启或者关闭服务
 * 而在这个生命周期当中，一旦窗体小部件出现那么就开启服务，当手机当前该窗体小部件全部移除了那么就可以关闭服务了
 */
public class MyAppWidgtProvider extends AppWidgetProvider {


    private static final String TAG = "MyAppWidgtProvider";

    /**
     * 身为广播所需要重写的函数
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtils.i(TAG,"onReceive方法被调用了");
    }


    /**
     * 创建第一个窗体小部件的时候后调用
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        LogUtils.i(TAG,"onEnabled--创建第一个窗体小部件的时候调用");
        SpUtils.putBoolean(Constant.UPDATE_WIDGET,true);
        //当窗体小部件创建的时候开启更新窗体小部件中数据的服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onEnabled(context);
    }

    /**
     * 再次创建窗体小部件的时候调用
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LogUtils.i(TAG,"onUpdate--再次创建窗体小部件的时候调用");
        SpUtils.putBoolean(Constant.UPDATE_WIDGET,true);
        //当窗体小部件创建的时候开启更新窗体小部件中数据的服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 当窗体小部件的宽高发生改变的时候调用
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        LogUtils.i(TAG,"onUpdate--当窗体小部件的宽高发生改变的时候调用");
        SpUtils.putBoolean(Constant.UPDATE_WIDGET,true);
        //当窗体小部件创建的时候开启更新窗体小部件中数据的服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    /**
     * 当每删除一个窗体小部件的时候就调用该方法
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        LogUtils.i(TAG,"onDeleted--当每删除一个窗体小部件的时候就调用该方法");
        super.onDeleted(context, appWidgetIds);
    }


    /**
     * 删除最后一个窗体小部件的时候调用该方法
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        LogUtils.i(TAG,"onDisabled--删除最后一个窗体小部件的时候调用该方法");
        SpUtils.putBoolean(Constant.UPDATE_WIDGET,false);
        //当最后一个窗体小部件被销毁的时候关闭更新窗体小部件中数据的服务
        context.stopService(new Intent(context,UpdateWidgetService.class));
        super.onDisabled(context);
    }




    /*
创建一个窗体小部件的时候调用的所有方法
10-17 11:21:57.667 8686-8686/? I/MyAppWidgtProvider: onEnabled--创建第一个窗体小部件的时候调用
10-17 11:21:57.677 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
10-17 11:21:57.687 8686-8686/? I/MyAppWidgtProvider: onUpdate--再次创建窗体小部件的时候调用
10-17 11:21:57.687 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
10-17 11:21:57.737 8686-8686/? I/MyAppWidgtProvider: onUpdate--当窗体小部件的宽高发生改变的时候调用
10-17 11:21:57.737 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
     */

    /*
    再次创建一个窗口小部件的时候调用的方法
10-17 11:23:54.867 8686-8686/? I/MyAppWidgtProvider: onUpdate--再次创建窗体小部件的时候调用
10-17 11:23:54.887 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
10-17 11:23:54.937 8686-8686/? I/MyAppWidgtProvider: onUpdate--当窗体小部件的宽高发生改变的时候调用
10-17 11:23:54.937 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
     */

    /*
    删除其中一个窗体小部件时所调用的方法
10-17 11:25:31.747 8686-8686/? I/MyAppWidgtProvider: onDeleted--当每删除一个窗体小部件的时候就调用该方法
10-17 11:25:31.747 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
     */

    /*
    删除最后一个窗体小部件的时候调用的方法
10-17 11:27:00.157 8686-8686/? I/MyAppWidgtProvider: onDeleted--当每删除一个窗体小部件的时候就调用该方法
10-17 11:27:00.157 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
10-17 11:27:00.167 8686-8686/? I/MyAppWidgtProvider: onDisabled--删除最后一个窗体小部件的时候调用该方法
10-17 11:27:00.167 8686-8686/? I/MyAppWidgtProvider: onReceive方法被调用了
     */

}
