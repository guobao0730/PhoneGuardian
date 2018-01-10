package com.xgj.phoneguardian.activity;

import com.xgj.phoneguardian.base.BasePnoneBakActivity;
import com.xgj.phoneguardian.R;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/8 0008 21:51
 * @des ${手机防盗引导页面1}
 */
public class PhoneBakSetingOneActivity extends BasePnoneBakActivity {


    @Override
    public void initView() {
        setContentView(R.layout.activity_phone_bak_seting_one);
    }


    @Override
    protected void nextActivity() {

        startActivity(PhonrBakSetingTwoActivity.class);
    }



    @Override
    protected void backActivity() {

    }
}
