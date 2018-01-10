package com.xgj.phoneguardian.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.engine.HomeLocationDao;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.service
 * @date： 2017/7/31 17:00
 * @brief: 归属地的服务
 *   注：监听来电需要用到TelephonyManager来完成，而监听去电需要通过广播的形式来完成
 */
public class HomeLocationService extends Service{
    private static final String TAG = "HomeLocationService";
    private static final int HOME_LOCATION = 0x001;
    private MyPhoneStateListener mMyPhoneStateListener;
    private TelephonyManager mTelephonyManager;
    private WindowManager mWindowManager;
    private View mToastView;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case HOME_LOCATION :
                    String homeLocation = (String) msg.obj;
                    mToast_tv_location.setText(homeLocation);

                    break;
            }
        }
    };
    private TextView mToast_tv_location;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    private OutgoingCallReceiver mOutgoingCallReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //实现当服务创建的时候监听手机来电的状态并显示吐司

        /**
         * 读手机状态的权限,监听手机来电状态时所用
           <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
         */
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听对象，监听的类型为手机的呼叫状态PhoneStateListener.LISTEN_CALL_STATE
        mMyPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mMyPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        //获取手机屏幕的宽高
        mScreenWidth = PhoneSystemUtils.getScreenWidth(UiUtils.getContext());
        mScreenHeight = PhoneSystemUtils.getScreenHeight(UiUtils.getContext());
        //获取当前手机的状态栏高度
        mStatusBarHeight = PhoneSystemUtils.getStatusBarHeight(UiUtils.getContext());

        //注册去电的广播
        registerOutgoingCallReceiver();


    }

    /**
     * 注册去电的广播
     */
    private void registerOutgoingCallReceiver() {
        //实现当服务创建的时候开启一个监听去电的广播
        //注：监听来电需要用到TelephonyManager来完成，而监听去电需要通过广播的形式来完成
        //<!--监听去电的权限-->
        //<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"></uses-permission>
        //监听去电的过滤条件
        IntentFilter intentFilter = new IntentFilter();
        //ACTION_NEW_OUTGOING_CALL表示新推出的行动电话
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        mOutgoingCallReceiver = new OutgoingCallReceiver();
        //注册广播
        registerReceiver(mOutgoingCallReceiver,intentFilter);

    }

    /**
     * 手机状态监听器
     */
    class MyPhoneStateListener extends PhoneStateListener {

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
                    destroyToast();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机状态,正在通话中的状态
                    LogUtils.i(TAG,"摘机状态,正在通话中的状态");
                    break;
                case TelephonyManager.CALL_STATE_RINGING://电话响起的状态
                    LogUtils.i(TAG,"电话响起了");
                    //当电话响起的时候显示来电归属的吐司
                    showToast(incomingNumber);

                    break;
            }

        }
    }

    /**
     * 显示自定义的吐司
     */
    private void showToast(String phoneNumber) {

        final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
       // mParams.windowAnimations = com.android.internal.R.style.Animation_Toast;
        //将当前TYPE_TOAST类型修改为TYPE_PHONE类型
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
           //     | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; 去除不可以触摸的旗标

        //表示指定吐司显示在左上角的位置
        mParams.gravity = Gravity.LEFT+Gravity.TOP;

        //设置归属地的位置
        setAttributionLocation(mParams);


        //实例化窗口管理器，是为了将吐司视图显示在该窗体上
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //将自定义的吐司视图转换为view
        mToastView = View.inflate(UiUtils.getContext(), R.layout.toast_layout, null);
        mToast_tv_location = (TextView) mToastView.findViewById(R.id.toast_tv_location);
        //从数据库查询归属地
        query(phoneNumber);

        //设置归属地背景颜色
        setAttributionLocationBackground(mToast_tv_location);

        //实现当来电话时可以任意拖拽归属地窗体
        mToastView.setOnTouchListener(new View.OnTouchListener() {
            private int mStartY;
            private int mStartX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN://按下
                        //当按下时获取该控件距离左边距离原点（屏幕左上角（不包括通知栏））的X轴距离
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        //移动时获取该控件距离左边距离原点（屏幕左上角（不包括通知栏））的X轴距离
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        //计算出当前的移动的距离差
                        int rangeX = moveX - mStartX;
                        int rangeY = moveY - mStartY;

                        mParams.x = mParams.x+rangeX;
                        mParams.y =  mParams.y +rangeY;

                        //限制吐司视图在手机中移动的位置
                        //如果当前的吐司视图的X轴<0(也就是移动到了手机左侧以外),那么最多就把当前的X轴置为0    左
                        if (mParams.x<0){
                            mParams.x = 0;
                        }
                        //如果移动到手机上部以外的位置，那么最多将当前的Y轴置为0    上
                        if (mParams.y<0){
                            mParams.y = 0;
                        }
                        // 如果当前吐司的左上角X轴>手机宽度-吐司视图的宽度（也就是移动到实手机右边窗口以外的区域） 右
                        if (mParams.x>mScreenWidth-mToastView.getWidth()){
                            //那么当前的X轴最多置为手机宽度-吐司视图的宽度
                            mParams.x = mScreenWidth-mToastView.getWidth();
                        }
                        //当前吐司的Y轴>手机的高度-吐司视图的高度-状态栏的高度（也就是移动到手机底部以外的区域） 下
                        if (mParams.y>mScreenHeight-mToastView.getHeight()-mStatusBarHeight){
                            mParams.y = mScreenHeight-mToastView.getHeight()-mStatusBarHeight;
                        }


                        //更新吐司视图，也就是更新窗口视图
                        mWindowManager.updateViewLayout(mToastView,mParams);


                        //最后更新一下当前吐司的
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://松开

                        //当松开的时候记录当前吐司视图的左上角位置，以便下次进行设置归属地位置时同步显示上一次设置的视图
                        SpUtils.putString(Constant.ATTRIBUTION_LOCATION_X, mParams.x+"");
                        SpUtils.putString(Constant.ATTRIBUTION_LOCATION_Y, mParams.y+"");

                        break;
                }



                //注：当只给当前控件设置触摸事件时需要将此返回值设置为true,如果即想给当前控件设置点击事件又想给当前控件设置触摸事件，那么此返回值必须设置为false
                return true;
            }
        });

        //注意：在窗口添加视图时需要加权限：
        //<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"></uses-permission>
        mWindowManager.addView(mToastView,mParams);
    }

    /**
     * 设置归属地背景颜色
     */
    private void setAttributionLocationBackground(TextView tv) {
        String attribution_style = SpUtils.getString(Constant.ATTRIBUTION_STYLE, "透明");
        if (attribution_style.equals("透明")){
            tv.setBackgroundColor(Color.TRANSPARENT);
        }else if (attribution_style.equals("橙色")){
            tv.setBackgroundColor(Color.parseColor("#FF8000"));
        }else if (attribution_style.equals("蓝色")){
            tv.setBackgroundColor(Color.BLUE);
        }else if (attribution_style.equals("灰色")){
            tv.setBackgroundColor(Color.GRAY);
        }else if (attribution_style.equals("绿色")){
            tv.setBackgroundColor(Color.GREEN);
        }


    }

    /**
     * 设置归属地的位置
     * @param mParams
     */
    private void setAttributionLocation( WindowManager.LayoutParams mParams) {
        //获取在设置页面设置的归属地左上角位置以便给当前的归属地吐司设置位置
        String attribution_location_x = SpUtils.getString(Constant.ATTRIBUTION_LOCATION_X, "0");
        String attribution_location_y = SpUtils.getString(Constant.ATTRIBUTION_LOCATION_Y, "0");
        mParams.x = Integer.parseInt(attribution_location_x);
        mParams.y = Integer.parseInt(attribution_location_y);
    }

    /**
     * 销毁吐司
     */
    public void destroyToast(){
        if (mWindowManager!=null&&mToastView!=null){
            mWindowManager.removeView(mToastView);
        }
    }

    /**
     * 查询归属地
     * @param phoneNumber
     */
    public void query(final String  phoneNumber){
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                String homeLocation = HomeLocationDao.query(phoneNumber);
                Message message = mHandler.obtainMessage();
                message.obj = homeLocation;
                message.what =HOME_LOCATION;
                mHandler.sendMessage(message);
            }
        });

    }

    /**
     * 监听去电的广播
     */
    class OutgoingCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //当监听到去电的广播时获取当前去电的手机号码
            String phoneNumber = getResultData();
            LogUtils.i(TAG,"phoneNumber:"+phoneNumber);
            //当监听到去电时搜索当前手机的归属地并显示
            showToast(phoneNumber);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //当服务销毁的时候，取消对电话的监听并且隐藏吐司
        if (mTelephonyManager!=null&&mMyPhoneStateListener!=null){
            //取消对电话的监听
            mTelephonyManager.listen(mMyPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }

        //当归属地的服务销毁的同时取消对去电的监听
        if (mOutgoingCallReceiver!=null){
            unregisterReceiver(mOutgoingCallReceiver);
        }
    }
}
