package com.xgj.phoneguardian.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.engine.ProcessInformationProvider;
import com.xgj.phoneguardian.receiver.MyAppWidgtProvider;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.service
 * @date： 2017/10/17 13:58
 * @brief: 更新窗体小部件中进程总数和剩余内存的服务
 */
public class UpdateWidgetService extends Service {


    private static final int UPDATE_WIDGET = 0x001;
    private Timer mTimer;
    private TimerTask mTimerTask;
    public static final String TAG = "UpdateWidgetService";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_WIDGET:
                    updateWidget();

                    //延迟5秒再次发送一个更新数据的空消息,以实现持续性的更新数据，这样就不需要定时器来完成,并且可以解决手机锁屏后不会在执行定时器任务的BUG
                    mHandler.sendEmptyMessageDelayed(UPDATE_WIDGET,5000);
                    LogUtils.i(TAG,"每隔5秒刷新一次当前正在运行的进程个数和剩余的内存......");
                    break;
            }

        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //每隔一段时间更新一次窗体小部件中的数据，这个时候就需要利用Handler来完成
        mHandler.sendEmptyMessage(UPDATE_WIDGET);
    }


    /**
     * 更新窗体小部件中的数据
     */
    private void updateWidget() {

        //获取应用窗体小部件管理器对象
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        //获取RemoteViews对象，也就是远程视图对象，
        // 参数一：传入要定位的窗体小部件所属的应用包名
        //参数二：该窗体小部件的布局
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget);

        //获取正在运行的TextView布局，并给其设置值
        remoteViews.setTextViewText(R.id.appwidget_tv_number,"正在运行的软件："+ ProcessInformationProvider.getNumberOfProcesses(this));

        //获取手机的可用内存
        long availableMemory = ProcessInformationProvider.getAvailableMemory(this);
        //对获取的可用内存进行格式化
        String s2 = Formatter.formatFileSize(this, availableMemory);
        remoteViews.setTextViewText(R.id.appwidget_tv_freeMemory,"剩余内存为："+s2);


        //实现点击窗体小部件跳转到手机卫士的主页面(除了一键清理按钮)
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        //如果想打开一个页面那么就通过延期意图（PendingIntent）的getActivity来完成
        // 0 表示请求码
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_ll_root,pendingIntent);


        // 通过延期意图实现点击一键清理按钮后清除当前正在运行的进程
        //利用延期意图中发送自定义广播，然后在自定义广播中调用杀死所有进程的函数来实现一键清理的功能
        //"android.intent.action.KILL_ALL_PROCESSES" 表示自定义一个action的广播意图
        Intent broadcastIntent = new Intent("android.intent.action.KILL_ALL_PROCESSES");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_bt_clearMaste,broadcastPendingIntent);


        //利用AppWidgetManager对象更新窗体小部件的UI
        ComponentName componentName = new ComponentName(this, MyAppWidgtProvider.class);
        appWidgetManager.updateAppWidget(componentName,remoteViews);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.i(TAG,TAG+"服务被销毁了");
        if (SpUtils.getBoolean(Constant.UPDATE_WIDGET,false)){
            //当服务销毁的时候关闭定时器
            //为了实现该服务在后台一直运行，而不会出现当手机锁屏后该服务会被杀死的BUG，那么就通过一个自定义广播来解决该BUG
            //如果当该服务不是删除最后一个窗体小部件的时候调用的，那么就发射一个自定义广播，当接收到该广播时再次开启本服务即可
            Intent intent = new Intent("com.xgj.phoneguardian.receiver.UpdateWidgetServiceDestroyRceiver");
            sendBroadcast(intent);
        }else {
            //取消更新窗体小部件中数据的任务
            mHandler.removeMessages(UPDATE_WIDGET);
        }


    }



}
