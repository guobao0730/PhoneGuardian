package com.xgj.phoneguardian.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.xgj.phoneguardian.base.BasePnoneBakActivity;
import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/9 9:57
 * @brief: 手机防盗引导页面3
 */
public class PnoneBakSetingThreeActivity extends BasePnoneBakActivity {


    private EditText mEt_phoneBak_setingThree_input;
    private ImageView mIv_phoneBak_setingThree_add;

    @Override
    public void initView() {

        setContentView(R.layout.activity_phone_bak_seting_three);
        //输入安全号码
        mEt_phoneBak_setingThree_input = (EditText) this.findViewById(R.id.et_phoneBak_setingThree_input);
        //添加 联系人
        mIv_phoneBak_setingThree_add = (ImageView) this.findViewById(R.id.iv_phoneBak_setingThree_add);
    }




    @Override
    protected void nextActivity() {
        startActivity(PhoneBakSetingFourActivity.class);

    }



    @Override
    protected void backActivity() {
        startActivity(PhonrBakSetingTwoActivity.class);
    }


    @Override
    public void next(View v) {

        //再点击下一步之前判断 安全号码
        String securityNumber = mEt_phoneBak_setingThree_input.getText().toString().trim();
        if (TextUtils.isEmpty(securityNumber)){
            UiUtils.showToast("安全号码不能为空");
            //如果安全号码为空那么就返回不调用super.next(v);方法，就相当于让下一步的点击事件失效而无法正常跳转页面
            return;


            //表示如果不是以13或15或18开头，后面9位数字结尾
        }else if (!securityNumber.matches("^(13|15|18)\\d{9}$")){

            UiUtils.showToast("您输入的安全号码不合法");
            //如果安全号码号码不合法那么就返回不调用super.next(v);方法，就相当于让下一步的点击事件失效而无法正常跳转页面
            return;

        }else{

            //那么安全号码输入正确
            Log.i("手机防盗引导页面3","安全号码输入正确");


            //讲安全号码存到SP中
            SpUtils.putString(Constant.SECURITY_NUMBER,securityNumber);
            //那么正常调用super.next(v);方法实现跳转页面

        }


        //此方法相当于调用父类的next(),(实现跳转页面和跳转时的动画操作)
        super.next(v);
    }


    /**
     * 为了解决当页面从引导4返回到引导页面3时出现刚刚输入的手机号码消失
     * 那么需重写此方法 并获取SP中的数据显示在输入文本框中
     */
    @Override
    protected void initData() {
        super.initData();

        //获取SP中的数据显示在输入文本框中
         mEt_phoneBak_setingThree_input.setText(SpUtils.getString(Constant.SECURITY_NUMBER,""));

    }


    @Override
    protected void initEvent() {
        super.initEvent();

        //添加联系人
        mIv_phoneBak_setingThree_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i("手机防盗引导页面3","您点击了添加联系人按钮");

                //跳转到显示联系人的页面并获取返回的结果数据（手机号），
                Intent intent = new Intent(UiUtils.getContext(),ContactsActivity.class);
                //意图对象，请求码
                startActivityForResult(intent,100);



            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //为了防止从显示联系人页面点击返回键时数据为空，那么就提前做出判断
        //如果返回的意图对象不为空
        if (data!=null){

            //获取返回的结果数据（当前选中的联系人的手机号码）
            String securityNumber = data.getStringExtra(Constant.SECURITY_NUMBER);

            //为了避免手机号码之间有空格而导致号码显示不全就按空格拆分该字符串
            String[] split = securityNumber.split("\\s+");
            StringBuffer stringBuffer = new StringBuffer();

            for (int i  =0;i<split.length;i++){
                 stringBuffer.append(split[i]);
            }


            //设置进组件中
            mEt_phoneBak_setingThree_input.setText(stringBuffer.toString());

        }





    }
}
