package com.xgj.phoneguardian.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.bean
 * @date： 2017/8/30 10:00
 * @brief: 进程bean
 */
public class ProgressBean {
    //进程名称
    private String progressName;
    //进程图标
    private Drawable icon;
    //进程大小
    private long processSize;
    //是否是系统进程
    private boolean isSystem;
    //进程是否选中
    private boolean isCheck;
    //进程包名
    private String packageName;


    public String getProgressName() {
        return progressName;
    }

    public void setProgressName(String progressName) {
        this.progressName = progressName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getProcessSize() {
        return processSize;
    }

    public void setProcessSize(long processSize) {
        this.processSize = processSize;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
