package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.AppLockAdapter;
import com.xgj.phoneguardian.bean.AppInfoBean;
import com.xgj.phoneguardian.db.AppLockDao;
import com.xgj.phoneguardian.engine.ApplicationMessage;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/11/5 18:00
 * @brief: 程序锁页面
 * 以加锁+未加锁=手机上的所有应用
 * 而AppLockDao表中存储就是以加锁的数据，只需要用全部的应用-已加锁的应用=未加锁的应用
 */

public class AppLockActivity extends Activity {

    private static final int LOCK_APP = 0x001;
    private static final int UNLOCK_APP = 0X002;
    private static final String TAG = "AppLockActivity";
    private TextView mTitleLayout_tv_title;
    private LinearLayout mTitleLayout_ll_back;
    private Button mAppLockActivity_bt_unlocked;
    private Button mAppLockActivity_bt_lock;
    private TextView mAppLockActivity_tv_appNumber;
    private ListView mAppLockActivity_lv;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case LOCK_APP:
                    ArrayList<AppInfoBean> lockApp = (ArrayList<AppInfoBean>) msg.obj;

                    break;
                case UNLOCK_APP :
                    List<AppInfoBean> unlockApp = (ArrayList<AppInfoBean>) msg.obj;

                    AppLockAdapter appLockAdapter = new AppLockAdapter(getApplicationContext(), unlockApp,false);
                    mAppLockActivity_lv.setAdapter(appLockAdapter);
                    mAppLockActivity_tv_appNumber.setText("未加锁应用："+unlockApp.size());

                    break;
            }
        }
    };
    private RadioGroup mAppLockActivity_rg;
    private RadioButton mAppLockActivity_rb_lock;
    private RadioButton mAppLockActivity_rb_unlocked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_lock);
        initView();
        initData();
        initEvent();
    }




    private void initView() {
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mAppLockActivity_bt_unlocked = (Button) findViewById(R.id.appLockActivity_bt_unlocked);
        mAppLockActivity_bt_lock = (Button) findViewById(R.id.appLockActivity_bt_lock);
        mAppLockActivity_tv_appNumber = (TextView) findViewById(R.id.appLockActivity_tv_appNumber);
        mAppLockActivity_lv = (ListView) findViewById(R.id.appLockActivity_lv);

        mAppLockActivity_rg = (RadioGroup) findViewById(R.id.appLockActivity_rg);
        mAppLockActivity_rb_lock = (RadioButton) findViewById(R.id.appLockActivity_rb_lock);
        mAppLockActivity_rb_unlocked = (RadioButton) findViewById(R.id.appLockActivity_rb_unlocked);


    }

    private void initData() {
        mTitleLayout_tv_title.setText("程序锁");
        mAppLockActivity_bt_unlocked.setBackgroundResource(R.drawable.tab_left_pressed);
        mAppLockActivity_bt_lock.setBackgroundResource(R.drawable.tab_right_default);

        mAppLockActivity_rb_unlocked.setChecked(true);


        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //获取手机中所有的应用
                List<AppInfoBean> applicationMessageList = ApplicationMessage.getApplicationMessageList(getApplicationContext());

                ArrayList<AppInfoBean> lockApp = new ArrayList<>();
                ArrayList<AppInfoBean> unlockApp = new ArrayList<>();

                AppLockDao appLockDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = appLockDao.queryAll();

                for (int i = 0; i <applicationMessageList.size() ; i++) {
                    AppInfoBean appInfoBean = applicationMessageList.get(i);
                    //如果加锁数据表中包含当前的应用，那么就将当前已加锁的对象添加到对应集合中
                    if (lockPackageList.contains(appInfoBean)){
                        lockApp.add(appInfoBean);
                    }else {
                        unlockApp.add(appInfoBean);
                    }
                }

                Message message = mHandler.obtainMessage();
                message.obj = lockApp;
                message.what = LOCK_APP;
                mHandler.sendMessage(message);


                Message message1 = mHandler.obtainMessage();
                message.obj = unlockApp;
                message.what = UNLOCK_APP;
                mHandler.sendMessage(message1);

            }
        });


    }

    private void initEvent() {
        mTitleLayout_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAppLockActivity_bt_unlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG,"未加锁被点击了");
                mAppLockActivity_bt_unlocked.setBackgroundResource(R.drawable.tab_left_default);
                mAppLockActivity_bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);

                LogUtils.i(TAG,"unlocked--mAppLockActivity_bt_lock.isEnabled():"+mAppLockActivity_bt_lock.isEnabled());
                LogUtils.i(TAG,"unlocked--mAppLockActivity_bt_unlocked.isEnabled():"+mAppLockActivity_bt_unlocked.isEnabled());
            }
        });
        mAppLockActivity_bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG,"已加锁被点击了");
                mAppLockActivity_bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                mAppLockActivity_bt_unlocked.setBackgroundResource(R.drawable.tab_left_default);

                LogUtils.i(TAG,"lock--mAppLockActivity_bt_lock.isEnabled():"+mAppLockActivity_bt_lock.isEnabled());
                LogUtils.i(TAG, "lock--mAppLockActivity_bt_unlocked.isEnabled():"+mAppLockActivity_bt_unlocked.isEnabled());
            }
        });


        mAppLockActivity_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.appLockActivity_rb_lock:
                        mAppLockActivity_rb_lock.setChecked(!mAppLockActivity_rb_lock.isChecked());
                        break;
                    case R.id.appLockActivity_rb_unlocked:
                        mAppLockActivity_rb_unlocked.setChecked(!mAppLockActivity_rb_unlocked.isChecked());
                        break;
                }

            }
        });
    }

}
