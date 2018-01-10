package com.xgj.phoneguardian.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.receiver
 * @date：2016/8/15 17:08
 * @brief: 实现在安装次程序时就默认注册了监听Sim卡是否改变的广播，以便手机卡发生改变的时候通知安全号码，那么就需要直接在清单文件中注册
 * 注意监听开机启动要加权限： <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"></uses-permission>
 */
public class SimChangeReceiver extends BroadcastReceiver {

    /**
     * 当监听到手机开机后调用此方法检测SIM卡是否发生变化
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        UiUtils.showToast("手机开机了");

        //获取以前引导页面2存储的SIM卡的序列号
        String oldSimNumber = SpUtils.getString(Constant.SIM, "");

        //获取当前的SIM卡的序列号
        TelephonyManager telephonyManager = (TelephonyManager) UiUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = telephonyManager.getSimSerialNumber();


        //如果SIM卡不一致那么发送短信到安全号码
        // TODO 注意+1位测试所用
        if (!oldSimNumber.equals(simSerialNumber)){

            //获取安全号码
            String securityNumber = SpUtils.getString(Constant.SECURITY_NUMBER, "");

            //获取当前的手机号
            String currentPhoneNumber = PhoneSystemUtils.getCurrentPhoneNumber();

            //发送短信
            //注意需加发送短信的权限<uses-permission android:name="android.permission.SEND_SMS"></uses-permission>
            PhoneSystemUtils.sendSMS(securityNumber,"亲爱的用户，手机卫士检测到您的手机SIM卡被更换，当前号码为"+currentPhoneNumber+"，如果手机被盗，请登录http://m.qq.com/anti_theft/  进行远程控制");

        }



    }
}
