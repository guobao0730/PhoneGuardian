package com.xgj.phoneguardian.engine;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/9/18 16:18
 * @brief: 常用号码列表的父类(订餐电话，公共服务,运营商等等)
 */
public class Group {

    private String name;
    private String idx;
    private List<Child> mChildList;

    public List<Child> getChildList() {
        return mChildList;
    }

    public void setChildList(List<Child> childList) {
        mChildList = childList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}
