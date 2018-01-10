package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.service.LockScreenSevice;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.ServiceUtils;
import com.xgj.phoneguardian.utils.SpUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/9/4 10:17
 * @brief: 进程设置页面
 */
public class ProcessSetActivity extends Activity {


    private static final String TAG = "ProcessSetActivity";
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;
    private RelativeLayout mProcessSetActivity_rl_systemProcess;
    private TextView mProcessSetActivity_systemProcess_tv_switch;
    private ImageView mProcessSetActivity_systemProcess_iv_switch;
    private RelativeLayout mProcessSetActivity_rl_lockScreenCleaning;
    private TextView mProcessSetActivity_lockScreenCleaning_tv_switch;
    private ImageView mProcessSetActivity_lockScreenCleaning_iv_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process_set);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        //系统进程设置
        mProcessSetActivity_rl_systemProcess = (RelativeLayout) findViewById(R.id.processSetActivity_rl_systemProcess);
        mProcessSetActivity_systemProcess_tv_switch = (TextView) findViewById(R.id.processSetActivity_systemProcess_tv_switch);
        mProcessSetActivity_systemProcess_iv_switch = (ImageView) findViewById(R.id.processSetActivity_systemProcess_iv_switch);

        //锁屏清理设置
        mProcessSetActivity_rl_lockScreenCleaning = (RelativeLayout) findViewById(R.id.processSetActivity_rl_lockScreenCleaning);
        mProcessSetActivity_lockScreenCleaning_tv_switch = (TextView) findViewById(R.id.processSetActivity_lockScreenCleaning_tv_switch);
        mProcessSetActivity_lockScreenCleaning_iv_switch = (ImageView) findViewById(R.id.processSetActivity_lockScreenCleaning_iv_switch);

    }
    private void initData() {
        mTitleLayout_tv_title.setText("进程设置");

        //初始化当前系统进程的状态
        if (SpUtils.getBoolean(Constant.IS_SYSTEM_PROCESS,false)){
            //如果当前是显示的状态，那么将当前的UI也设置为显示的状态
            isShowSystemProcess(true);
        }else {
            isShowSystemProcess(false);
        }


        LogUtils.i(TAG,"锁屏服务："+LockScreenSevice.class.getName());
        if (ServiceUtils.isStartService(LockScreenSevice.class.getName())){
            //如果当前是开启锁屏清理的状态，那么将当前的UI设置为开启的状态
            isOpenLockScreenCleaning(true);
        }else {
            isOpenLockScreenCleaning(false);
        }

    }

    private void initEvent() {
        mTitleLayout_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //系统进程设置（其实控制系统进程的显示与隐藏可以在进程管理页面中的Adapter中的getCount()中实现，
        // 隐藏系统进程就是只显示用户进程+1个用户进程常驻悬浮框，而显示系统进程就是显示用户进程+系统进程+2个常驻悬浮框）
        mProcessSetActivity_rl_systemProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SpUtils.getBoolean(Constant.IS_SYSTEM_PROCESS,false)){
                    //如果当前是显示的，那么就隐藏系统进程
                    isShowSystemProcess(false);
                    //保存当前是隐藏的标记
                    SpUtils.putBoolean(Constant.IS_SYSTEM_PROCESS,false);
                }else {
                    //那么显示系统进程
                    isShowSystemProcess(true);

                    //保存当前是显示的标记
                    SpUtils.putBoolean(Constant.IS_SYSTEM_PROCESS,true);
                }
            }
        });

        //锁屏清理设置
        mProcessSetActivity_rl_lockScreenCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //通过判断监听手机锁屏的服务是否开启来设置当前的UI和逻辑代码
                if (ServiceUtils.isStartService(LockScreenSevice.class.getName())){
                    //如果当前锁屏清理以开启，那么就关闭
                    isOpenLockScreenCleaning(false);

                    //关闭服务
                    stopService(new Intent(ProcessSetActivity.this,LockScreenSevice.class));
                }else {
                    //那么开启锁屏清理
                    isOpenLockScreenCleaning(true);
                    //开启监听手机锁屏的服务（实际监听锁屏是通过广播来完成的,而开启服务是为了让该广播长期在后台监听）
                    //因为开启或者关闭锁屏清理的功能需要在任意场合下使用，比如退出当前的手机卫士任然可以使用该功能，使用其它应用时也可以使用该功能，那么就需要通过服务来实现在后台监听手机锁屏的状态，然后通过此状态清理所有进程
                    startService(new Intent(ProcessSetActivity.this,LockScreenSevice.class));

                }
            }
        });
    }

    /**
     * 是否开启锁屏清理
     * @param b
     */
    private void isOpenLockScreenCleaning(boolean b) {
       //实现选中或未选中图片的切换
        mProcessSetActivity_lockScreenCleaning_iv_switch.setEnabled(b);
        if (b){
            mProcessSetActivity_lockScreenCleaning_tv_switch.setText("锁屏清理以开启");
        }else {
            mProcessSetActivity_lockScreenCleaning_tv_switch.setText("锁屏清理以关闭");
        }

    }

    /**
     * 是否显示系统进程
     * @param isShow
     */
    private void isShowSystemProcess(boolean isShow) {
        //实现选中或未选中图片的切换
        mProcessSetActivity_systemProcess_iv_switch.setEnabled(isShow);
        if (isShow){
            mProcessSetActivity_systemProcess_tv_switch.setText("系统进程已显示");
        }else {
            mProcessSetActivity_systemProcess_tv_switch.setText("系统进程已隐藏");
        }
    }


}
