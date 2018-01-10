package com.xgj.phoneguardian.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.CommonlyUsedNumberElvAdapter;
import com.xgj.phoneguardian.engine.Child;
import com.xgj.phoneguardian.engine.CommonlyUsedNumberDao;
import com.xgj.phoneguardian.engine.Group;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.LogUtils;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/9/15 16:59
 * @brief: 常用号码查询
 * 常用号码的数据来自commonnum.db的数据库中
 * 1.首先需要将当前的commonnum.db数据库在欢迎页面拷贝到本地文件中
 */
public class CommonlyUsedNumberActivity extends Activity {

    private static final String TAG = "CommonlyUsedNumberActivity";
    private static final int DATA = 0x001;
    private ExpandableListView mCommonlyUsedNumberActivity_elv;
    private TextView mTitleLayout_tv_title;
    private LinearLayout mTitleLayout_ll_back;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DATA:
                    List<Group> groupList = (List<Group>) msg.obj;
                    for (int i = 0; i < groupList.size(); i++) {
                        Group group = groupList.get(i);
                        String idx = group.getIdx();
                        String name = group.getName();

                        LogUtils.i(TAG, "idx:" + idx);
                        LogUtils.i(TAG, "name:" + name);
                        /*
                         idx:1
                         name:订餐电话
                         */
                        List<Child> childList = group.getChildList();
                        for (int j = 0; j < childList.size(); j++) {
                            Child child = childList.get(j);
                            String id = child.get_id();
                            String name1 = child.getName();
                            String number = child.getNumber();

                            LogUtils.i(TAG, "id:" + id);
                            LogUtils.i(TAG, "name1:" + name1);
                            LogUtils.i(TAG, "number:" + number);
                        /*
                        id:1
                        name1:麦当劳麦乐送
                        number:4008517517
                         */
                        }
                    }

                    mCommonlyUsedNumberElvAdapter = new CommonlyUsedNumberElvAdapter(CommonlyUsedNumberActivity.this, groupList);
                    mCommonlyUsedNumberActivity_elv.setAdapter(mCommonlyUsedNumberElvAdapter);


                    break;
            }
        }
    };
    private CommonlyUsedNumberElvAdapter mCommonlyUsedNumberElvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_commonly_used_number);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);

        mCommonlyUsedNumberActivity_elv = (ExpandableListView) findViewById(R.id.commonlyUsedNumberActivity_elv);


    }

    private void initData() {
        mTitleLayout_tv_title.setText("常用号码查询");

        //因为查询数据库是属于耗时操作，那么就需要开辟一个子线程来完成
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                List<Group> groupList = CommonlyUsedNumberDao.getGroup();
                Message message = mHandler.obtainMessage();
                message.obj = groupList;
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

        //给ExpandableListView设置一个子控件的点击事件
        mCommonlyUsedNumberActivity_elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //获取当前点击的子条目对象
                Child child = (Child) mCommonlyUsedNumberElvAdapter.getChild(groupPosition, childPosition);
                startCall(child.getNumber());

                return false;
            }
        });
    }


    /**
     * 拨打电话
     * @param number
     */
    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }


}
