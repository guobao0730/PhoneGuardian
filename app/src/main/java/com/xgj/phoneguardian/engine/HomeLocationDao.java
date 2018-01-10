package com.xgj.phoneguardian.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xgj.phoneguardian.utils.LogUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/7/30 9:13
 * @brief: 该类用于操作assets目录下的address.db数据库的
 */
public class HomeLocationDao {


    //数据库的路径
    private static final String PATH = UiUtils.getContext().getFilesDir()+"/address.db";
    private static final String TABLE_ONE_NAME = "data1";
    private static final String TAG = "HomeLocationDao";
    private static final String TABLE_TWO_NAME = "data2";


    /**
     * 查询归属地的函数
     * 根据电话在data1表中查询outKey的值，然后再根据outKey查询data2表中对应的归属地的值
     * 实现多表查询
     * @param phoneNumber
     * @return
     */
    public static String query(String phoneNumber){
        String homeLocation = "未知号码";

        //表示如果是以13或15或18开头，后面9位数字结尾
        if (phoneNumber.matches("^(13|15|18)\\d{9}$")){

            //截取前7位的字符串以便在date1表中查询，因为date1表中的号码只有7位数
            phoneNumber = phoneNumber.substring(0, 7);
            //SQLiteDatabase.OPEN_READONLY 表示已只读的方式打开数据库
            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

            /**
             * TABLE_NAME ：表名
             * columns：查询的列名（查询的结果）
             * selection:查询条件(根据什么查)
             */
            Cursor cursor = sqLiteDatabase.query(TABLE_ONE_NAME, new String[]{"outkey"}, "id = ?", new String[]{phoneNumber}, null, null, null);

            //只要查询到，那么就得出结果，所以不需要while循环查
            if (cursor.moveToNext()){
                String outkey = cursor.getString(cursor.getColumnIndex("outkey"));
                //根据id查询data1中的outkey值，然后再根据outKey查询data2表中的归属地值
                LogUtils.i(TAG,"outkey:"+outkey);

                Cursor cursorTwo = sqLiteDatabase.query(TABLE_TWO_NAME, new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (cursorTwo.moveToNext()){
                    String location = cursorTwo.getString(cursorTwo.getColumnIndex("location"));
                    LogUtils.i(TAG,"location:"+location);
                    homeLocation = location;
                }
            }
        }else if (phoneNumber.equals("110")){
            homeLocation = "报警电话";
        }else if (phoneNumber.equals("120")){
            homeLocation = "医疗救护电话";
        }else if (phoneNumber.equals("119")){
            homeLocation = "火警电话";
        }


        return homeLocation;
    }

}
