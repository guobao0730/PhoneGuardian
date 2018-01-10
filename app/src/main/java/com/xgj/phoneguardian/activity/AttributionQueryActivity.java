package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.engine.HomeLocationDao;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/7/29 16:23
 * @brief: 归属地查询
 */
public class AttributionQueryActivity extends Activity {


    private static final int LOCATION = 0x001;
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;
    private EditText mAttributionQueryActivity_et;
    private ImageView mAttributionQueryActivity_iv_query;
    private TextView mAttributionQueryActivity_tv_show;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case LOCATION:
                   String location = (String) msg.obj;
                    mAttributionQueryActivity_tv_show.setText(location);

                    break;
            }

        }
    };
    private RelativeLayout mAttributionQueryActivity_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributionauery);
        initView();
        initData();
        initEvent();
    }



    private void initView() {
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mAttributionQueryActivity_et = (EditText) findViewById(R.id.attributionQueryActivity_et);
        mAttributionQueryActivity_iv_query = (ImageView) findViewById(R.id.attributionQueryActivity_iv_query);
        mAttributionQueryActivity_tv_show = (TextView) findViewById(R.id.attributionQueryActivity_tv_show);
        mAttributionQueryActivity_rl = (RelativeLayout) findViewById(R.id.attributionQueryActivity_rl);


    }


    private void initData() {
        mTitleLayout_tv_title.setText("归属地查询");
    }
    private void initEvent() {
        mTitleLayout_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //在查询之前首先将小米数据库存放在assets目录下，然后再SplashActivity中初始化数据库
        mAttributionQueryActivity_et.addTextChangedListener(new TextWatcher() {
            /**
             * 当文本改变之前调用该函数
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 当文本改变的时候调用该函数
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            /**
             * 当文字改变之后调用该函数
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

                final String phoneNumber = mAttributionQueryActivity_et.getText().toString().trim();

                query(phoneNumber);

            }
        });

        mAttributionQueryActivity_iv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumber = mAttributionQueryActivity_et.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)){
                    UiUtils.showToast("号码不能为空!");
                    //加载抖动的动画
                    Animation animation = AnimationUtils.loadAnimation(UiUtils.getContext(), R.anim.shake);
                    mAttributionQueryActivity_rl.startAnimation(animation);
                    //震动
                    PhoneSystemUtils.vibrate(UiUtils.getContext());
                    return;
                }else {
                    //查询数据库
                    query(phoneNumber);

                }
            }
        });
    }

    /**
     * 查询手机归属地
     * @param phoneNumber
     */
    private void query(final String phoneNumber) {
        //注意：查询数据库是耗时操作，那么就需要开辟一个子线程来完成操作
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                String location = HomeLocationDao.query(phoneNumber);
                Message message = mHandler.obtainMessage();
                message.obj = location;
                message.what = LOCATION;
                mHandler.sendMessage(message);
            }
        });
    }

}
