package com.xgj.phoneguardian.bean;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/11 14:20
 * @brief: 手机联系人的JAVAbean
 */
public class ContactsBean {
    //联系人 名
    private String name;
    //手机号
    private String phoneNumber;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
