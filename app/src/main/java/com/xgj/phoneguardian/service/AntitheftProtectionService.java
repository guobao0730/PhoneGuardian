package com.xgj.phoneguardian.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.receiver.MyDeviceAdmin;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： service
 * @date：2016/8/12 12:27
 * @brief: 防盗保护的服务（注册广播监听系统发出的短信到来的广播）
 */
public class AntitheftProtectionService extends Service {


    private SmsReceiver mSmsReceiver;

    /**
     * 再服务中创建的广播
     * 主要用于监听本手机所接收到的短信信息
     *
     */
    private class SmsReceiver extends BroadcastReceiver{
        /**
         * 当广播注册后
         * 当监听到系统发出的短信到来的是广播时调用此方法
         * @param context
         * @param intent
         */

        @Override
        public void onReceive(Context context, Intent intent) {

            UiUtils.showToast("短信来啦");

            //获取监听到的额外信息对象
            Bundle extras = intent.getExtras();
            //固定写法,pdu表获取1条，pdus表示获取多条短信Object数组,根据Key/键获取对应的数据
            Object[] objects = (Object[]) extras.get("pdus");


            for(Object object:objects){

                //实例化短信管理器对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);

                //获取短信发送者的手机号码
                String phoneNumber = smsMessage.getOriginatingAddress();
                //获取短信的内容
                String messageBody = smsMessage.getMessageBody();

                Log.i("防盗保护广播","发送者的手机号："+phoneNumber+"\t短信内容："+messageBody);

                //获取安全号码
                String securityNumber = SpUtils.getString(Constant.SECURITY_NUMBER, "");

                //如果短信发送者的手机号是安全号码，那么就判断发送的的短信内容
                if(phoneNumber.equals(securityNumber)){

                    //如果短信的内容是GPS
                    if (messageBody.equals("#*gps*#")){

                        Log.i("SmsReceiver","收到了GPS的短信");

                        //那么开启获取当前位置的服务（因为是耗时的且需精准度的操作所有利用服务完成）
                        Intent intent1 = new Intent(UiUtils.getContext(),LocationService.class);
                        startService(intent);

                        //为了实现隐藏刚刚给安全号码发送的这条短信，那么就终止广播
                        abortBroadcast();

                        //如果收到的短信内容是 锁屏
                    }else if(messageBody.equals("#*lockscreen*#")){

                        Log.i("SmsReceiver","收到了锁屏的短信");
                        //实例化设备策略管理器
                        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

                        ComponentName componentName = new ComponentName(UiUtils.getContext(), MyDeviceAdmin.class);

                        //如果当前应用已经激活了设备管理器权限，
                        if (devicePolicyManager.isAdminActive(componentName)){
                            //重设密码为123
                            devicePolicyManager.resetPassword("123",0);
                            //那么就锁屏
                            devicePolicyManager.lockNow();
                        }else{
                            //否则帮助用户打开设备管理器的页面 以激活设备管理器权限
                            Intent intent2 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
                            //intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"测试测试...");  //这行代码可以不要
                            //注：在广播中开启Activity要设置旗标
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);

                            //再次给安全号码发送一条短信让其在发送一次锁屏的短信
                            String securityNumber2 = SpUtils.getString(Constant.SECURITY_NUMBER, "");
                            PhoneSystemUtils.sendSMS(securityNumber2,"请再次发送锁屏的短信");

                        }

                        //为了实现隐藏刚刚给安全号码发送的这条短信，那么就终止广播
                        abortBroadcast();


                        //如果收到的短息是远程清除数据
                    }else if (messageBody.equals("#*wipedata*#")){

                        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

                        //清除SD卡中数据
                        devicePolicyManager.wipeData(0);


                        //为了实现隐藏刚刚给安全号码发送的这条短信，那么就终止广播
                        abortBroadcast();


                        //如果是播放音乐
                    }else if(messageBody.equals("#*music*#")){

                        //实例化媒体播放器对象，传入上下文和 音乐文件
                        MediaPlayer mediaPlayer = MediaPlayer.create(UiUtils.getContext(), R.raw.qqqg);
                        //设置音量
                        mediaPlayer.setVolume(1,1);
                        //开始播放
                        mediaPlayer.start();

                    }

                }



            }


        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }


    @Override
    public void onCreate() {
        super.onCreate();

        //当服务创建的时候注册广播
        mSmsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //设置该广播的优先级为Int的最大值
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        registerReceiver(mSmsReceiver,intentFilter);

        Log.i("防盗保护服务","监听SMS短信的广播已开启");


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //当服务销毁的时候取消广播
        unregisterReceiver(mSmsReceiver);

        Log.i("防盗保护服务","监听SMS短信的广播已关闭");


    }
}
