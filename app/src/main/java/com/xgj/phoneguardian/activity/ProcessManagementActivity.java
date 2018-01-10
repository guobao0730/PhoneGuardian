package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.ProgressAdapter;
import com.xgj.phoneguardian.bean.ProgressBean;
import com.xgj.phoneguardian.engine.ProcessInformationProvider;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/8/29 10:12
 * @brief: 进程管理
 */
public class ProcessManagementActivity extends Activity {

    private static final String TAG ="ProcessManagementActivity" ;
    private static final int PROCESS_INFO_DATA = 0x001;
    private TextView mTitleLayout_tv_title;
    private LinearLayout mTitleLayout_ll_back;
    private TextView mProcessManagementActivity_tv_processesNumber;
    private TextView mProcessManagementActivity_tv_processes;
    private ListView mProcessManagementActivity_lv;
    private Button mProcessManagementActivity_bt_checkall;
    private Button mProcessManagementActivity_bt_inverse;
    private Button mProcessManagementActivity_bt_clear;
    private Button mProcessManagementActivity_bt_setting;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case PROCESS_INFO_DATA:
                    List<ProgressBean> progressList = (List<ProgressBean>) msg.obj;
                    mSystemProgress = new ArrayList<>();
                    mUserProgress = new ArrayList<>();
                    for (int i = 0; i < progressList.size(); i++) {
                        ProgressBean progressBean = progressList.get(i);
                        //如果当前进程是系统进程
                        if(progressBean.isSystem()){
                            //那么将当前的对象存储到系统进程集合中
                            mSystemProgress.add(progressBean);
                        }else {
                            mUserProgress.add(progressBean);
                        }
                    }

                    //设置数据
                    mProgressAdapter = new ProgressAdapter(UiUtils.getContext(), mSystemProgress, mUserProgress);
                    mProcessManagementActivity_lv.setAdapter(mProgressAdapter);


