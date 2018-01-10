package com.xgj.phoneguardian.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xgj.phoneguardian.engine.ProcessInformationProvider;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.service
 * @date： 2017/9/4 16:07
 * @brief: 锁屏服务
 */
public class LockScreenSevice extends Service {

    private LockScreenReceiver mLockScreenReceiver;
    private IntentFilter mIntentFilter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //当服务创建的时候开启广播实现监听手机锁屏的状态
        mIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mLockScreenReceiver = new LockScreenReceiver();
        //注册广播
        registerReceiver(mLockScreenReceiver, mIntentFilter);
    }

    /**
     * 锁屏广播
     */
    class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            //当监听到锁屏的时候杀死当前所有进程（除了本应用以外）
            ProcessInformationProvider.killAllProcess(context);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //实现当服务销毁的时候注销广播
        if (mLockScreenReceiver!=null&&mIntentFilter!=null){
            unregisterReceiver(mLockScreenReceiver);
        }
    }




}
