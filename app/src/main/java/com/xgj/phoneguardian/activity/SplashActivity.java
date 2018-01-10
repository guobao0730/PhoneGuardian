package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.SpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {


    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private int mVersionCode;


    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initAnimation();
        loadMainActivity();
        initDB();
        initShortCut();

    }

    /**
     * 初始化该应用的快捷方式
     *<!--创建快捷方式所需的权限 -->
     <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"></uses-permission>
     */
    private void initShortCut() {

        //如果没有创建过快捷方式，那么创建，否则不予创建
        if (!SpUtils.getBoolean(Constant.SHORT_CUT,false)){
            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            //设置快捷方式的图标
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
            //设置快捷方式的名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");

            //设置点击快捷方式后要跳转的页面,通过隐式意图的方式来开启,实现当点击快捷方式后跳转到该应用的主页面
            //"android.intent.action.Home" 该事件是自定义的
            Intent intent1 = new Intent("android.intent.action.HOME");
            intent1.addCategory("android.intent.category.DEFAULT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent1);

            //最后给系统发送一个注册快捷方式的广播
            sendBroadcast(intent);

            //记录当前已经创建了快捷方式的旗标，以避免出现重复创建快捷方式的BUG
            SpUtils.putBoolean(Constant.SHORT_CUT,true);
        }


    }

    private void initDB() {

        //初始化归属地数据库
        initHomeLocationDB("address.db");
        //初始化常用号码数据库
        initHomeLocationDB("commonnum.db");
    }


    /**
     * 初始化归属地数据库(将assets目录下的address.db数据库拷贝到file文件下)
     */
    private void initHomeLocationDB(String dbName) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, dbName);
        if (!file.exists()){

            //获取assets目录下的address.db
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                 inputStream = getAssets().open(dbName);

                //将读取的文件写入到指定的文件夹中
                 fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int temp = -1;
                while ( (temp = inputStream.read(bytes))!=-1){
                    fileOutputStream.write(bytes,0,temp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //关流
                if (inputStream!=null&&fileOutputStream!=null){
                    try {
                        inputStream.close();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }


    private void initView() {
        mRelativeLayout = (RelativeLayout) this.findViewById(R.id.rl_splash_root);
        mTextView = (TextView) this.findViewById(R.id.tv_splash_version);
    }

    private void initData() {

        //获取当前程序的版本信息
        PackageManager packageManager = getPackageManager();

        try {
            //获取包信息，传入该应用的包名，旗标（PERMISSION_GRANTED/许可授予）
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.PERMISSION_GRANTED);

            //获取该应用的版本码
            mVersionCode = packageInfo.versionCode;

            //获取版本名
            String versionName = packageInfo.versionName;

            mTextView.setText("郭宝专属 "+versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }


    private void initAnimation() {

        AnimationSet animationSet = new AnimationSet(true);

        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画执行的时间（缩放效果持续的时间）
        scaleAnimation.setDuration(1000);
        //设置动画停止在最后一帧的状态
        scaleAnimation.setFillAfter(true);



        //渐隐渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        mRelativeLayout.setAnimation(animationSet);

    }




    /**
     * 加载主页面
     */
    private void loadMainActivity() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


    }



}
