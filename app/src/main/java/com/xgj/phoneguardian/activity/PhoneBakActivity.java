package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.ServiceUtils;
import com.xgj.phoneguardian.utils.SpUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/8 15:16
 * @brief: 手机防盗页面
 */
public class PhoneBakActivity extends Activity {

    private TextView mTv_phonebak_show_security_number;
    private TextView mTv_phoneBak_isOpen;
    private Button mBt_phoneBak_reset;
    private ImageView mIv_phoneBak_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak);
        initView();
        initData();
        initEvent();
    }



    private void initView() {

        //显示安全号码
        mTv_phonebak_show_security_number = (TextView) this.findViewById(R.id.tv_phonebak_show_security_number);
       //是否开启防盗保护
        mTv_phoneBak_isOpen = (TextView) this.findViewById(R.id.tv_phoneBak_isOpen);
        //重设
        mBt_phoneBak_reset = (Button) this.findViewById(R.id.bt_phoneBak_reset);

        //返回
        mIv_phoneBak_back = (ImageView) this.findViewById(R.id.iv_phoneBak_back);

    }

    private void initData() {

        mTv_phonebak_show_security_number.setText("安全号码："+SpUtils.getString(Constant.SECURITY_NUMBER,""));

        if (ServiceUtils.isStartService("com.xgj.phoneguardian.service.AntitheftProtectionService")){

            mTv_phoneBak_isOpen.setText("防盗保护已开启");

        }else {
            mTv_phoneBak_isOpen.setText("防盗保护未开启");
        }




    }


    private void initEvent() {

        //重设
        mBt_phoneBak_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhoneBakActivity.this, PhoneBakSetingOneActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //返回
        mIv_phoneBak_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }





}
