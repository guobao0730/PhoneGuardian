package com.xgj.phoneguardian;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.xgj.phoneguardian.bean.ContactsBean;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.ServiceUtils;

import java.util.List;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian
 * @date：2016/8/11 12:27
 * @brief: 自定义的测试类，，新建测试类。注意继承测试父类（此为InstrumentationTestCase类），测试方法以test开头。
 */
public class TestClass extends InstrumentationTestCase {

    public void test(){

        List<ContactsBean> contactsBeens = PhoneSystemUtils.readContacts();
        for (int i = 0;i<contactsBeens.size();i++){

            ContactsBean contactsBean = contactsBeens.get(i);
            String name = contactsBean.getName();
            String phoneNumber = contactsBean.getPhoneNumber();

            Log.i("单元测试","联系人："+name+"\t手机号："+phoneNumber);


        }


    }

    public void testService(){

        boolean startService = ServiceUtils.isStartService("com.qihoo360.mobilesafe.floatwin.service.FloatService");

        Log.i("单元测试",String.valueOf(startService));

    }


    public void testNotification(){

        String currentPhoneNumber = PhoneSystemUtils.getCurrentPhoneNumber();
        Log.i("单元测试","当前的手机号是："+currentPhoneNumber);


    }

    /**
     * 测试数据库
     */
    public void testDB(){

    }


}
