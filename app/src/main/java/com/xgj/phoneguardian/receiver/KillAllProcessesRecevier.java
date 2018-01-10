package com.xgj.phoneguardian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xgj.phoneguardian.engine.ProcessInformationProvider;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.receiver
 * @date： 2017/11/5 12:45
 * @brief: 杀死所有进程的广播
 */

public class KillAllProcessesRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //当监听到自定义的广播时，调用杀死全部进程的方法"android.intent.action.KILL_ALL_PROCESSES"

        ProcessInformationProvider.killAllProcess(context);
    }
}
