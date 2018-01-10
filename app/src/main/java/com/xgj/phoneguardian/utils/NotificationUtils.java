package com.xgj.phoneguardian.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.graphics.Color;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.utils
 * @date：2016/8/15 14:51
 * @brief: 自定义通告的工具
 */
public class NotificationUtils {


    /**
     * 自定义的通告
     * @return
     */
    public static void createNotification(int icon,String title){

        //
        NotificationManager manager=(NotificationManager)UiUtils.getContext().getSystemService(Service.NOTIFICATION_SERVICE);
        //创建Notification对象
        Notification notification=new Notification(icon,title,System.currentTimeMillis());
        //notification.setLatestEventInfo(UiUtils.getContext(), title, null,null);
        notification.ledARGB= Color.GREEN;//控制通知的led灯颜色
        notification.ledOnMS=1000;//通知灯的显示时间
        notification.ledOffMS=1000;
        notification.flags=Notification.FLAG_SHOW_LIGHTS;
        manager.notify(1,notification);//调用NotificationManager的notify方法使通知显示

    }


}
