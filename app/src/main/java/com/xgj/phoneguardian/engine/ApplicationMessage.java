package com.xgj.phoneguardian.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xgj.phoneguardian.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/8/21 15:11
 * @brief: 应用信息类
 */
public class ApplicationMessage {


    /**
     * 获取安装在手机上面的应用信息集合
     * 注意：在获取安装在手机上面的应用信息集相对比较耗时，在调用该函数时最好开辟线程
     * @param context
     */
    public static List<AppInfoBean> getApplicationMessageList(Context context){

        PackageManager packageManager = context.getPackageManager();
        //获取安装在手机上面的应用信息集合
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        ArrayList<AppInfoBean> appInfoBeanList = new ArrayList<>();
        AppInfoBean appInfoBean = null;
        for (int i = 0; i <packageInfoList.size() ; i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
             appInfoBean = new AppInfoBean();
            //获取包名
            appInfoBean.setPackageName(packageInfo.packageName);
            //获取应用信息类
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取应用名称
            appInfoBean.setAppName(applicationInfo.loadLabel(packageManager).toString());
            //获取应用的图标
            appInfoBean.setIcon(applicationInfo.loadIcon(packageManager));

            //如果是系统应用
            if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                appInfoBean.setSystemAPP(true);
            }else {
                //那么就不是系统应用，而是用户自己安装的
                appInfoBean.setSystemAPP(false);
            }

            //判断该应用是否安装在SD卡中
            if ((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                //如果安装在SD卡中
                appInfoBean.setSdcard(true);
            }else {
                //否则没有安装在SD卡中
                appInfoBean.setSdcard(false);
            }

            appInfoBeanList.add(appInfoBean);

        }

        return appInfoBeanList;


    }
}
