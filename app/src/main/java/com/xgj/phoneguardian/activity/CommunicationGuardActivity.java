package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.adapter.BlacklistAdapter;
import com.xgj.phoneguardian.bean.BlacklistBean;
import com.xgj.phoneguardian.db.BlacklistDao;
import com.xgj.phoneguardian.manager.ThreadManager;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/8/7 14:23
 * @brief: 通讯卫士页面
 */
public class CommunicationGuardActivity extends Activity {

    private static final String TAG = "CommunicationGuardActivity";
    private static final int BLACKLIST =0x001 ;
    private static final int MORE_DATA = 0x002;
    private ImageView mIv_seting_back;
    private ImageView mCommunicationGuardActivity_iv_addBlacklist;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case BLACKLIST:
                    mBlacklistBeen = (List<BlacklistBean>) msg.obj;
                    //设置适配器
                    mBlacklistAdapter = new BlacklistAdapter(UiUtils.getContext(), mBlacklistBeen);
                    mCommunicationGuardActivity_lv_blacklist.setAdapter(mBlacklistAdapter);
                    break;
                case MORE_DATA: //更多
                    if (mBlacklistAdapter!=null){
                        mBlacklistAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    private ListView mCommunicationGuardActivity_lv_blacklist;
    private BlacklistAdapter mBlacklistAdapter;
    private List<BlacklistBean> mBlacklistBeen;
    //为了防止滑动太快而导致重复加载数据，那么需要自定义一个旗标，等数据加载完毕后才能继续加载更多的数据
    private boolean isLoad = false;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_guard);
        initView();
        initData();
        initEvent();
    }




    private void initView() {
        mIv_seting_back = (ImageView) findViewById(R.id.iv_seting_back);
        mCommunicationGuardActivity_iv_addBlacklist = (ImageView) findViewById(R.id.communicationGuardActivity_iv_addBlacklist);
        mCommunicationGuardActivity_lv_blacklist = (ListView) findViewById(R.id.communicationGuardActivity_lv_blacklist);

    }
    private void initData() {
        /*for (int i = 0; i <100 ; i++) {
            BlacklistBean blacklistBean = new BlacklistBean();
            blacklistBean.setModel((new Random().nextInt(3)+1)+"");
            blacklistBean.setPhoneNumber(i+"");
            BlacklistDao.getInstance(UiUtils.getContext()).insert(blacklistBean);
        }*/
        //初始化黑名单数据
        initAddBlacklistData();


    }

    /**
     * 初始化黑名单数据
     */
    private void initAddBlacklistData() {
        //从数据库查询黑名单的数据集
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //默认查询数据库中最后的20条数据
                List<BlacklistBean> blacklistBeen = BlacklistDao.getInstance(UiUtils.getContext()).queryPart(0);
                //获取该表中条目的总个数
                mCount = BlacklistDao.getInstance(UiUtils.getContext()).getCount();
                Message message = mHandler.obtainMessage();
                message.obj = blacklistBeen;
                message.what = BLACKLIST;
                mHandler.sendMessage(message);
            }
        });
    }

    private void initEvent() {
        mIv_seting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加黑名单
        mCommunicationGuardActivity_iv_addBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个自定义的对话框
                showAddBlacklistDialog();

            }
        });

        //实现加载更多所需要的滚动监听器
        mCommunicationGuardActivity_lv_blacklist.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 当ListView中条目滚动的状态发生改变时所调用的方法
             * @param view
             * @param scrollState 表示当前的滚动状态
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //AbsListView.OnScrollListener.SCROLL_STATE_IDLE 表示当前的滚动状态为空闲
                //AbsListView.OnScrollListener.SCROLL_STATE_FLING 表示当前的滚动状态为飞速滚动
                //AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 表示当前的滚动状态为触摸滚动
                if (mBlacklistBeen!=null&&mBlacklistBeen.size()>0){
                    //加载更多的触发条件
                    //1.当前ListView必须处于为空闲的状态(也就是滚动停止的状态)
                    //2.当前最后一个条目处于显示的状态(也就是最后一个显示的下标>=当前ListView数据集长度-1) ，getLastVisiblePosition()获取的是0~n的下标，mBlacklistBeen.size()获取的是长度，所说判断时要-1
                    //3.自定义的旗标，防止重复加载数据而设置的
                    if (scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            &&mCommunicationGuardActivity_lv_blacklist.getLastVisiblePosition()>=mBlacklistBeen.size()-1
                            &&!isLoad){

                        //还要判断如果当前数据库中的总数据个数>当前ListView数据集（也就是如果还有数据，那么才加载更多的数据）
                        LogUtils.i(TAG,"mCount:"+mCount+"\tmBlacklistBeen.size():"+mBlacklistBeen.size());
                        if (mCount>mBlacklistBeen.size()){
                            //加载更多数据
                            ThreadManager.getThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    //表示从当前数据集的长度开始为下标在该表中查询再次查询倒数第二组的20条数据
                                    List<BlacklistBean> blacklistBeen = BlacklistDao.getInstance(UiUtils.getContext()).queryPart(mBlacklistBeen.size());
                                    //将当前的查询出的更多数据添加到全局的用于ListView展示的数据集中
                                    mBlacklistBeen.addAll(blacklistBeen);
                                    //刷新数据
                                    mHandler.sendEmptyMessage(MORE_DATA);

                                }
                            });

                        }
                    }

                }


            }

            /**
             * 当ListView中条目发生滚动的时候调用的方法
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    /**
     * 添加黑名单对话框
     */
    private void showAddBlacklistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunicationGuardActivity.this);
        final AlertDialog alertDialog = builder.create();

        View view = View.inflate(UiUtils.getContext(), R.layout.dialog_add_blacklist, null);
        //因为想实现去除默认对话框中上下的间距，那么需要以下函数
        alertDialog.setView(view,0,0,0,0);
        Button addBlacklistDialog_bt_confirm = (Button) view.findViewById(R.id.addBlacklistDialog_bt_confirm);
        Button addBlacklistDialog_bt_cancel = (Button) view.findViewById(R.id.addBlacklistDialog_bt_cancel);
        final EditText addBlacklistDialog_et = (EditText) view.findViewById(R.id.addBlacklistDialog_et);
        RadioGroup addBlacklistDialog_rg = (RadioGroup) view.findViewById(R.id.addBlacklistDialog_rg);

        final BlacklistBean blacklistBean = new BlacklistBean();
        //表示设置默认为短信拦截的状态
        blacklistBean.setModel(Constant.INTERCEPT_MESSAGES);
        addBlacklistDialog_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.addBlacklistDialog_rb_sms://拦截短信
                        blacklistBean.setModel(Constant.INTERCEPT_MESSAGES);
                        break;
                    case R.id.addBlacklistDialog_rb_phone://拦截电话
                        blacklistBean.setModel(Constant.INTERCEPT_PHONE);
                        break;
                    case R.id.addBlacklistDialog_rb_all: //拦截所有
                        blacklistBean.setModel(Constant.INTERCEPT_ALL);
                        break;
                }
            }
        });
        //确认
        addBlacklistDialog_bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = addBlacklistDialog_et.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)){
                    UiUtils.showToast("请输入拦截号码");
                    return;
                }else {
                    blacklistBean.setPhoneNumber(phoneNumber);
                    //向数据库添加数据
                    BlacklistDao.getInstance(UiUtils.getContext()).insert(blacklistBean);


                    //更新适配器数据
                    //这个时候有两种方式完成数据的刷新的操作，
                    //1.重新从数据库获取数据并刷新（每次添加都需要新开一个线程，相对浪费资源）
                    //2.将添加的数据手动添加到ListView要展示的数据集合中，然后再利用适配器通知更新数据,之间只需要开辟一个线程（步骤繁琐，但效率高，相对浪费资源少）

                     //1.initAddBlacklistData();

                    //2.将添加的数据手动添加到ListView要展示的数据集合中
                    if (mBlacklistBeen!=null){
                        //将新的黑名单数据添加到数据集的头部位置，以实现新添加的在最上面，后添加的在最下面
                        mBlacklistBeen.add(0,blacklistBean);
                        //然后再利用适配器来更新数据
                        if (mBlacklistAdapter!=null){
                            mBlacklistAdapter.notifyDataSetChanged();
                        }

                    }






                    alertDialog.dismiss();
                }
            }
        });
        //取消
        addBlacklistDialog_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }
}
