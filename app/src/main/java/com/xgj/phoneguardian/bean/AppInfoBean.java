package com.xgj.phoneguardian.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.bean
 * @date： 2017/8/21 15:16
 * @brief: 应用信息bean
 */
public class AppInfoBean {
    //应用名称
    private String appName;
    //包名
    private String packageName;
    //应用图标
    private Drawable icon;
    //是否是系统应用
    private boolean isSystemAPP;
    //是否存储在SD卡中
    private boolean isSdcard;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystemAPP() {
        return isSystemAPP;
    }

    public void setSystemAPP(boolean systemAPP) {
        isSystemAPP = systemAPP;
    }

    public boolean isSdcard() {
        return isSdcard;
    }

    public void setSdcard(boolean sdcard) {
        isSdcard = sdcard;
    }
}
