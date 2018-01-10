package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xgj.phoneguardian.bean.JsonBean;
import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.Md5Utils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.StreamUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/6 0006 16:47
 * @des ${主页面}
 */
public class MainActivity extends Activity {

    private static final int UPDATE_DIALOG = 1;
    private static final int ERROR_NETWORK = 2;
    private static final int ERROR_SERVER = 3;
    private static final int ERROR_JSON = 4 ;
    private static final int ERROR_404 = 5;

    private JsonBean mJsonBean;
    private String mDesc;
    private String mServerVersion;

    private ProgressDialog mProgDialog;

    private int mVersionCode;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case UPDATE_DIALOG:

                    //显示更新信息的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("更新提醒");
                    //对话框要显示的信息（最新的版本号  ， 描述信息）
                    builder.setMessage("版本："+mServerVersion+"\n"+"最新版特征："+"\n"+mDesc);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.i("SplashActivity", "取消更新软件");

                        }
                    });
                    builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.i("SplashActivity", "开始更新软件");
                            dowmloadNewApk();
                        }
                    });

                    //显示对话框
                    builder.show();



                    break;
                case ERROR_SERVER:

                    UiUtils.showToast("服务器异常");

                    break;
                case ERROR_NETWORK:
                    UiUtils.showToast("网络异常");
                    break;
                case ERROR_JSON:
                    UiUtils.showToast("JSON异常");

                    break;
                case ERROR_404:
                    //资源找不到异常
                    UiUtils.showToast("404异常");

                    break;


            }





        }



    };
    private GridView mGv_main_menu;

    //GridView要显示的图片
    private int[] images = {R.mipmap.phone_bak, R.mipmap.communiction, R.mipmap.software, R.mipmap.process, R.mipmap.traffic_analysis, R.mipmap.virus
, R.mipmap.cache_cleaner, R.mipmap.utils, R.mipmap.set };
    //GridView要显示的文字
    private String[] names = {"手机防盗","通讯卫士","软件管家","进程管理","流量统计","病毒查杀","缓存清理","高级工具","设置中心",};
    private LinearLayout mTitleLayout_ll_back;
    private TextView mTitleLayout_tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        initView();

        initData();

        initEvent();




        //判断是否需要更新应用
        checkVersion();
    }


    private void initView() {
        mGv_main_menu = (GridView) this.findViewById(R.id.gv_main_menu);

    }


    private void initData() {


        //获取该应用的版本号
        mVersionCode = getVersionCode();

        //给GridView设置适配器
        mGv_main_menu.setAdapter(new GridViewAdapter());

    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        //给GridView设置条目单机事件监听器
        mGv_main_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0://手机防盗

                        //再点击在手机防盗后判断是否已经设置过密码了，如果没有设置过密码就进入密码的对话框，否则进入登录密码的对话框

                        if (TextUtils.isEmpty(SpUtils.getString(Constant.PASSWORD, ""))) {

                            //设置密码
                            showInputPasswordDiaLog();

                        } else {

                            //登录密码
                            showLoginDiaLog();

                        }


                        break;
                    case 1://通讯卫士
                        startActivity(new Intent(MainActivity.this,CommunicationGuardActivity.class));
                        break;
                    case 2://软件管家
                        startActivity(new Intent(MainActivity.this,SoftwareManagerActivity.class));
                        break;
                    case 3://进程管理
                        startActivity(new Intent(MainActivity.this,ProcessManagementActivity.class));
                        break;
                    case 7: //高级工具
                        Intent intent7 = new Intent(UiUtils.getContext(), AdvancedToolsActivity.class);
                        startActivity(intent7);
                        break;
                    case 8: //设置中心

                        Intent intent = new Intent(UiUtils.getContext(), SetingActivity.class);
                        startActivity(intent);

                        break;


                }


            }
        });



    }



    /**
     * 检查应用程序的的版本信息
     * 获取服务器的数据并解析JSON
     */
    private void checkVersion() {

        new Thread(){


            @Override
            public void run() {
                super.run();

                try {
                    URL url = new URL("http://192.168.56.1:8080/PhoneGuardian.json");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setConnectTimeout(5000);

                    httpURLConnection.setRequestMethod("GET");

                    int responseCode = httpURLConnection.getResponseCode();

                    if (responseCode==200){

                        InputStream inputStream = httpURLConnection.getInputStream();

                        //将流对象转换为字符串
                        String jsonData = StreamUtils.StreamToString(inputStream);

                        Log.i("SplashActivity", "json数据：" + jsonData);

                        //解析JSON数据
                        mJsonBean = parseJson(jsonData);

                        Log.i("SplashActivity", "最新应用程序版本信息：" + mJsonBean.getVersion());
                        //判断是否要更新
                        isNewVersion(mJsonBean);








                        //最后断开网络连接
                        httpURLConnection.disconnect();
                    }else {

                        //否则结果码可能就是404(资源找不到异常)
                        mHandler.sendEmptyMessage(ERROR_404);
                        Log.i("SplashActivity", "资源找不到异常，结果码："+String.valueOf(responseCode));

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    mHandler.sendEmptyMessage(ERROR_SERVER);
                    Log.i("SplashActivity", "服务器异常：" + e.getLocalizedMessage());
                } catch (IOException e) {
                    e.printStackTrace();

                    mHandler.sendEmptyMessage(ERROR_NETWORK);
                    Log.i("SplashActivity", "网络异常：" + e.getLocalizedMessage());
                }


            }


        }.start();


    }



    /**
     * 解析应用程序最新版本的JSON数据
     * @param jsonData
     * @return
     */
    private JsonBean parseJson(String jsonData) {
        //实例化对照JSON数据表的自定义的类
        JsonBean jsonBean = new JsonBean();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            //最新版应用程序的版本号
            String version = jsonObject.getString("version");
            //最新版应用程序的下载地址
            String url = jsonObject.getString("url");
            //最新版应用程序的描述信息
            String desc = jsonObject.getString("desc");

            //将数据存入到类属性中
            jsonBean.setVersion(version);
            jsonBean.setUrl(url);
            jsonBean.setDesc(desc);



        } catch (JSONException e) {
            e.printStackTrace();

            mHandler.sendEmptyMessage(ERROR_JSON);
            Log.i("SplashActivity", "JSON异常：" + e.getLocalizedMessage());
        }


        return jsonBean;
    }


    /**
     * 判断是否需要更新
     * 注：此方法是在子线程中执行的，而此方法又需要更新UI(跳转页面，显示对话框)，那么就需要利用Handler
     */
    private void isNewVersion(JsonBean jsonBean) {

        //获取最新版本的描述信息
        mDesc = jsonBean.getDesc();

        //获取服务器的版本
        mServerVersion = jsonBean.getVersion();

        //如果服务器的版本不是当前的版本
        if (Integer.parseInt(mServerVersion) !=mVersionCode){
            //那么就是有更新的数据，那么弹出更新的对话框
            mHandler.sendEmptyMessage(UPDATE_DIALOG);
        }



    }



    /**
     * 下载新的APK
     * 利用XUtils来实现
     */
    private void dowmloadNewApk() {

        //显示正在下载的进度条对话框
        showProgressDialog();

        //实例化XUtils架包中的类
        HttpUtils httpUtils = new HttpUtils();

        Log.i("SplashActivity", "手机内置SD卡路径：" + Environment.getExternalStorageDirectory().getPath());

        //开始下载，传入要下载的URL地址，将下载好的的文件存放的路径（注意加权限），下载结果监听器(下载的是文件所以泛型里面传入的是File)
        httpUtils.download(mJsonBean.getUrl(), Environment.getExternalStorageDirectory().getPath() + "/PhoneGuardian.apk", new RequestCallBack<File>() {


            /**
             * 下载成功
             * @param responseInfo
             */
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {

                //取消对话框
                dissmissProgressDialog();

                Log.i("SplashActivity", "下载成功");

                UiUtils.showToast("更新成功");

                //安装APK
                installApk();
            }

            /**
             * 下载失败
             * @param e
             * @param s
             */
            @Override
            public void onFailure(HttpException e, String s) {

                //取消对话框
                dissmissProgressDialog();

                Log.i("SplashActivity", "下载失败");
                UiUtils.showToast("更新失败");

            }

            /**
             * 表示正在下载中的状态（为了显示下载的实际进度而重写的）
             * @param total 要下载的文件的总进度
             * @param current 要下载的文件的当前进度
             * @param isUploading
             */
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);

                //为了显示下载的实际进度而设置进度条的最大值  和  当前的值
                mProgDialog.setMax((int) total);
                mProgDialog.setProgress((int) current);

            }
        });



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
        //设置为水平样式的进度条对话框
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //false不允许用户点击手机左下角的返回键取消该对话框
        mProgDialog.setCancelable(false);
        mProgDialog.setMessage("正在拼命下载中...");
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
     * 安装新的APK
     */
    private void installApk() {

        //意图构造传入Action的名称
        Intent intent = new Intent("android.intent.action.VIEW");
        //添加种类为为默认的
        intent.addCategory("android.intent.category.DEFAULT");
        //要安装的文件路径
        Uri data =Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/PhoneGuardian.apk")) ;
        //意图的数据类型（安装界面还是打电话的界面）
        String type = "application/vnd.android.package-archive";
        //因为不能同时设置data跟Type所以要用此方法兼容
        intent.setDataAndType(data,type);
        startActivity(intent);


    }

    public int getVersionCode() {

        int mVersionCode = 0;

        //获取当前程序的版本信息
        PackageManager packageManager = getPackageManager();

        try {
            //获取包信息，传入该应用的包名，旗标（PERMISSION_GRANTED/许可授予）
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.PERMISSION_GRANTED);

            //获取该应用的版本码
            mVersionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return mVersionCode;
    }


    /**
     * GridView的适配器
     */
    class GridViewAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //GridView要显示的视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(UiUtils.getContext(), R.layout.item_grid_view_main, null);

            //定位GridView内部的组件并设置显示的数据
            ImageView iv_main_gridView_item = (ImageView) view.findViewById(R.id.iv_main_gridView_item);
            TextView tv_main_gridView_item = (TextView) view.findViewById(R.id.tv_main_gridView_item);

            //设置数据
            iv_main_gridView_item.setImageResource(images[position]);
            tv_main_gridView_item.setText(names[position]);


            return view;
        }
    }


    /**
     * 手机防盗弹出的对话框
     */
    private void showInputPasswordDiaLog() {

        //将自定义的对话框XML布局转换为View
        View view = View.inflate(UiUtils.getContext(), R.layout.input_password_dialog, null);

        //创建自定义的View(注意对话框的上下文必须写本Activity的上下文，否则报错)
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setView(view).create();

        //设置点击对话框外部的时候解散该对话框
        alertDialog.setCanceledOnTouchOutside(true);
        //设置点击手机左下角的返回键时解散该对话框
        alertDialog.setCancelable(true);
        alertDialog.show();

        final TextView  ed_main_password_one = (TextView) view.findViewById(R.id.ed_main_password_one);
        final TextView  ed_main_password_two = (TextView) view.findViewById(R.id.ed_main_password_two);

        //确认
        TextView tv_main_password_dialog_validation = (TextView) view.findViewById(R.id.tv_main_password_dialog_validation);
        //取消
        TextView tv_main_password_dialog_cancel = (TextView) view.findViewById(R.id.tv_main_password_dialog_cancel);

        tv_main_password_dialog_validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passwordOne = ed_main_password_one.getText().toString().trim();
                String passwordTwo = ed_main_password_two.getText().toString().trim();

                if (TextUtils.isEmpty(passwordOne)||TextUtils.isEmpty(passwordTwo)){
                    UiUtils.showToast("密码不能为空");
                    return;
                }else if (!passwordOne.equals(passwordTwo)){

                    UiUtils.showToast("密码不一致");
                    return;
                    //如果两次输入的密码相同
                }else if (passwordOne.equals(passwordTwo)){

                    //那么首先将密码进行 两次MD5加密 然后在存储
                    passwordOne = Md5Utils.md5(Md5Utils.md5(passwordOne));

                    Log.i("MainActivity", "加密后：" + passwordOne);
                    //那么存储密码
                    SpUtils.putString(Constant.PASSWORD, passwordOne);
                    UiUtils.showToast("设置密码成功");


                }


                //取消对话框
                alertDialog.dismiss();
            }
        });

        tv_main_password_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    /**
     * 登录密码的对话框
     */
    private void showLoginDiaLog() {

        View view = View.inflate(UiUtils.getContext(), R.layout.login_password_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setView(view).create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);

        alertDialog.show();

        final EditText ed_main_login_password = (EditText) view.findViewById(R.id.ed_main_login_password);
        TextView tv_main_login_password_dialog_validation = (TextView) view.findViewById(R.id.tv_main_login_password_dialog_validation);
        TextView tv_main_login_password_dialog_cancel = (TextView) view.findViewById(R.id.tv_main_login_password_dialog_cancel);

        //确认
        tv_main_login_password_dialog_validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = ed_main_login_password.getText().toString().trim();

                if (TextUtils.isEmpty(password)){
                    UiUtils.showToast("密码不能为空");
                    return;
                    //对输入的密码进行  两次MD5加密  然后判断是否是以前设置的 经过两次MD5加密 的数据
                }else if (!Md5Utils.md5(Md5Utils.md5(password)).equals(SpUtils.getString(Constant.PASSWORD, ""))){
                    UiUtils.showToast("密码错误");


                }else if (Md5Utils.md5(Md5Utils.md5(password)).equals(SpUtils.getString(Constant.PASSWORD,""))){

                    //那么登录成功
                    UiUtils.showToast("登录成功");

                    //取消对话框
                    alertDialog.dismiss();



                    //登录成功以后再次判断是否是第一次进入手机防盗页面，如果是那么先进入手机防盗引导页面，否则进入手机防盗主页面
                    //如果存储了数据
                    if(SpUtils.getBoolean(Constant.IS_FIRST,false)){
                        //进入手机防盗页面
                        Intent intent = new Intent(UiUtils.getContext(), PhoneBakActivity.class);
                        startActivity(intent);
                    }else{

                        //进入手机防盗设置向导页面
                        Intent intent = new Intent(UiUtils.getContext(), PhoneBakSetingOneActivity.class);
                        startActivity(intent);


                    }


                }

            }
        });

        //取消
        tv_main_login_password_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


    }




}
