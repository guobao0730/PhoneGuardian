package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.AdvancedToolsAdapter;
import com.xgj.phoneguardian.bean.AdvancedToolsBean;
import com.xgj.phoneguardian.engine.SMSBackup;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.UiUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/7/29 15:02
 * @brief: 高级工具
 */
public class AdvancedToolsActivity extends Activity {

    private static final String TAG ="AdvancedToolsActivity" ;
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;
    private GridView mAdvancedToolsActivity_gv;

    private int[] imgs = new int[]{R.mipmap.home_location, R.mipmap.sms, R.mipmap.phone, R.mipmap.lock};
    private String[] msgs = new String[]{"归属地查询","短信备份","常用号码查询","程序锁"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advancedtools);
        initView();
        initData();
        initEvent();
    }



    private void initView() {
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mAdvancedToolsActivity_gv = (GridView) findViewById(R.id.advancedToolsActivity_gv);

    }
    private void initData() {

        mTitleLayout_tv_title.setText("高级工具");

        ArrayList<AdvancedToolsBean> advancedToolsBeen = new ArrayList<>();
        AdvancedToolsBean advancedToolsBean = null;
        for (int i = 0; i <imgs.length ; i++) {
             advancedToolsBean = new AdvancedToolsBean();
            advancedToolsBean.setImg(imgs[i]);
            advancedToolsBean.setStr(msgs[i]);
            advancedToolsBeen.add(advancedToolsBean);
        }
        AdvancedToolsAdapter advancedToolsAdapter = new AdvancedToolsAdapter(UiUtils.getContext(),advancedToolsBeen);
        mAdvancedToolsActivity_gv.setAdapter(advancedToolsAdapter);
    }
    private void initEvent() {
        mTitleLayout_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdvancedToolsActivity_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0: //归属地查询
                        Intent intent0 = new Intent(UiUtils.getContext(), AttributionQueryActivity.class);
                        startActivity(intent0);
                        break;
                    case 1://短信备份
                        final ProgressDialog progressDialog = new ProgressDialog(AdvancedToolsActivity.this);
                        progressDialog.setTitle("短信备份");
                        //设置进度条对话框的风格为水平的的样式
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.show();

                        ThreadManager.getThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                String storePath = null;
                                boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                                if (sdCardExist){
                                    //获取SD卡的存储路径
                                     storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms.xml";
                                }else {
                                    //获取Android 提供的cache文件夹
                                    storePath = UiUtils.getContext().getCacheDir().getPath() + File.separator + "sms.xml";
                                }

                                //通过自定义的函数备份短信
                                SMSBackup.backups(UiUtils.getContext(), storePath, new SMSBackup.OnBackupsClickListener() {
                                    @Override
                                    public void setMax(int max) {
                                        progressDialog.setMax(max);
                                    }
                                    @Override
                                    public void setProgress(int currentValue) {
                                        progressDialog.setProgress(currentValue);
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        });

                        break;
                    case 2://常用号码查询
                        startActivity(new Intent(AdvancedToolsActivity.this,CommonlyUsedNumberActivity.class));

                        break;
                    case 3://程序锁
                        startActivity(new Intent(AdvancedToolsActivity.this,AppLockActivity.class));


                        break;
                }

            }
        });
    }



}
