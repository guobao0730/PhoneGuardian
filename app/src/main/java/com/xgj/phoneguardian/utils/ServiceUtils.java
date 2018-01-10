package com.xgj.phoneguardian.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/12 12:32
 * @brief: 服务的工具
 */
public class ServiceUtils {


    /**
     * 该方法是判断指定的服务是否开启
     * serviceName 传入到判断的服务名
     * @return
     */
    public static boolean isStartService(String serviceName){

        boolean isStartService  = false;

        //实例化 Activity 管理器
        ActivityManager activityManager = (ActivityManager) UiUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);

        //获取手机正在运行的服务集合，50表示最大的服务个数
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(50);

        for (int i = 0; i <runningServices.size(); i++) {
            //获取正在运行的服务信息类对象
            ActivityManager.RunningServiceInfo runningServiceInfo = runningServices.get(i);

            String className = runningServiceInfo.service.getClassName();
            Log.i("ServiceUtils","正在手机上运行的服务名："+className);

            //如果正在运行的服务是传入的服务名
            if (serviceName.equals(className)){
                //那么将当前的布尔值改为true
                isStartService = true;

                //找到后跳出循环体
                break;

            }

        }

        return isStartService;


    }


}
