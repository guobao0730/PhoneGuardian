package com.xgj.phoneguardian.engine;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/9/18 16:23
 * @brief: 常用号码的子类（订餐电话旗下的 麦当劳派乐送 400120301031等等）
 */
public class Child {
    private String _id;
    private String number;
    private String name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
