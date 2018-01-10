package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xgj.phoneguardian.bean.ContactsBean;
import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/11 16:04
 * @brief: 显示联系人的页面 ，注意：因为当前页面只显示一个ListView那么就直接继承自ListActivity
 */
public class ContactsActivity extends Activity {

    private ListView mLv_contacts;
    private static final int SHOW_PROGRESS = 0;
    private static final int DISSMISS_PROGRESS = 1;

    private ProgressDialog mProgDialog;

    //注意必须实例化该集合否则报空指针
    private List<ContactsBean> mContactsBeens = new ArrayList<ContactsBean>();

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case SHOW_PROGRESS:

                    //显示对话框
                    showProgressDialog();


                    break;
                case DISSMISS_PROGRESS:

                    //关闭对话框
                    dissmissProgressDialog();

                    //通知改变
                    //mContactsListViewAdapter.notifyDataSetChanged();



                    break;

            }


        }
    };
    private contactsListViewAdapter mContactsListViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);
        initView();

        getData();
        initData();

        initEvent();



    }




    private void initView() {

        mLv_contacts = (ListView) this.findViewById(R.id.lv_contacts);


    }

    /**
     * 初始化数据
     */
    private void getData() {

        //因为获取手机联系人是一种耗时的操作，那么就开辟一个子线程去获取数据并利用Handler传递数据
        new Thread(){

            @Override
            public void run() {
                super.run();

                //为了用户的体验效果，在获取数据之前应该发送消息给Handler创建一个进度条对话框显示出来
                mHandler.sendEmptyMessage(SHOW_PROGRESS);


                //获取当前ListView要显示的数据(手机联系人)
                mContactsBeens = PhoneSystemUtils.readContacts();

                //测试
                for (int i = 0;i<mContactsBeens.size();i++){
                    ContactsBean contactsBean = mContactsBeens.get(i);

                    String name = contactsBean.getName();
                    String phoneNumber = contactsBean.getPhoneNumber();
                    Log.i("ContactsActivity","联系人："+name +"\t手机号"+phoneNumber);

                }




                //获取完手机联系人以后关闭进度条对话框
                mHandler.sendEmptyMessage(DISSMISS_PROGRESS);


            }
        }.start();


    }


    private void initData() {

        mContactsListViewAdapter = new contactsListViewAdapter();
        //给ListView设置数据
        mLv_contacts.setAdapter(mContactsListViewAdapter);

    }



    /**
     * 显示进度框
     */
    private void showProgressDialog() {

        if (mProgDialog == null)
            mProgDialog = new ProgressDialog(this);//STYLE_SPINNER
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //false表示设置当点击进度条或者进度条外部的时候无响应（）
        mProgDialog.setCanceledOnTouchOutside(false);
        //设置不确定的
        mProgDialog.setIndeterminate(false);
        //false不允许用户点击手机左下角的返回键取消该对话框
        mProgDialog.setCancelable(false);
        mProgDialog.setMessage("正在玩命加载中...");
        mProgDialog.show();
    }


    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (mProgDialog != null && mProgDialog.isShowing()) {
            mProgDialog.dismiss();
        }
    }


    /**
     * 当前显示联系人的 LiseView的适配器
     */
    class  contactsListViewAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return mContactsBeens.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * 该ListView要显示的视图
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //声明ViewHoder
            ViewHoder viewHoder = null;
            //如果缓存视图为空
            if (convertView==null){

                //那么重新赋值
                convertView = View.inflate(UiUtils.getContext(), R.layout.item_contacts_listview, null);

                //实例化ViewHoder类
                viewHoder = new ViewHoder();

                //并将该ListView中的组件赋值给ViewHoder类中的属性
                viewHoder.mTextView  = (TextView) convertView.findViewById(R.id.tv_contacts_item);

                //并给缓存视图设置旗标
                convertView.setTag(viewHoder);

            }else{
                //如果缓存对象不为空那就就继续复用缓存对象
                viewHoder = (ViewHoder) convertView.getTag();

            }

            //获取当前的联系人对象
            ContactsBean contactsBean = mContactsBeens.get(position);

            //将手机号码设置进组件中
            viewHoder.mTextView.setText(contactsBean.getName());



            //返回缓存视图
            return convertView;
        }
    }


    /**
     * 对当前的ListView做优化
     */
    private class ViewHoder{

        TextView mTextView;


    }



    private void initEvent() {

        //显示联系人的 ListView的条目单机事件监听器
        mLv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String phoneNumber = mContactsBeens.get(position).getPhoneNumber();

                Intent intent = new Intent();

                //通过意图对象将数据发送到手机防盗引导3页面
                intent.putExtra(Constant.SECURITY_NUMBER,phoneNumber);

                //设置结果数据
                setResult(200,intent);

                finish();



            }
        });

    }

}


