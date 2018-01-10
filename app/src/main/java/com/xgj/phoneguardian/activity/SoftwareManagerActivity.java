package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.AppInfoAdapter;
import com.xgj.phoneguardian.bean.AppInfoBean;
import com.xgj.phoneguardian.engine.ApplicationMessage;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/8/21 15:39
 * @brief: 软件管家页面
 */
public class SoftwareManagerActivity extends Activity {

    private static final String TAG = "SoftwareManagerActivity";
    private static final int DATA = 0X001;
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;
    private TextView mSoftwareManagerActivity_tv_memory;
    private TextView mSoftwareManagerActivity_tv_sdcard;
    private ListView mSoftwareManagerActivity_lv;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case DATA:
                    List<AppInfoBean> applicationMessageList = (List<AppInfoBean>) msg.obj;
                    mSystemApp = new ArrayList<>();
                    mUserApp = new ArrayList<>();
                    for (int i = 0; i < applicationMessageList.size(); i++) {
                        AppInfoBean appInfoBean = applicationMessageList.get(i);
                        LogUtils.i(TAG,"appInfoBean.getAppName():"+appInfoBean.getAppName());
                        LogUtils.i(TAG,"appInfoBean.getPackageName():"+appInfoBean.getPackageName());
                        LogUtils.i(TAG,"appInfoBean.isSdcard():"+appInfoBean.isSdcard());
                        LogUtils.i(TAG,"appInfoBean.isSystemAPP():"+appInfoBean.isSystemAPP());

                        //遍历所有集合数据，将系统应用和非系统应用分别存储
                        if (appInfoBean.isSystemAPP()){
                            mSystemApp.add(appInfoBean);
                        }else {
                            mUserApp.add(appInfoBean);
                        }
                    }


                    AppInfoAdapter appInfoAdapter = new AppInfoAdapter(UiUtils.getContext(), mSystemApp, mUserApp);
                    mSoftwareManagerActivity_lv.setAdapter(appInfoAdapter);

