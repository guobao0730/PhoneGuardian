package com.xgj.phoneguardian.bean;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.bean
 * @date： 2017/8/7 15:42
 * @brief: 黑名单
 */
public class BlacklistBean {

    private String phoneNumber;
    // 1 表示短信 2表示电话 3表示所有
    private String model;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
