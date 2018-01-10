package com.xgj.phoneguardian.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.base.BasePnoneBakActivity;
import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/9 9:26
 * @brief: 手机防盗设置向导页面2
 */
public class PhonrBakSetingTwoActivity extends BasePnoneBakActivity {


    private LinearLayout mLl_phoneBak_setingTwo_bound;
    private ImageView mIv_phoneBak_setingTwo_bound_img;
    private TextView mTv_phoneBak_setingTwo_bound_text;

    //是否绑定的第三方标识变量
    private boolean isBound = true;

    @Override
    public void initView() {

        setContentView(R.layout.activity_phone_bak_seting_two);
        //绑定SIM卡/解绑SIM卡
        mLl_phoneBak_setingTwo_bound = (LinearLayout) this.findViewById(R.id.ll_phoneBak_setingTwo_bound);
        //绑定SIM卡/解绑SIM卡所显示的图片组件
        mIv_phoneBak_setingTwo_bound_img = (ImageView) this.findViewById(R.id.iv_phoneBak_setingTwo_bound_img);
        //绑定SIM卡/解绑SIM卡所显示的文字组件
        mTv_phoneBak_setingTwo_bound_text = (TextView) this.findViewById(R.id.tv_phoneBak_setingTwo_bound_text);
    }




    @Override
    protected void nextActivity() {

        startActivity(PnoneBakSetingThreeActivity.class);
    }


    @Override
    protected void backActivity() {
        startActivity(PhoneBakSetingOneActivity.class);

    }


    @Override
    protected void initData() {
        super.initData();


        //首先初始化数据，SIM卡默认是绑定的状态，那么就先存SIM卡数据
        //注意需要此权限 <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
        //实例化电话管理器
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //获取SIM卡的序列号
        String simSerialNumber = telephonyManager.getSimSerialNumber();

        //将SIM卡的序列号信息存储到SP中
        SpUtils.putString(Constant.SIM,simSerialNumber);

        UiUtils.showToast("SIM卡数据默认以存储");


    }

    @Override
    protected void initEvent() {
        super.initEvent();

        //绑定SIM卡/解绑SIM卡监听器
        mLl_phoneBak_setingTwo_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果开sim键中没有数据那么就表示SIM卡未绑定，（程序默认走这里）
                if (isBound){

                    SpUtils.putString(Constant.SIM,"");

                    //改变为解绑所对应的图片
                    mIv_phoneBak_setingTwo_bound_img.setImageResource(R.mipmap.checkbox_off);
                    //改变为解绑所对应的文字
                    mTv_phoneBak_setingTwo_bound_text.setText("SIM卡已解绑");

                    isBound = false;

                    UiUtils.showToast("SIM卡数据以销毁");

                }else{

                    //注意需要此权限 <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
                    //实例化电话管理器
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                    //获取SIM卡的序列号
                    String simSerialNumber = telephonyManager.getSimSerialNumber();

                    //将SIM卡的序列号信息存储到SP中
                    SpUtils.putString(Constant.SIM,simSerialNumber);

                    //改变为绑定所对应的图片
                    mIv_phoneBak_setingTwo_bound_img.setImageResource(R.mipmap.checkbox_on);
                    //改变为绑定所对应的文字
                    mTv_phoneBak_setingTwo_bound_text.setText("SIM卡已绑定");


                    isBound = true;

                    UiUtils.showToast("SIM卡数据再次存储");


                }

            }
        });


    }


    /**
     * 为了实现当引导页面2中的SIM卡未绑定时就无法跳转到引导页面3，就必须重写父类的next()
     * @param v
     */
    @Override
    public void next(View v) {

        //如果当前的第三方表示变量为false, 那么就控制 下一步 按钮点击失效并给出提示
        if (isBound==false){
            UiUtils.showToast("请绑定SIM卡");
            //返回出去就不执行父类的 下一步（下一个页面和动画的方法） 方法
            return;
        }
        super.next(v);
    }
}
