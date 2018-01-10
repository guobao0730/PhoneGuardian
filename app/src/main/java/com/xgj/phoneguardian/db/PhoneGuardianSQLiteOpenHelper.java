package com.xgj.phoneguardian.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.db
 * @date： 2017/8/7 14:45
 * @brief: 手机卫士数据库帮助类
 */
public class PhoneGuardianSQLiteOpenHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "phoneguardian.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME_ONE = "blacklist";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String MODEL ="model" ;


    public PhoneGuardianSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //初始化黑名单表结构
        BlacklistDao.init(db);
        //初始化程序锁表结构
        AppLockDao.init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * 当当前的APK已经安装到手机，并且表已经创建，如果下次还需要在该表中添加新的字段时，需要通过修改当前数据库的版本号，
     * 并再onUpgrade()中判断newVersion并调用该函数实现添加字符串的操作，否则会出现程序莫名的程序崩溃
     * 在表中添加字段方法 操作步骤： 1、先更改表名 2、创建新表,表名为原来的表名 3、复制数据 4、删除旧表
     * @param db
     *            数据库名
     * @param newColumnArr
     *            添加的新字段的表名数组
     * @param oldTableName
     *            旧表名，在方法内部将旧表名修改为 _temp_+oldTableName
     */
    private void addColumn(SQLiteDatabase db, String[] newColumnArr,
                           String oldTableName) {

        if (db == null || newColumnArr == null || newColumnArr.length < 1
                || TextUtils.isEmpty(oldTableName)) {
            // 数据库为空，新字段个数为0，添加字段后的字段数组个数为0，旧表名为空
            return;
        }

        // 拿到旧表中所有的数据
        Cursor cursor = db.rawQuery("select * from " + oldTableName, null);
        if (cursor == null) {
            // 如果游标为空，说明旧表中没有数据，如果是这种情况那就可以直接改表的字段，不需要转移数据了，代码后面也有，省略掉复制数据的操作就好
            return;
        }
        // 拿到原来的表中所有的字段名
        String[] oldColumnNames = cursor.getColumnNames();

        // 更改原表名为临时表
        String tempTableName = "_temp_" + oldTableName;
        db.execSQL(
                "alter table " + oldTableName + " rename to " + tempTableName);

        // 创建新表
        if (oldColumnNames.length < 1) {
            // 如果原来的表中字段个数为0
            return;
        }

        // 创建一个线程安全的字符串缓冲对象，防止用conn多线程访问数据库时造成线程安全问题
        StringBuffer createNewTableStr = new StringBuffer();
        createNewTableStr
                .append("create table if not exists " + oldTableName + "(");
        for (int i = 0; i < oldColumnNames.length; i++) {
            if (i == 0) {
                createNewTableStr.append(oldColumnNames[i]
                        + " integer primary key autoincrement,");
            } else {
                createNewTableStr.append(oldColumnNames[i] + ",");
            }
        }

        for (int i = 0; i < newColumnArr.length; i++) {
            if (i == newColumnArr.length - 1) {
                // 最后一个
                createNewTableStr.append(newColumnArr[i] + ")");
            } else {
                // 不是最后一个
                createNewTableStr.append(newColumnArr[i] + ",");
            }
        }

        db.execSQL(createNewTableStr.toString());

        // 复制旧表数据到新表
        StringBuffer copySQLStr = new StringBuffer();
        copySQLStr.append("insert into " + oldTableName + " select *,");
        // 有多少个新的字段，就要预留多少个' '空值给新字段
        for (int i = 0; i < newColumnArr.length; i++) {
            if (i == newColumnArr.length - 1) {
                // 最后一个
                copySQLStr.append("' ' from " + tempTableName);
            } else {
                // 不是最后一个
                copySQLStr.append("' ',");
            }
        }
        db.execSQL(copySQLStr.toString());
        // 删除旧表
        db.execSQL("drop table " + tempTableName);
        // 关闭游标
        cursor.close();
    }

}
