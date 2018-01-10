package com.xgj.phoneguardian.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.base.BasePnoneBakActivity;
import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.service.AntitheftProtectionService;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.ServiceUtils;
import com.xgj.phoneguardian.utils.SpUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/9 10:21
 * @brief: 手机防盗引导页面4
 */
public class PhoneBakSetingFourActivity extends BasePnoneBakActivity {


    private LinearLayout mLl_phoneBak_setingFour_bound;
    private ImageView mIv_phoneBak_setingFour_img;
    private TextView mTv_phoneBak_setingFour_text;

    @Override
    public void initView() {

        setContentView(R.layout.activity_phone_bak_seting_four);
        //防盗保护总组件
        mLl_phoneBak_setingFour_bound = (LinearLayout) this.findViewById(R.id.ll_phoneBak_setingFour_bound);
        //防盗保护对应的图片组件
        mIv_phoneBak_setingFour_img = (ImageView) this.findViewById(R.id.iv_phoneBak_setingFour_img);
        //防盗保护所对应的文字
        mTv_phoneBak_setingFour_text = (TextView) this.findViewById(R.id.tv_phoneBak_setingFour_text);
    }



    @Override
    protected void nextActivity() {

        //当点击引导页面4中的 完成 的时候存储旗标以便第二次直接进入手机防盗页面
        SpUtils.putBoolean(Constant.IS_FIRST,true);

        //然后跳转到手机防盗主页面
        startActivity(PhoneBakActivity.class);

    }


    @Override
    protected void backActivity() {
        startActivity(PnoneBakSetingThreeActivity.class);
    }

    /**
     * 因防盗保护是默认开启的状态，那么就重写此方法默认通过服务开启防盗保护
     */
    @Override
    protected void initData() {
        super.initData();


        //为了防止在引导页面4中回到页面3中时在返回到页面4中时会重复的开启防盗保护（也就是重复开启服务）就先加判断（如果服务没有开启那么就开启服务，否则不执行）
        if (!ServiceUtils.isStartService("com.xgj.phoneguardian.service.AntitheftProtectionService")){

            //默认开启防盗保护
            Intent intent = new Intent(PhoneBakSetingFourActivity.this, AntitheftProtectionService.class);
            startService(intent);

            Log.i("防盗保护引导页面4","防盗保护服务默认以开启");

        }

    }


    @Override
    protected void initEvent() {
        super.initEvent();

        //防盗保护总组件
        mLl_phoneBak_setingFour_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //如果已经开启服务
                if (ServiceUtils.isStartService("com.xgj.phoneguardian.service.AntitheftProtectionService")){

                    //那么就停止服务
                    Intent intent = new Intent(PhoneBakSetingFourActivity.this, AntitheftProtectionService.class);

                    stopService(intent);

                    mIv_phoneBak_setingFour_img.setImageResource(R.mipmap.checkbox_off);
                    mTv_phoneBak_setingFour_text.setText("防盗保护已关闭");

                    Log.i("防盗保护引导页面4","防盗保护服务以关闭");

                }else {

                    //那么就开启服务
                    Intent intent = new Intent(PhoneBakSetingFourActivity.this, AntitheftProtectionService.class);

                    startService(intent);

                    mIv_phoneBak_setingFour_img.setImageResource(R.mipmap.checkbox_on);
                    mTv_phoneBak_setingFour_text.setText("防盗保护已开启");

                    Log.i("防盗保护引导页面4","防盗保护服务以开启");


                }



            }
        });

    }
}
