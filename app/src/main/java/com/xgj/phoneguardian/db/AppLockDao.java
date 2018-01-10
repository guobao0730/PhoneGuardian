package com.xgj.phoneguardian.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.db
 * @date： 2017/11/18 17:52
 * @brief: 程序锁对应的数据库
 * 因为需要永久的记录当前的程序是否加锁的状态，就算是该应用退出了，下次再次进入时还会显示当前程度加锁与未加锁的状态，那么可以通过SP和数据库来实现，又因为需要记录的状态太多，这个时候就要舍弃SP用数据库来永久的记录，如果将要设置程序锁的包名添加到了数据库，说明该程序设置加锁了,否则就未加锁
 */

public class AppLockDao {

    private static final String TABLE_NAME = "applock";
    private static AppLockDao appLockDao;
    private final PhoneGuardianSQLiteOpenHelper mPhoneGuardianSQLiteOpenHelper;
    private static final String PACKAGE_NAME = "package_name";

    private AppLockDao(Context context){
        mPhoneGuardianSQLiteOpenHelper = new PhoneGuardianSQLiteOpenHelper(context);
    }

    /**
     * 通过单利懒汉模式设计该数据库操作类
     * @param context
     * @return
     */
    public static synchronized AppLockDao getInstance(Context context){
        if (appLockDao ==null){
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    public static void init(SQLiteDatabase sQLiteDatabase){
        //	db.execSQL("create table test(_id integer primary key autoincrement,name varchar(20),phone varchar(20))"); //表
        sQLiteDatabase.execSQL("create table "+TABLE_NAME+"(_id integer primary key autoincrement,"+PACKAGE_NAME+ " varchar(100))");
    }

    /**
     * 添加
     * 添加包名就代表该程序是以加锁的状态
     * @param packageName
     * @return
     */
    public boolean insert(String packageName){
        //是否添加成功的标识变量
        long isAddSucceed = 0;

        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        //存数据
        // ContentValues 就相当于Map集合
        ContentValues contentValues = new ContentValues();
        contentValues.put(PACKAGE_NAME,packageName);
        isAddSucceed = readableDatabase.insert(TABLE_NAME, null, contentValues);

        readableDatabase.close();
        return isAddSucceed==-1?false:true;
    }

    /**
     * 根据包名去删除该行对应的数据
     * @param packageName
     */
    public void delete(String packageName){
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();
        //参数二： whereClause 表示删除数据的判断条件，也表示根据什么去删,  ""+PHONE_NUMBER+" = ?"表示根据手机号码去删除表中的数据
        //参数三：判断条件对应的值
        readableDatabase.delete(TABLE_NAME,""+PACKAGE_NAME+" = ?",new String[]{packageName});
        readableDatabase.close();
    }


    /**
     * 查询该表中的所有数据
     * @return
     */
    public List<String> queryAll(){
        ArrayList<String> packageNameList = new ArrayList<>();
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        //查询该表中的所有数据
        Cursor cursor = readableDatabase.rawQuery(" select * from "+TABLE_NAME+"", null);

        //cursor(结果集)
        //如果有结果数据，并且表中行数大于0
        if (cursor!=null&&cursor.getCount()>0){
            //如果游标还可以往下移动，那么就继续循环取出数据
            while (cursor.moveToNext()){
                //根据列的索引获取对应的数据
                String package_name = cursor.getString(cursor.getColumnIndex(PACKAGE_NAME));
                //实例化一个 水源保护区类对象
                packageNameList.add(package_name);
            }
            //关闭结果集
            cursor.close();
        }

        readableDatabase.close();

        return packageNameList;
    }


}