                    break;
            }
        }
    };
    private TextView mSoftwareManagerActivity_tv_title;
    private ArrayList<AppInfoBean> mSystemApp;
    private ArrayList<AppInfoBean> mUserApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_softwaremanager);
        initView();
        initData();
        initEvent();
    }



    private void initView() {
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mSoftwareManagerActivity_tv_memory = (TextView) findViewById(R.id.softwareManagerActivity_tv_memory);
        mSoftwareManagerActivity_tv_sdcard = (TextView) findViewById(R.id.softwareManagerActivity_tv_sdcard);
        mSoftwareManagerActivity_lv = (ListView) findViewById(R.id.softwareManagerActivity_lv);
        mSoftwareManagerActivity_tv_title = (TextView) findViewById(R.id.softwareManagerActivity_tv_title);

    }
    private void initData() {
        mTitleLayout_tv_title.setText("软件管家");

        //获取手机可用内存
        final String memory = PhoneSystemUtils.getSpaceAvailable(this, PhoneSystemUtils.getMemoryPath());
        //获取SD卡可用内存
        String sdcard = PhoneSystemUtils.getSpaceAvailable(this, PhoneSystemUtils.getSDPath());
        mSoftwareManagerActivity_tv_memory.setText("手机内存可用："+memory);
        mSoftwareManagerActivity_tv_sdcard.setText("SD卡可用："+sdcard);




    }

    /**
     * 初始化在当前手机中安装的所有应用数据
     */
    private void initApkData() {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<AppInfoBean> applicationMessageList = ApplicationMessage.getApplicationMessageList(UiUtils.getContext());
                Message message = mHandler.obtainMessage();
                message.obj = applicationMessageList;
                message.what = DATA;
                mHandler.sendMessage(message);
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
        //给当前的ListView设置一个滚动监听器
        mSoftwareManagerActivity_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 当正在滚动的时候调用该函数
             * @param view
             * @param firstVisibleItem 第一个可见条目的下标
             * @param visibleItemCount 当前屏幕的可见条目个数
             * @param totalItemCount 总条目个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mUserApp!=null&&mSystemApp!=null){
                    //如果第一个可见的视图条目下标>=系统应用常驻悬浮框
                    //也就是当滚顶到 系统应用常驻悬浮框 时
                    if (firstVisibleItem>=mUserApp.size()+1){
                        mSoftwareManagerActivity_tv_title.setText("系统应用("+mSystemApp.size()+")");
                    }else {
                        mSoftwareManagerActivity_tv_title.setText("用户应用("+mUserApp.size()+")");
                    }

                }
            }
        });

        //条目点击事件
        mSoftwareManagerActivity_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前点击的条目不是 用户应用常驻悬浮框或者 系统应用常驻悬浮框 ,那么就显示PopupWindow
                if (position==0||position==mUserApp.size()+1){
                    return;
                }else {
                    AppInfoBean appInfoBean = null;
                    //如果当前的位置在 系统应用常驻悬浮框条目 的范围内
                    if (position<mUserApp.size()+1){
                        //那么获取当前位置的前一个用户应用对象,以便后期卸载，打开，分享该应用时使用
                         appInfoBean = mUserApp.get(position - 1);
                    }else {
                        //那么获取系统应用对象
                        // position-userApp.size()-2 (表示当前的条目-用户的总个数-2个常驻悬浮框)
                        appInfoBean = mSystemApp.get(position-mUserApp.size()-2);
                    }

                    showPopupWindow(appInfoBean,view);

                }
            }
        });
    }

    /**
     * 显示 卸载，打开，分享的Popupwindow
     * @param appInfoBean
     */
    private void showPopupWindow(final AppInfoBean appInfoBean, View itemView) {

        View view = View.inflate(UiUtils.getContext(), R.layout.popupwindow_application_details, null);
        //true 表示点击窗体以外的区域销毁当前的PopupWindow，或者说当失去焦点的时候销毁PopupWindow
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //注意：给PopupWindow设置动画比较特殊，必须先给PopupWindow设置背景资源才会显示动画效果（可选）
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        /*//设置相对于按钮来显示,数组的长度为2表X，Y轴
        int[] ints = new int[2];
        itemView.getLocationInWindow(ints);
        Log.i("MainActivity","按钮在屏幕中的X轴"+String.valueOf(ints[0])+"\t"+"Y轴"+String.valueOf(ints[1]));

        //showAtLocation()显示弹出窗体并设置其显示的位置
        //mBt_show 就是该弹出窗体的父组件
        //ints[0] 表示mBt_show的X轴   ，ints[1] 表示mBt_show的y轴
        popupWindow.showAtLocation(itemView, Gravity.TOP,ints[0],ints[1]+itemView.getHeight());*/

        //设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量
        // 0（因为之前的PopupWindow宽度设置的是ViewGroup.LayoutParams.MATCH_PARENT填充整个窗体，所以无需加偏移量）
        //-itemView.getHeight()*2  表示自定义的经过反复测试的Y轴偏移量
        popupWindow.showAsDropDown(itemView,0,-itemView.getHeight()*2);

        //卸载
        view.findViewById(R.id.applicationDetailsPopupwindow_tv_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInfoBean.isSystemAPP()){
                    UiUtils.showToast("系统应用无法卸载");
                }else {
                    //开始根据当前指定的包名删除当前的APK
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:"+appInfoBean.getPackageName()));
                    startActivity(intent);

                    //当卸载后通过onResume()重新获取当前手机中安装的所有APK，一避免出现删除该应用后任然在列表中显示的BUG
                }
                //销毁popupWindow
                popupWindow.dismiss();
            }
        });

        //启动
        view.findViewById(R.id.applicationDetailsPopupwindow_tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PackageManager packageManager = getPackageManager();
                //根据指定的包名去开启一应用程序
                Intent intent = packageManager.getLaunchIntentForPackage(appInfoBean.getPackageName());
                if (intent!=null){
                    startActivity(intent);
                }else {
                    UiUtils.showToast("此应用无法启动");
                }

                //销毁popupWindow
                popupWindow.dismiss();
            }
        });

        //分享
        view.findViewById(R.id.applicationDetailsPopupwindow_tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //分享可以通过 微信，QQ，新浪等第三方的媒介去分享
                //目前只用通过发送短信的方式将当前的APK分享出去
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个好玩又有趣的APK给你："+appInfoBean.getAppName()+"");
                intent.setType("text/plain");
                startActivity(intent);

                //销毁popupWindow
                popupWindow.dismiss();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        //为了实现当删除应用后该应用的数据依然展示在ListView上面，那么就需要实现当Activity获取焦点的时候重新初始化应用集合数据
        initApkData();
    }
}