                    break;
            }
        }
    };
    private TextView mProcessManagementActivity_tv_title;
    private ArrayList<ProgressBean> mSystemProgress;
    private ArrayList<ProgressBean> mUserProgress;
    private ProgressAdapter mProgressAdapter;
    private int mNumberOfProcesses;
    private long mAvailableMemory;
    private String mtocalMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process_management);
        initView();
        initData();
        initEvent();
    }



    private void initView() {
        mTitleLayout_tv_title = (TextView) findViewById(R.id.titleLayout_tv_title);
        mTitleLayout_ll_back = (LinearLayout) findViewById(R.id.titleLayout_ll_back);
        mProcessManagementActivity_tv_processesNumber = (TextView) findViewById(R.id.processManagementActivity_tv_processesNumber);
        mProcessManagementActivity_tv_processes = (TextView) findViewById(R.id.processManagementActivity_tv_processes);
        mProcessManagementActivity_lv = (ListView) findViewById(R.id.processManagementActivity_lv);
        //全选
        mProcessManagementActivity_bt_checkall = (Button) findViewById(R.id.processManagementActivity_bt_checkall);
        //反选
        mProcessManagementActivity_bt_inverse = (Button) findViewById(R.id.processManagementActivity_bt_inverse);
        //一键清理
        mProcessManagementActivity_bt_clear = (Button) findViewById(R.id.processManagementActivity_bt_clear);
        //设置
        mProcessManagementActivity_bt_setting = (Button) findViewById(R.id.processManagementActivity_bt_setting);

        //常驻悬浮框
        mProcessManagementActivity_tv_title = (TextView) findViewById(R.id.processManagementActivity_tv_title);

    }
    private void initData() {
        mTitleLayout_tv_title.setText("进程管理");

        //获取正在运行的进程总个数
        mNumberOfProcesses = ProcessInformationProvider.getNumberOfProcesses(this);
        mProcessManagementActivity_tv_processesNumber.setText("进程总数："+ mNumberOfProcesses);

        //获取可用内存
        mAvailableMemory = ProcessInformationProvider.getAvailableMemory(this);
        //Formatter.formatFileSize会根据文件字节数转化为合适的单位（B、KB、MB等数值）数值。
        String s2 = Formatter.formatFileSize(this, mAvailableMemory);

        String s1 = Formatter.formatFileSize(this, 1024*1024);
        LogUtils.i(TAG,"s1:"+s1);
        //s1:1.00 MB

        //获取总内存
        long totalMemory = ProcessInformationProvider.getTotalMemory();
        mtocalMemory = Formatter.formatFileSize(this, totalMemory);
        mProcessManagementActivity_tv_processes.setText("可用内存/总内存："+s2+"/"+ mtocalMemory);

        //初始化进程数据 也就是ListView要展示的数据
        initProgressData();

    }

    /**
     * 初始化进程数据
     */
    private void initProgressData() {

        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<ProgressBean> progressList = ProcessInformationProvider.getProgressList(UiUtils.getContext());
                for (int i = 0; i <progressList.size() ; i++) {
                    ProgressBean progressBean = progressList.get(i);
                    LogUtils.i(TAG,"progressBean.getPackageName():"+progressBean.getPackageName());
                    LogUtils.i(TAG,"progressBean.getProgressName():"+progressBean.getProgressName());
                    LogUtils.i(TAG,"progressBean.getProcessSize():"+progressBean.getProcessSize());
                    LogUtils.i(TAG,"progressBean.isSystem():"+progressBean.isSystem());
                }

                Message message = mHandler.obtainMessage();
                message.obj = progressList;
                message.what = PROCESS_INFO_DATA;
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

        //全选
        mProcessManagementActivity_bt_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkall();
            }
        });

        //反选
        mProcessManagementActivity_bt_inverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inverse();
            }
        });

        //一键清理
        mProcessManagementActivity_bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                clear();
            }
        });

        //设置
        mProcessManagementActivity_bt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UiUtils.getContext(),ProcessSetActivity.class));
            }
        });

        mProcessManagementActivity_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }



            /**
             * 当正在滚动的时候调用该函数
             * @param view
             * @param firstVisibleItem 第一个可见条目的下标
             * @param visibleItemCount 当前屏幕的可见条目个数
             * @param totalItemCount 总条目个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mUserProgress!=null&&mSystemProgress!=null){
                    //如果第一个可见的视图条目下标>=系统进程常驻悬浮框
                    //也就是当滚顶到 系统进程常驻悬浮框 时
                    if (firstVisibleItem>=mUserProgress.size()+1){
                        mProcessManagementActivity_tv_title.setText("系统进程("+mSystemProgress.size()+")");
                    }else {
                        mProcessManagementActivity_tv_title.setText("用户进程("+mUserProgress.size()+")");
                    }
                }
            }
        });


        //实现当点击ListView中的条目的时候，将内部的控件设置为选中的状态
        mProcessManagementActivity_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //如果当前点击的条目不是 用户进程常驻悬浮框或者 系统进程常驻悬浮框 ,那么就显示PopupWindow
                if (position==0||position==mUserProgress.size()+1){
                    return;
                }else {
                    ProgressBean progressBean = null;
                    //如果当前的位置在 系统应用常驻悬浮框条目 的范围内
                    if (position<mUserProgress.size()+1){
                        //那么获取当前位置的前一个用户应用对象,以便后期卸载，打开，分享该应用时使用
                        progressBean = mUserProgress.get(position - 1);
                    }else {
                        //那么获取系统应用对象
                        // position-userApp.size()-2 (表示当前的条目-用户的总个数-2个常驻悬浮框)
                        progressBean = mSystemProgress.get(position-mUserProgress.size()-2);
                    }

                    if (progressBean!=null){
                        //如果当前点击的条目不是本应用的条目
                        if (!progressBean.getPackageName().equals(getPackageName())){

                            //对当前的状态取反，如果当前是选中的，那么点击了就是未选中的状态
                            progressBean.setCheck(!progressBean.isCheck());

                            //然后将条目内部的控件设置为取反后的结果
                            ImageView progressinfoAdapter_iv_isChecked = (ImageView) view.findViewById(R.id.progressinfoAdapter_iv_isChecked);
                            progressinfoAdapter_iv_isChecked.setEnabled(progressBean.isCheck());
                        }
                    }


                }
            }
        });
    }

    /**
     * 一键清理所选择的进程
     */
    private void clear() {

        //创建该集合是为了记录已经选中并且要杀死的进程集合
        //注：不能在循环中移除本遍历的集合数据，否则会出现异常
        ArrayList<ProgressBean> killProgressBeen = new ArrayList<>();

        //遍历用户进程集合
        for (int i = 0; i <mUserProgress.size() ; i++) {
            ProgressBean progressBean = mUserProgress.get(i);
            //如果当前的进程是本进程，那么跳过本次循环
            if (progressBean.getPackageName().equals(getPackageName())){
                continue;
            }
            //如果当前的进程被选中了
            if (progressBean.isCheck()){
                //那么将当前的进程对象记录下来以便后续删除对应的UI和删除进程所使用
                killProgressBeen.add(progressBean);
            }
        }

        //遍历系统进程集合
        for (int i = 0; i <mSystemProgress.size() ; i++) {
            ProgressBean progressBean = mSystemProgress.get(i);
            //如果当前的进程被选中了
            if (progressBean.isCheck()){
                //那么将当前的进程对象记录下来以便后续删除对应的UI和删除进程所使用
                killProgressBeen.add(progressBean);
            }
        }

        long killMemory = 0;
        for (int i = 0; i <killProgressBeen.size() ; i++) {
            ProgressBean progressBean = killProgressBeen.get(i);
            //如果用户进程中包含该进程，那么移除当前进程对象
            if (mUserProgress.contains(progressBean)){
                mUserProgress.remove(progressBean);
            }
            //如果系统进程中包含该进程，那么移除当前进程对象
            if (mSystemProgress.contains(progressBean)){
                mSystemProgress.remove(progressBean);
            }
            //根据指定的包名杀死指定的进程
            ProcessInformationProvider.killProcess(this,progressBean.getPackageName());

            //将当前要杀死的进程占用内存数记录下来以便后续更新可用内存UI时所用
            killMemory+=progressBean.getProcessSize();
        }

        //通知适配器更新数据
        if (mProgressAdapter!=null){
            mProgressAdapter.notifyDataSetChanged();
        }

        //清理后更新进程总数UI
        mNumberOfProcesses-=killProgressBeen.size();
        mProcessManagementActivity_tv_processesNumber.setText("进程总数："+ mNumberOfProcesses);

        //更新可用内存UI(可用内存=当前可用内存+以清理的内存)
        mAvailableMemory+=killMemory;
        String s2 = Formatter.formatFileSize(this, mAvailableMemory);
        mProcessManagementActivity_tv_processes.setText("可用内存/总内存："+s2+"/"+ mtocalMemory);


        //给用户提示杀死了多少个进程，释放了多少的内存
        String memory = Formatter.formatFileSize(this, killMemory);
        UiUtils.showToast("杀死了"+killProgressBeen.size()+"个进程，释放了"+memory+"内存");
    }

    /**
     * 反选
     */
    private void inverse() {

        //遍历用户进程
        if(mUserProgress!=null){
            for (int i = 0; i <mUserProgress.size() ; i++) {
                ProgressBean progressBean = mUserProgress.get(i);
                //如果当前的进程是本应用进程，那么跳过本次循环
                if (progressBean.getPackageName().equals(getPackageName())){
                    continue;
                }else {
                    //设置为取反的状态
                    progressBean.setCheck(!progressBean.isCheck());
                }
            }
        }
        //遍历系统进程
        if (mSystemProgress!=null){
            for (int i = 0; i <mSystemProgress.size() ; i++) {
                ProgressBean progressBean = mSystemProgress.get(i);
                //设置为取反的状态
                progressBean.setCheck(!progressBean.isCheck());
            }
        }
        if (mProgressAdapter!=null){
            //通知适配器更新数据
            mProgressAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 全选
     */
    private void checkall() {

        //遍历用户进程
        if(mUserProgress!=null){
            for (int i = 0; i <mUserProgress.size() ; i++) {
                ProgressBean progressBean = mUserProgress.get(i);
                //如果当前的进程是本应用进程，那么跳过本次循环
                if (progressBean.getPackageName().equals(getPackageName())){
                    continue;
                }else {
                    //设置为选中的状态
                    progressBean.setCheck(true);
                }
            }
        }
        //遍历系统进程
        if (mSystemProgress!=null){
            for (int i = 0; i <mSystemProgress.size() ; i++) {
                ProgressBean progressBean = mSystemProgress.get(i);
                //设置为选中的状态
                progressBean.setCheck(true);
            }
        }
        if (mProgressAdapter!=null){
            //通知适配器更新数据
            mProgressAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //为了实现在进程设置页面中控制系统进程的显示与隐藏，那么需要当进程设置页面销毁的时候重新刷新一个适配器的数据，而进程设置页面销毁后就会调用该页面的onResume(),那么就可以通过此函数更新适配器的数据
        if (mProgressAdapter!=null){
            //通知适配器更新数据
            mProgressAdapter.notifyDataSetChanged();
        }

    }

}
