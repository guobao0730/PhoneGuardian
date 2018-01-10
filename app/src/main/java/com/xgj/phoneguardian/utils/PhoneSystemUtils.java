package com.xgj.phoneguardian.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;

import com.xgj.phoneguardian.bean.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/11 11:32
 * @brief: 手机系统工具类
 */
public class PhoneSystemUtils {



    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    /**
     * 读取手机联系人
     * 注意需加入读联系人和写联系人的权限
     * <!--读取联系人-->
     <uses-permission android:name="android.permission.READ_CONTACTS"></uses-permission>
     <!--写联系人-->
     <uses-permission android:name="android.permission.WRITE_CONTACTS"></uses-permission>
     */
    public static List<ContactsBean> readContacts() {

        //实例化 手机联系人 集合
        List<ContactsBean> contactsBeenLists = new ArrayList<ContactsBean>();


        Uri uri = Uri.parse("content://com.android.contacts/contacts");


        Uri uriDatas = Uri.parse("content://com.android.contacts/data");


        //获取内容解析者 并查询数据库中 的表
        Cursor cursor = UiUtils.getContext().getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        //参数一：传入URL地址,
        //参数二：projection(要取出数据库表中的哪一列)
        //参数三：selection(要取出数据库表中的哪一列的条件)
        //参数四：selectionArgs(要取出数据库表中的哪一列的条件所对应的值)
        //参数四：sortOrder(进行排序的方式)

        //循环取出该列的数据
        while (cursor.moveToNext()) {


            //每循环一次就创建一个联系人对象
            ContactsBean  contactsBean = new ContactsBean();

            //获取联系人ID
            String id = cursor.getString(0);

            //再次查询
            Cursor cursor1 = UiUtils.getContext().getContentResolver().query(uriDatas, new String[]{"data1", "mimetype"}, " raw_contact_id = ? ", new String[]{id}, null);

            //再次循环取出data1列的数据和mimetype列的数据（循环遍历每个联系人的信息）
            while (cursor1.moveToNext()) {

                String data = cursor1.getString(0);
                String mimetype = cursor1.getString(1);

                //再次判断取出联系人 名
                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    Log.i("PhoneSystemUtils", "id:" + id + "  名称：" + data);

                    //讲数据设置进类中
                    contactsBean.setName(data);


                } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {

                    Log.i("PhoneSystemUtils", "id:" + id + "  手机号：" + data);

                    //讲手机号设置进类变量中
                    contactsBean.setPhoneNumber(data);
                }

            }

            //关闭游标释放资源
            cursor1.close();
            //讲当前的 联系人对象 添加到集合中
            contactsBeenLists.add(contactsBean);


        }

        //关闭游标释放资源
        cursor.close();
        //返回 联系人集合
        return contactsBeenLists;

    }


    /**
     * 获取当前的手机号
     * 注意需要添加以下权限
     * <!-- 添加访问手机状态的权限 --> <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     * @return
     */
    public static String getCurrentPhoneNumber(){

        TelephonyManager telephonyManager = (TelephonyManager) UiUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE);

        String currentPhoneNumber = telephonyManager.getLine1Number();

        return currentPhoneNumber;

    }

    /**
     * 发送短信的方法
     * @param phoneNumber
     * @param content
     *  //注意需加发送短信的权限<uses-permission android:name="android.permission.SEND_SMS"></uses-permission>
     */
    public static void sendSMS(String phoneNumber,String content){
        //发送短信
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,content,null,null);
    }


    /**
     *  //实现手机震动的效果
     //注：需加权限 <uses-permission android:name="android.permission.VIBRATE"></uses-permission>
     */
    public static void vibrate(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //震动的时间（毫秒值）
        vibrator.vibrate(200);
    }

    /**
     * @brief 得到屏幕宽
     * @param context
     * @return 屏幕宽度
     * @note
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * @brief 得到屏幕高
     * @param context
     * @return 屏幕高度
     * @note
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取手机内存的路径（存储数据的内存）
     * @return
     */
    public static String getMemoryPath(){
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取可用空间
     * path 传入要获取可用空间大小的路径，如：手机内存的路径 或 SD卡的路径
     * @return
     */
    public static String getSpaceAvailable(Context context,String path){
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long availableBlocks = statFs.getAvailableBlocks();
        //获取可用区块的大小
        long blockSize = statFs.getBlockSize();
        //可用区块的个数*可用区块的大小 = 可用空间大小
        long l = availableBlocks * blockSize;
        //将可用空间大小的数据格式化转换为KB,MB,GB为单位
        String s = Formatter.formatFileSize(context, l);
        return s;
    }

	
	  /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

}
