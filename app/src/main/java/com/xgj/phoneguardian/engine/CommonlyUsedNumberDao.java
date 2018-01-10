package com.xgj.phoneguardian.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xgj.phoneguardian.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.db
 * @date： 2017/9/18 14:29
 * @brief: 常用号码数据库操作类
 */
public class CommonlyUsedNumberDao {


    private static final String PATH = UiUtils.getContext().getFilesDir()+"/commonnum.db";
    private static final String TABLE_NAME = "classlist";
    private static final String NAME ="name" ;
    private static final String IDX = "idx";
    private static final String _ID ="_id";
    private static final String NUMBER = "number";


    /**
     * 查询commonnum.db数据库获取父列表的数据
     * @return
     */
    public static List<Group> getGroup(){

        ArrayList<Group> groups = new ArrayList<>();
        //已只读的方式打开一个数据库
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        //要查询的字段为NAME和IDX，null表示没有查询的条件
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String[]{NAME, IDX}, null, null, null, null, null);
        Group group = null;
        while (cursor.moveToNext()){
             group = new Group();
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String idx = cursor.getString(cursor.getColumnIndex(IDX));

            List<Child> child = getChild(idx);

            group.setName(name);
            group.setIdx(idx);
            group.setChildList(child);

            groups.add(group);
        }

        cursor.close();
        sqLiteDatabase.close();

        return groups;
    }


    /**
     * 根据父类中的idx字段获取对应的子表数据
     * idx = 1;(table1)
     * idx = 2;(table2)
     * @param idx
     * @return
     */
    private static List<Child> getChild(String idx){
        ArrayList<Child> childList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        //查询该数据库中的所有表
        Cursor cursor = sqLiteDatabase.rawQuery("select * from table" + idx + ";", null);

        Child child = null;
        while (cursor.moveToNext()){
             child = new Child();
            String _id = cursor.getString(cursor.getColumnIndex(_ID));
            String number = cursor.getString(cursor.getColumnIndex(NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(NAME));

            child.set_id(_id);
            child.setNumber(number);
            child.setName(name);

            childList.add(child);
        }

        cursor.close();
        sqLiteDatabase.close();

        return childList;
    }


}
