package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.service.BlackListService;
import com.xgj.phoneguardian.service.HomeLocationService;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.ServiceUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/8 17:16
 * @brief: 设置中心的页面
 */
public class SetingActivity extends Activity {

    private static final String TAG ="SetingActivity" ;
    private RelativeLayout mRl_updateSeting;
    private TextView mTv_seting_update_desc;
    private ImageView mIv_seting_updata_img;

    private boolean updateSwitch = true;
    private ImageView mIv_seting_back;
    private RelativeLayout mSettingActicity_homeLocation_rl;
    private TextView mSettingActicity_homeLocation_tv_desc;
    private ImageView mSettingActicity_homeLocation_iv_img;
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;
    private RelativeLayout mSettingActicity_attributionStyle_rl;
    private TextView mSettingActicity_attributionStyle_tv_desc;
    private RelativeLayout mSettingActicity_attributionLocation_rl;
    private TextView mSettingActicity_attributionLocation_tv_desc;
    private String mBackgroundColor;
    private RelativeLayout mSettingActicity_blackList_rl;
    private TextView mSettingActicity_blackList_tv_desc;
    private ImageView mSettingActicity_blackList_iv_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.avtivity_seting);

        initView();
        initData();
        initEvent();

    }




    private void initView() {

        //返回
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);


        mRl_updateSeting = (RelativeLayout) this.findViewById(R.id.rl_updateSeting);
        mTv_seting_update_desc = (TextView) this.findViewById(R.id.tv_seting_update_desc);
        mIv_seting_updata_img = (ImageView) this.findViewById(R.id.iv_seting_updata_img);

        mSettingActicity_homeLocation_rl = (RelativeLayout) findViewById(R.id.settingActicity_homeLocation_rl);
        mSettingActicity_homeLocation_tv_desc = (TextView) findViewById(R.id.settingActicity_homeLocation_tv_desc);
        mSettingActicity_homeLocation_iv_img = (ImageView) findViewById(R.id.settingActicity_homeLocation_iv_img);

        mSettingActicity_attributionStyle_rl = (RelativeLayout) findViewById(R.id.settingActicity_attributionStyle_rl);
        mSettingActicity_attributionStyle_tv_desc = (TextView) findViewById(R.id.settingActicity_attributionStyle_tv_desc);

        mSettingActicity_attributionLocation_rl = (RelativeLayout) findViewById(R.id.settingActicity_attributionLocation_rl);
        mSettingActicity_attributionLocation_tv_desc = (TextView) findViewById(R.id.settingActicity_attributionLocation_tv_desc);

        //黑名单拦截设置
        mSettingActicity_blackList_rl = (RelativeLayout) findViewById(R.id.settingActicity_blackList_rl);
        mSettingActicity_blackList_tv_desc = (TextView) findViewById(R.id.settingActicity_blackList_tv_desc);
        mSettingActicity_blackList_iv_img = (ImageView) findViewById(R.id.settingActicity_blackList_iv_img);


    }

    private void initData() {

        mTitleLayout_tv_title.setText("设置中心");

        if (SpUtils.getBoolean(Constant.AUTO_UPDATE,false)){
            automaticUpdate();
        }else {
            turnOffAutomaticUpdates();
        }

        //判断当前是显示归属地还是隐藏，需要根据归属地的服务是否在运行来判断，而不是通过SP来记录状态，
        // 因为如果用SP保存状态可能会出现SP状态为true,而对应的服务因为手机内存不足而被系统销毁的BUG
        if (ServiceUtils.isStartService(HomeLocationService.class.getName())){
            attributively();
        }else {
            concealment();
        }


        String name = BlackListService.class.getName();
        LogUtils.i(TAG,"name11:"+name);
        //根据服务是否正在后台运行来判断当前的黑名单拦截是否开启
        if (ServiceUtils.isStartService(BlackListService.class.getName())){
            LogUtils.i(TAG,"拦截服务正在运行");
            //开启黑名单拦截
            openBlacklist();
        }else {
            LogUtils.i(TAG,"拦截服务销毁了");
            //关闭黑名单拦截
            closeBlacklist();
        }


    }

    /**
     * 开启黑名单
     */
    private void openBlacklist() {
        mSettingActicity_blackList_tv_desc.setText("黑名单拦截已开启");
        mSettingActicity_blackList_iv_img.setEnabled(true);
    }

    /**
     * 关闭黑名单
     */
    private void closeBlacklist() {
        mSettingActicity_blackList_tv_desc.setText("黑名单拦截已关闭");
        mSettingActicity_blackList_iv_img.setEnabled(false);
    }
    /**
     * 隐藏归属地
     */
    private void concealment() {
        mSettingActicity_homeLocation_tv_desc.setText("归属地显示以关闭");
        mSettingActicity_homeLocation_iv_img.setEnabled(false);

        //隐藏显示设置归属地的视图
        mSettingActicity_attributionStyle_rl.setVisibility(View.GONE);
        mSettingActicity_attributionLocation_rl.setVisibility(View.GONE);
    }

    /**
     * 显示归属地
     */
    private void attributively() {
        mSettingActicity_homeLocation_tv_desc.setText("归属地显示以开启");
        mSettingActicity_homeLocation_iv_img.setEnabled(true);

        //显示设置归属地的视图
        mSettingActicity_attributionStyle_rl.setVisibility(View.VISIBLE);
        mSettingActicity_attributionLocation_rl.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭自定更新
     */
    private void turnOffAutomaticUpdates() {
        mTv_seting_update_desc.setText("自动更新已关闭");
        mIv_seting_updata_img.setEnabled(false);
    }

    /**
     * 开启自动更新
     */
    private void automaticUpdate() {
        mTv_seting_update_desc.setText("自动更新已开启");
        mIv_seting_updata_img.setEnabled(true);
    }


    private void initEvent() {

        //自动更新设置
        mRl_updateSeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SpUtils.getBoolean(Constant.AUTO_UPDATE,false)){
                    turnOffAutomaticUpdates();
                    SpUtils.putBoolean(Constant.AUTO_UPDATE,false);
                }else{
                    automaticUpdate();
                    SpUtils.putBoolean(Constant.AUTO_UPDATE,true);
                }

            }
        });



        //返回
        mTitleLayout_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //归属地
        mSettingActicity_homeLocation_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前是显示归属地还是隐藏，需要根据归属地的服务是否在运行来判断，而不是通过SP来记录状态，
                // 因为如果用SP保存状态可能会出现SP状态为true,而对应的服务因为手机内存不足而被系统销毁的BUG
                if (ServiceUtils.isStartService(HomeLocationService.class.getName())){
                    concealment();
                    SpUtils.putBoolean(Constant.HOME_LOCATION,false);

                    //关闭归属地的服务
                    Intent intent = new Intent(UiUtils.getContext(), HomeLocationService.class);
                    stopService(intent);

                }else {
                    attributively();
                    SpUtils.putBoolean(Constant.HOME_LOCATION,true);

                    //开启归属地的服务，在服务中监听手机来电的状态和显示吐司
                    Intent intent = new Intent(UiUtils.getContext(), HomeLocationService.class);
                    startService(intent);
                }

            }
        });

        //设置归属地的风格
        mSettingActicity_attributionStyle_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SetingActivity.this);
                //设置对话框的图标
                //builder.setIcon(R.drawable.back_selector);
                //设置对话框的标题
                builder.setTitle("归属地样式");
                //0: 默认第一个单选按钮被选中
                builder.setSingleChoiceItems(R.array.attribution_style, 0, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        mBackgroundColor = getResources().getStringArray(R.array.attribution_style)[which];
                        UiUtils.showToast("您选择了： "+which+"位置的"+mBackgroundColor);
                    }
                });
                //添加一个确定按钮
                builder.setPositiveButton(" 确 定 ", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.i(TAG,"setPositiveButton--which:"+which);
                        //如果当前选择的颜色为null，那么就设置为默认的透明色
                        if (TextUtils.isEmpty(mBackgroundColor)){
                            mSettingActicity_attributionStyle_tv_desc.setText(getResources().getStringArray(R.array.attribution_style)[0]);
                        }else {
                            //设置颜色
                            mSettingActicity_attributionStyle_tv_desc.setText(mBackgroundColor);
                        }
                        //将设置的颜色存储到SP中
                        SpUtils.putString(Constant.ATTRIBUTION_STYLE,mBackgroundColor);
                    }
                });
                //创建一个单选按钮对话框
               builder.create();
                builder.show();

            }
        });

        mSettingActicity_attributionLocation_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetingActivity.this, SetLocationAttributionActivity.class);
                startActivity(intent);
            }
        });

        //黑名单拦截
        mSettingActicity_blackList_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = BlackListService.class.getName();
                LogUtils.i(TAG,"name222:"+name);
                if (ServiceUtils.isStartService(BlackListService.class.getName())){
                    //关闭
                    closeBlacklist();
                    Intent intent = new Intent(UiUtils.getContext(), BlackListService.class);
                    stopService(intent);
                }else {
                    //打开黑名单拦截服务
                    openBlacklist();
                    Intent intent = new Intent(UiUtils.getContext(), BlackListService.class);
                    startService(intent);

                }


            }
        });

    }


}
