package com.xgj.phoneguardian.utils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/8 10:27
 * @brief: 常量接口，专用于存储常量
 */
public interface Constant {

    //存储到SP中的密码的.XML文件名
    public static String SP_NAME = "userinfo";

    //存储到SP中的KEY
    String PASSWORD = "password";


    //用于判断是否是第一次进入手机防盗时利用SP存储的KEY
    String IS_FIRST = "isFirst";


    //将SIM信息存储到SP中的键
    String SIM ="sim";

    //讲安全号码 存储到SP中的键
    String SECURITY_NUMBER = "securityNumber";

    //自动更新
    String AUTO_UPDATE = "auto_update";

    //归属地
    String HOME_LOCATION ="home_Location";

    //归属地风格
    String ATTRIBUTION_STYLE = "attribution_style";

    //归属地位置的X轴（也就是归属地图片距离左边的距离）
    String ATTRIBUTION_LOCATION_X = "attribution_location_x";

    //归属地位置的y轴
    String ATTRIBUTION_LOCATION_Y = "attribution_location_y";

    //拦截短信的模式
    String INTERCEPT_MESSAGES = "1";

    //拦截电话的模式
    String INTERCEPT_PHONE = "2";

    //拦截所有的模式
    String INTERCEPT_ALL = "3";

    //是否显示系统进程
    String IS_SYSTEM_PROCESS = "is_system_process";

    //快捷方式的Key
    String SHORT_CUT = "short_cut";

    //更新部件
    String UPDATE_WIDGET = "update_widget";
}
