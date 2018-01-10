package com.xgj.phoneguardian.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.xgj.phoneguardian.db.BlacklistDao;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.service
 * @date： 2017/8/11 14:48
 * @brief: 黑名单拦截服务(在服务完成拦截短信和拦截电话的服务)
 * 注：拦截短信需要利用广播来完成，而拦截电话需要TelephonyManager来完成
 * 注：该拦截短信的服务值针对于模拟器有效，真机无效 ,拦截电话对真机有效
 */
public class BlackListService extends Service {

    private static final int INTERCEPT_MODEL =0x001 ;
    private static final String TAG ="BlackListService" ;
    private InterceptMessageReceiver mInterceptMessageReceiver;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case INTERCEPT_MODEL:
                    mModel = (String) msg.obj;

                    //如果是拦截电话或者拦截所有的模式
                    if (mModel.equals(Constant.INTERCEPT_PHONE)|| mModel.equals(Constant.INTERCEPT_ALL)){
                        //拦截电话
                        interceptPhone();
                    }

                    break;
            }

        }

    };



    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListene mMyPhoneStateListene;
    private String mModel;
    private MyContentObserver mMyContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //监听短信
        LogUtils.i(TAG,"拦截服务已开启-onCreate");
        //当服务被创建的时候注册广播拦截收到的短信
        // "android.provider.Telephony.SMS_RECEIVED" 表示拦截短信的事件
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //设置该广播的优先级为1000（最大级别）
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        mInterceptMessageReceiver = new InterceptMessageReceiver();
        registerReceiver(mInterceptMessageReceiver,intentFilter);


        //监听电话
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mMyPhoneStateListene = new MyPhoneStateListene();
        mTelephonyManager.listen(mMyPhoneStateListene,PhoneStateListener.LISTEN_CALL_STATE);



    }

    /**
     * 自定义一个监听收到短信的广播
     */
    class InterceptMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取监听到的额外信息对象
            Bundle extras = intent.getExtras();
            //固定写法,pdu表获取1条，pdus表示获取多条短信Object数组,根据Key/键获取对应的数据
            Object[] objects = (Object[]) extras.get("pdus");
            //遍历获取的短信对象数组
            for(Object object:objects) {
                //实例化短信管理器对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //获取短信发送者的手机号码
                String phoneNumber = smsMessage.getOriginatingAddress();
                //获取短信的内容
                String messageBody = smsMessage.getMessageBody();

                //根据当期短信发送者的手机号去黑名单数据库搜索其对应的拦截模式
                LogUtils.i(TAG,"phoneNumber:"+phoneNumber+"\tmessageBody:"+messageBody);
                getModel(phoneNumber);

                //如果当前的手机号是拦截短信（1）或者拦截所有的模式（3）
                if (mModel.equals(Constant.INTERCEPT_MESSAGES)|| mModel.equals(Constant.INTERCEPT_ALL)){
                    //那么拦截短信
                    abortBroadcast();
                }

            }
        }
    }

    /**
     * 根据来电号码去黑名单数据库查询该号码的拦截模式
     * @param phoneNumber
     */
    private void getModel(final String phoneNumber) {
        //因为数据库查询是耗时操作，所以需要开辟一个子线程来完成
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //根据手机号去黑名单数据库查询拦截模式
                String model = BlacklistDao.getInstance(UiUtils.getContext()).getModel(phoneNumber);
                LogUtils.i(TAG,"model:"+model);
                Message message = mHandler.obtainMessage();
                message.obj = model;
                message.what = INTERCEPT_MODEL;
                mHandler.sendMessage(message);
            }
        });

    }

    class MyPhoneStateListene extends PhoneStateListener {
        /**
         * 在呼叫状态改变
         * 当呼叫状态发生改变的时候调用
         * @param state 状态码
         * @param incomingNumber 表示来电号码
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://挂断电话，闲置的状态
                    LogUtils.i(TAG,"挂断电话，闲置的状态");
                    //实现当电话挂断的时候销毁吐司
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机状态,正在通话中的状态
                    LogUtils.i(TAG,"摘机状态,正在通话中的状态");
                    break;
                case TelephonyManager.CALL_STATE_RINGING://电话响起的状态
                    LogUtils.i(TAG,"电话响起了");
                    //当电话响起的时候查询当前的电话是否存在于黑名单数据库中，
                    // 如果存在并且当前的拦截模式为拦截电话或者拦截所有的模式，那么拦截该电话
                    getModel(incomingNumber);

                    //同时删除来电号码的来电记录
                   /* <!--删除通话记录所需要的权限-->
                    <uses-permission android:name="android.permission.READ_CALL_LOG"></uses-permission>
                    <uses-permission android:name="android.permission.WRITE_CALL_LOG"></uses-permission>*/
                    //在内容解析器上去注册内容观察者，通过内容观察者去观察数据库
                    mMyContentObserver = new MyContentObserver(new Handler(), incomingNumber);
                    getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true, mMyContentObserver);

                    break;
            }

        }
    }





    /**
     * 拦截电话
     * 拦截电话需要用到的权限
     <uses-permission android:name="android.permission.CALL_PHONE"/>
     */
    private void interceptPhone() {
        //那么拦截电话
        //因为在android 2.2之后android将挂断电话的函数endCall()放进了名为ITelephony的aidl文件中，
        // 那么就需要将该aidl文件导入到该工程中的指定包下com.android.internal.telephony下
        //并将名为NeighboringCellInfo的aidl关联文件导入到android.telephony下

        //	ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
        //又因为ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用

        try {
            //通过全类名android.os.ServiceManager获取当前的class对象
            Class<?> aClass = Class.forName("android.os.ServiceManager");
            //根据class对象获取当前的指定的函数对象,
            // "getService"表示ServiceManager类中的函数名getService(),
            // String.class表示getService函数中要传入的参数类型class对象,因为要传入的是Context.TELEPHONY_SERVICE，而其就是一个字符串常量，所有传入String.class
            Method getService = aClass.getMethod("getService", String.class);
            try {
                //invoke表示调用当前的函数
                // 参数1：调用该函数的对象,因为该函数是静态方法，直接用类名调用，所以可以不用传该函数的对象 ServiceManager.getService(Context.TELEPHONY_SERVICE)
                // 参数2：表示调用这个函数要填入的参数
                IBinder iBinder = (IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

                try {
                    //调用在aidl文件中隐藏的endCall()实现拦截电话的操作
                    iTelephony.endCall();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }




    }

    class MyContentObserver extends ContentObserver{

        private String phoneNumer;
        /**
         * Creates a content observer.
         *phoneNumer：构造的时候传入手机号
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String phoneNumer) {
            super(handler);
            this.phoneNumer = phoneNumer;
        }


        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            //再次删除通话记录,根据手机号去删除通话记录
            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new String[]{phoneNumer});

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG,"拦截服务已销毁-onDestroy");
        //当服务被销毁的时候取消对短信的监听
        if (mInterceptMessageReceiver!=null){
            unregisterReceiver(mInterceptMessageReceiver);
        }


        //当服务销毁的时候，取消对电话的监听
        if (mTelephonyManager!=null&&mMyPhoneStateListene!=null){
            //取消对电话的监听
            mTelephonyManager.listen(mMyPhoneStateListene,PhoneStateListener.LISTEN_NONE);
        }


        //当服务销毁的时候注销观察者(用于删除通话记录的)
        if (mMyContentObserver!=null){
            getContentResolver().unregisterContentObserver(mMyContentObserver);
        }
    }


}
