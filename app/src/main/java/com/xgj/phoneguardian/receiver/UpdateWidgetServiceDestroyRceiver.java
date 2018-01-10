package com.xgj.phoneguardian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xgj.phoneguardian.service.UpdateWidgetService;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.receiver
 * @date： 2017/11/18 16:18
 * @brief: UpdateWidgetService销毁的广播
 */

public class UpdateWidgetServiceDestroyRceiver extends BroadcastReceiver {

    public static final String ACTION = "com.xgj.phoneguardian.receiver.UpdateWidgetServiceDestroyRceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent intent1 = new Intent(context, UpdateWidgetService.class);
            context.startService(intent1);
        }

    }
}
