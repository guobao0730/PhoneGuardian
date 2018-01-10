package com.xgj.phoneguardian.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xgj.phoneguardian.bean.BlacklistBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.db
 * @date： 2017/8/7 14:59
 * @brief: 针对黑名单数据库操作的类
 *   // 1 表示短信 2表示电话 3表示所有
 */
public class BlacklistDao {

    private static final String TABLE_NAME = "blacklist";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String MODEL ="model" ;
    private final PhoneGuardianSQLiteOpenHelper mPhoneGuardianSQLiteOpenHelper;

    private static BlacklistDao blacklistDao = null;
    private BlacklistDao(Context context){
        //当实例化该类的时候实例化数据库帮助类
        mPhoneGuardianSQLiteOpenHelper = new PhoneGuardianSQLiteOpenHelper(context);

    }

    /**
     * 通过单利懒汉模式设计该数据库操作类
     * @param context
     * @return
     */
    public static synchronized BlacklistDao getInstance(Context context){
        if (blacklistDao ==null){
            blacklistDao = new BlacklistDao(context);
        }
        return blacklistDao;
    }

    /**
     * 初始化表结构
     * @param sqLiteDatabase
     */
    public static void init(SQLiteDatabase sqLiteDatabase){
        //	db.execSQL("create table test(_id integer primary key autoincrement,name varchar(20),phone varchar(20))"); //表
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+"(_id integer primary key autoincrement,"+PHONE_NUMBER+ " varchar(20),"+MODEL+" varchar(20))");
    }

    /**
     * 添加
     * @param blacklistBean
     * @return
     */
    public boolean insert(BlacklistBean blacklistBean){
        //是否添加成功的标识变量
        long isAddSucceed = 0;

        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        //存数据
        // ContentValues 就相当于Map集合
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHONE_NUMBER,blacklistBean.getPhoneNumber());
        contentValues.put(MODEL,blacklistBean.getModel());
        isAddSucceed = readableDatabase.insert(TABLE_NAME, null, contentValues);

        readableDatabase.close();
        return isAddSucceed==-1?false:true;
    }

    /**
     * 根据手机号删除该表中对应的数据
     * @param blacklistBean
     */
    public void delete(BlacklistBean blacklistBean){
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();
        //参数二： whereClause 表示删除数据的判断条件，也表示根据什么去删,  ""+PHONE_NUMBER+" = ?"表示根据手机号码去删除表中的数据
        //参数三：判断条件对应的值
        readableDatabase.delete(TABLE_NAME,""+PHONE_NUMBER+" = ?",new String[]{blacklistBean.getPhoneNumber()});
        readableDatabase.close();
    }

    /**
     * 修改
     * 根据手机号去修改当前手机号拦截的模式
     * @param blacklistBean
     */
    public void update(BlacklistBean blacklistBean){
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        //要修改的数据,也就是模式
        ContentValues contentValues = new ContentValues();
        contentValues.put(MODEL,blacklistBean.getModel());

        //参数二:ContentValues 表示的是要修改的数据
        //参数三：whereClause 修改的条件，"phone = ?"表示根据手机号去修改其中的数据
        //参数四：修改条件对应的值
        readableDatabase.update(TABLE_NAME,contentValues,""+PHONE_NUMBER+" = ?",new String[]{blacklistBean.getPhoneNumber()});

        readableDatabase.close();
    }

    /**
     * 查询该表中的所有数据并且进行逆序排序
     * @return
     */
    public List<BlacklistBean> queryAll(){
        ArrayList<BlacklistBean> blacklistBeenLists = new ArrayList<>();
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        /**
         * 参数二：columns 表示查询的字段,new String[]{PHONE_NUMBER,MODEL}表示查询该表当中所有的数据
         * 参数二：selection表示查询的条件，目前是查询该表中所有的数据，那么为null
         * 参数四：selectionArgs 表示查询条件对应的值,目前是查询该表中所有的数据，那么为null
         * 参数五：String groupBy 分组
         * 参数六：String having
         * 参数七：orderBy 表示根据什么排序,
         * 因为需要在添加黑名单的时候让后添加的显示在上面，先添加的在下面，那么需要通过自增长_id倒序排序，所以需要"_id desc"
         * "_id desc" 表示根据_id倒序排序,desc表示倒序排列,默认是正序排列的
         */
        Cursor cursor = readableDatabase.query(TABLE_NAME, new String[]{PHONE_NUMBER, MODEL}, null, null, null, null, "_id desc");
        while (cursor.moveToNext()){
            BlacklistBean blacklistBean = new BlacklistBean();
            //根据建获取值
            String phone_number = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
            String model = cursor.getString(cursor.getColumnIndex(MODEL));

            blacklistBean.setPhoneNumber(phone_number);
            blacklistBean.setModel(model);
            blacklistBeenLists.add(blacklistBean);
        }

        cursor.close();
        readableDatabase.close();
        return blacklistBeenLists;
    }


    /**
     * 因为当前ListView要展示的数太多，那么就可以对其进行优化，实现分页查询，每次只查询显示20条数据
     * 当上拉加载更多的时候实现再加载20条数据，而不是一下全部都加载,并且实现逆序展示查询的数据
     * @param index 查询的索引值 0~20,20~40,40~60
     * @return
     */
    public List<BlacklistBean> queryPart(int index){
        ArrayList<BlacklistBean> BlacklistBeanLists = new ArrayList<>();
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();
        //select *  表示查询所有
        //_id desc 表示根据_id倒序查询
        //limit ?,20;    ? 表示从哪个索引开始查 ,20表示每次查询的个数
        //new String[]{index + ""} 表示  ? 所对应的值
        Cursor cursor = readableDatabase.rawQuery("select * from " + TABLE_NAME + " order by _id desc limit ?,20;", new String[]{index + ""});

        BlacklistBean blacklistBean = null;
        while (cursor.moveToNext()){
             blacklistBean = new BlacklistBean();
            String phone_number = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
            String model = cursor.getString(cursor.getColumnIndex(MODEL));
            blacklistBean.setPhoneNumber(phone_number);
            blacklistBean.setModel(model);
            BlacklistBeanLists.add(blacklistBean);

        }
        cursor.close();
        readableDatabase.close();

        return BlacklistBeanLists;
    }

    /**
     * 获取该表中数据条目的总个数
     * @return
     */
    public int getCount(){
        int count = 0;
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("select count(*) from " + TABLE_NAME + ";", null);
        if (cursor.moveToNext()){
            count = cursor.getInt(cursor.getColumnIndex("count(*)"));
        }
        cursor.close();
        readableDatabase.close();
        return count;
    }

    /**
     * 根据手机号去查询当前手机号的拦截模式
     * @param phoneNumber
     * @return 0：未查询到拦截模式（也就是该手机号没有设置拦截模式） 1：拦截短信  2：拦截电话  3：拦截所有
     */
    public String getModel(String phoneNumber){
        String model = "0";
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();
        /**
         * 参数二：columns 表示查询的字段,new String[]{MODEL}表示查询该表当中的模式（也表示查询的结果）
         * 参数二：selection表示查询的条件，PHONE_NUMBER+" = ?" 表示根据手机号去查询模式
         * 参数四：selectionArgs 表示查询条件对应的值,new String[]{phoneNumber}表示查询条件对应的值
         * 参数五：String groupBy 分组
         * 参数六：String having
         * 参数七：orderBy 表示根据什么排序,
         */
        Cursor cursor = readableDatabase.query(TABLE_NAME, new String[]{MODEL}, PHONE_NUMBER + " = ?", new String[]{phoneNumber}, null, null, null);
        if (cursor.moveToNext()){
             model = cursor.getString(cursor.getColumnIndex(MODEL));
        }
        cursor.close();
        readableDatabase.close();
        return model;
    }


    /**
     * 利用另外一种数据库语法获取该表中的所有数据
     * @return
     */
    public List<BlacklistBean> getAllData(){
        ArrayList<BlacklistBean> blacklistBeenList = new ArrayList<>();
        SQLiteDatabase readableDatabase = mPhoneGuardianSQLiteOpenHelper.getReadableDatabase();

        //查询该表中的所有数据
        Cursor cursor = readableDatabase.rawQuery(" select * from "+TABLE_NAME+"", null);

        BlacklistBean blacklistBean = null;
        while (cursor.moveToNext()){
            blacklistBean = new BlacklistBean();
            String phone_number = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
            String model = cursor.getString(cursor.getColumnIndex(MODEL));
            blacklistBean.setPhoneNumber(phone_number);
            blacklistBean.setModel(model);
            blacklistBeenList.add(blacklistBean);

        }
        cursor.close();
        readableDatabase.close();


        return blacklistBeenList;
    }


}
