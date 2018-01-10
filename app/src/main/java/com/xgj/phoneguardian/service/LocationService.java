package com.xgj.phoneguardian.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

import java.util.List;

/**
 * @author Administrator
 * @project： PhoneGuardian
 * @package： com.xgj.service
 * @date：2016/8/15 0015 21:42
 * @brief: 获取当前的坐标点的服务
 *
 * <!--系统自带的定位所需要的权限 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
 *
 */
public class LocationService extends Service {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //当服务创建的时候获取位置信息
        mLocationManager = (LocationManager) UiUtils.getContext().getSystemService(Context.LOCATION_SERVICE);

        //定位所需的监听器
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //当位置发生改变时获取当前的位置信息
                //经度
                double longitude = location.getLongitude();
                //维度
                double latitude = location.getLatitude();

                //移动的速度
                float speed = location.getSpeed();
                //高度
                double altitude = location.getAltitude();


                //组拼要发送给安全号码的短信内容
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("您遗失的手机位置为："+"\n");
                stringBuffer.append("精度："+longitude+"\n");
                stringBuffer.append("维度："+latitude+"\n");
                stringBuffer.append("移动的速度："+speed+"\n");
                stringBuffer.append("高度："+altitude+"\n");

                //短信内容
                String message = stringBuffer.toString();

                //安全号码
                String securityNumber = SpUtils.getString(Constant.SECURITY_NUMBER, "");

                //发送短信给安全号码
                PhoneSystemUtils.sendSMS(securityNumber,message);




                //当定位成功并发送完短信后关闭自己（相当于调用自己的onDestroy()）
                //相当于只定位一次就关闭服务停止定位
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //获取支持的所有的定位方式
        List<String> allProviders = mLocationManager.getAllProviders();

        for (String providers:allProviders){
            Log.i("LocationService","当前设备支持的定位方式："+providers);
        }


        //获取最佳的定位方式的条件
        Criteria criteria = new Criteria();
        //设置条件为需要付费的
        criteria.setCostAllowed(true);
        //设置条件为精确度最高的
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //获取最佳的定位方式
        String bestProvider = mLocationManager.getBestProvider(criteria, true);
        Log.i("LocationService","当前设备支持最佳的定位方式为："+bestProvider);

        //请求位置更新
        mLocationManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
        //定位的方式（GPS/WIFI/3G4G）
        //相隔多少时间定位一次
        //相隔多少距离定位一次
        //位置监听器


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //当服务销毁的时候停止定位
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager = null;

    }
}
