package com.xgj.phoneguardian.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Xml;

import com.xgj.phoneguardian.utils.LogUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/8/17 14:29
 * @brief: 短信备份的类
 */
public class SMSBackup {


    private static final String ADDRESS = "address";
    private static final String DATE ="date" ;
    private static final String TYPE = "type";
    private static final String BODY ="body" ;
    private static final String TAG ="SMSBackup" ;
    private FileOutputStream fileOutputStream;

    public static int index = 0;

    public interface OnBackupsClickListener{
        void setMax(int max);
        void setProgress(int currentValue);

    }


    /**
     * 短信备份的函数
     * 注意需要加权限
     * @param context 上下文
     * @param storePath 存储路径
     * @param onBackupsClickListener 自定义的接口对象（因为不确定用于展示备份进度是使用进度条对话框还是用进度条来实现，所以使用接口回调将当前要设置的数据传出去，由外部决定）
     */
    public static void backups(Context context, String storePath, OnBackupsClickListener onBackupsClickListener){
        Cursor cursor = null;
        FileOutputStream fileOutputStream = null;

        try {

            if (TextUtils.isEmpty(storePath)){
                return;
            }
            //创建短信备份的存储路径
            File file = new File(storePath);
            //根据内容提供者去查询系统存储的短信数据

            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{ADDRESS, DATE, TYPE, BODY}, null, null, null);

            fileOutputStream =  new FileOutputStream(file);


            //因为短信备份是创建的一个XML文件,所以需要组拼XML文件
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fileOutputStream,"utf-8");
            xmlSerializer.startDocument("utf-8",true);

            xmlSerializer.startTag(null,"smss");


            //设置进度条的最大值(通过接口回调的方式将最大值传出去)
            if (onBackupsClickListener!=null){
                onBackupsClickListener.setMax(cursor.getCount());
            }


            while (cursor.moveToNext()){
                xmlSerializer.startTag(null,"sms");

                xmlSerializer.startTag(null,ADDRESS);
                xmlSerializer.text(cursor.getColumnName(cursor.getColumnIndex(ADDRESS)));
                LogUtils.i(TAG,"电话号码："+cursor.getColumnName(cursor.getColumnIndex(ADDRESS)));
                xmlSerializer.endTag(null,ADDRESS);

                xmlSerializer.startTag(null,DATE);
                xmlSerializer.text(cursor.getColumnName(cursor.getColumnIndex(DATE)));
                xmlSerializer.endTag(null,DATE);

                xmlSerializer.startTag(null,TYPE);
                xmlSerializer.text(cursor.getColumnName(cursor.getColumnIndex(TYPE)));
                xmlSerializer.endTag(null,TYPE);

                xmlSerializer.startTag(null,BODY);
                xmlSerializer.text(cursor.getColumnName(cursor.getColumnIndex(BODY)));
                LogUtils.i(TAG,"短信内容："+cursor.getColumnName(cursor.getColumnIndex(BODY)));
                xmlSerializer.endTag(null,BODY);

                xmlSerializer.endTag(null,"sms");

                index++;
                Thread.sleep(500);

                //(通过接口回调的方式将当前值传出去)
                if (onBackupsClickListener!=null){
                    onBackupsClickListener.setProgress(index);
                }

            }
            xmlSerializer.endTag(null,"smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null&&fileOutputStream!=null){
                cursor.close();
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
