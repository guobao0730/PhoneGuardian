package com.xgj.phoneguardian.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.ProgressBean;
import com.xgj.phoneguardian.utils.LogUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.engine
 * @date： 2017/8/29 13:41
 * @brief: 进程信息提供者（主要给进程管理器页面提供数据支持）
 */
public class ProcessInformationProvider {

    private static final String TAG ="ProcessInformationProvider" ;

    /**
     * 获取正在运行的进程总个数
     * @param context
     * @return
     */
    public static int getNumberOfProcesses(Context context){

        //获取ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        return runningAppProcesses.size();
    }

    /**
     * 获取手机的可用内存
     * @param context
     * @return 以byte为单位，需要格式化
     */
    public static long getAvailableMemory(Context context){
        //获取ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //实例化内存信息类ActivityManager.MemoryInfo
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);

        //获取可用内存数 bytes为单位
        return memoryInfo.availMem;

    }


    /**
     * 获取手机的总内存大小
     * @return 以byte为单位返回
     */
    public static long getTotalMemory(){

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            //因为手机的总内存大小一般保存在文件meminfo文件中，所以可通过读取该文件的方式获取其中内存大小值
            fileReader = new FileReader("proc/meminfo");
             bufferedReader = new BufferedReader(fileReader);
            //一般第一行就是手机总内存的数据，读取第一行的字符串，然后通过遍历比对的方式取出具体值
            String s = bufferedReader.readLine();
            char[] chars = s.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i <chars.length ; i++) {
                char aChar = chars[i];
                //如果当前的字符是数字，那么取出
                if (aChar>='0'&&aChar<='9'){
                    stringBuffer.append(aChar);
                }
            }
            //因为返回数字是以KB为单位，而乘以1024是为了以MB为单位
            return Long.parseLong(stringBuffer.toString()) * 1024;

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //最后关流
            if (fileReader!=null&&bufferedReader!=null){
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }


    /**
     * 获取正在运行的进程集合
     * 因为获取正在运行的进程集合比较耗时，那么就需要开启一个子线程来完成获取数据的操作
     * @param context
     * @return
     */
    public static List<ProgressBean> getProgressList(Context context){
        ArrayList<ProgressBean> progressBeenList = new ArrayList<>();

        //获取ActivityManager对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //获取包管理器对象,以便后期获取进程的名称
        PackageManager packageManager = context.getPackageManager();
        ProgressBean progressBean = null;
        for (int i = 0; i < runningAppProcesses.size(); i++) {
             progressBean = new ProgressBean();
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(i);
            //获取进程名称并赋值,进程名称也就是包名
            progressBean.setPackageName(runningAppProcessInfo.processName);
            //获取进程占用的内存大小,runningAppProcessInfo.pid 表示每个进程所对应的一个唯一标识
            Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            //获取当期进程的内存信息对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取该进程已使用的内存大小,因为获取的是以KB为单位，*1024是为了以MB单位
            long totalPrivateDirty = memoryInfo.getTotalPrivateDirty()*1024;
            progressBean.setProcessSize(totalPrivateDirty);
            try {
                //根据包管理器获取ApplicationInfo对象
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(progressBean.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                //获取进程名称
                String progressName = applicationInfo.loadLabel(packageManager).toString();
                progressBean.setProgressName(progressName);

                //获取进程的图标
                Drawable drawable = applicationInfo.loadIcon(packageManager);
                progressBean.setIcon(drawable);

                //判断是否是系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)== ApplicationInfo.FLAG_SYSTEM){
                    //是系统进程
                    progressBean.setSystem(true);
                }else {
                    progressBean.setSystem(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                //处理名称找不到的异常
                //如果找不到该应用的的名称，图标，那么就手动赋值一个
                //如果没有名称，那么就将当前的包名赋值给名称
                progressBean.setProgressName(runningAppProcessInfo.processName);

                //如果该进程没有图标那么就将当前应用的图标赋值上去
                progressBean.setIcon(context.getResources().getDrawable(R.mipmap.icon));

                //如果没有名称那么一定是一个系统进程
                progressBean.setSystem(true);
                e.printStackTrace();
            }
            progressBeenList.add(progressBean);
        }
        return progressBeenList;
    }

    /**
     * 根据包名杀死指定的进程
     * 注意需加权限
     <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"></uses-permission>
     * @param context
     * @param packageName
     */
    public static void killProcess(Context context,String packageName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //根据包名杀死指定的进程
        activityManager.killBackgroundProcesses(packageName);
    }

    /**
     * 杀死所有进程(除了本应用的进程以外)
     * @param context
     */
    public static void killAllProcess(Context context){
        //获取ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (int i = 0; i <runningAppProcesses.size() ; i++) {
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(i);

            //如果当前正在运行的进程是本进程，那么跳过本次循环
            if (runningAppProcessInfo.processName.equals(context.getPackageName())){
                continue;
            }
            //根据包名杀死所有进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            LogUtils.i(TAG,"杀死了包名为"+runningAppProcessInfo.processName+"的进程");
        }
    }


}
