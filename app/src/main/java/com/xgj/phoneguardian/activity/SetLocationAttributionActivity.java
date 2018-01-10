package com.xgj.phoneguardian.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.PhoneSystemUtils;
import com.xgj.phoneguardian.utils.SpUtils;
import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.activity
 * @date： 2017/8/3 9:15
 * @brief: 设置归属地提示框位置
 */
public class SetLocationAttributionActivity extends Activity {

    private static final String TAG = "SetLocationAttributionActivity";
    private static final long SPACING_INTERVAL = 500;
    private Button mSetLocationAttributionActivity_bt_top;
    private ImageView mSetLocationAttributionActivity_iv;
    private Button mSetLocationAttributionActivity_bt_bottom;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    private int mHeight;

    //注：当前数组的长度就是连续点击的次数
    private long[] clicks = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_location_attribution);
        initView();
        initData();
        initEvent();
    }




    private void initView() {
        mSetLocationAttributionActivity_bt_top = (Button) findViewById(R.id.setLocationAttributionActivity_bt_top);
        mSetLocationAttributionActivity_iv = (ImageView) findViewById(R.id.setLocationAttributionActivity_iv);
        mSetLocationAttributionActivity_bt_bottom = (Button) findViewById(R.id.setLocationAttributionActivity_bt_bottom);
    }
    private void initData() {

        //获取上次设置的归属地图片的左上角位置
        String attribution_location_x = SpUtils.getString(Constant.ATTRIBUTION_LOCATION_X, "0");
        String attribution_location_y = SpUtils.getString(Constant.ATTRIBUTION_LOCATION_Y, "0");

        //设置为上一次设置的归属地位置
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = Integer.parseInt(attribution_location_x);
        layoutParams.topMargin =  Integer.parseInt(attribution_location_y);
        mSetLocationAttributionActivity_iv.setLayoutParams(layoutParams);

        //获取手机的宽度
        mScreenWidth = PhoneSystemUtils.getScreenWidth(UiUtils.getContext());
        mScreenHeight = PhoneSystemUtils.getScreenHeight(UiUtils.getContext());
        //获取状态栏的高度
        mStatusBarHeight = PhoneSystemUtils.getStatusBarHeight(UiUtils.getContext());

        //计算出除了状态栏的屏幕高度
        mHeight = mScreenHeight - mStatusBarHeight;

        //为了避免下次进入该页面时，当前的归属地位置正好处于屏幕中心点以下或以上，而当前并没有显示或者隐藏对应的提示的BUG
        if(Integer.parseInt(attribution_location_y)>mScreenHeight/2){
            mSetLocationAttributionActivity_bt_bottom.setVisibility(View.INVISIBLE);
            mSetLocationAttributionActivity_bt_top.setVisibility(View.VISIBLE);
        }else{
            mSetLocationAttributionActivity_bt_bottom.setVisibility(View.VISIBLE);
            mSetLocationAttributionActivity_bt_top.setVisibility(View.INVISIBLE);
        }

    }

    private void initEvent() {

        //给当前要移动的图片制作一个触摸事件，以实现移动图片的效果
        mSetLocationAttributionActivity_iv.setOnTouchListener(new View.OnTouchListener() {
            private int mStartY;
            private int mStartX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN://按下
                        //当手指按下的时候获取当前按下的X轴到原点（屏幕左上角）的X轴距离，
                        // 注意：当前的左上角位置不包括状态栏
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        //当移动图片的时候获取移动时的X轴到原点（屏幕左上角）的X轴距离
                        // 注意：当前的左上角位置不包括状态栏

                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();


                        //同时计算出移动的距离差
                        int rangeX =moveX-mStartX;
                        int rangeY =moveY- mStartY;


                        //获取移动后的图片位置（左上右下）
                        //mSetLocationAttributionActivity_iv.getLeft() 表示获取图片左边距离原点（屏幕左上角）的X轴的距离
                        // 注意：当前的左上角位置不包括状态栏
                        //原图片距离左边的距离+移动的距离差 = 现在图片距离左边的距离
                        int left = mSetLocationAttributionActivity_iv.getLeft() + rangeX;
                        int top = mSetLocationAttributionActivity_iv.getTop() + rangeY;
                        int right = mSetLocationAttributionActivity_iv.getRight() + rangeX;
                        int bottom = mSetLocationAttributionActivity_iv.getBottom() + rangeY;

                        //限制图片移动的范围在手机屏幕以内
                        if (left<0||top<0||right> mScreenWidth||bottom>mHeight){
                            return true;
                        }

                        //实现当双击居中的图片拖动到屏幕高度一半以外的位置时显示或者隐藏顶部或底部的提示按钮
                        if (top>mScreenHeight/2){
                            mSetLocationAttributionActivity_bt_top.setVisibility(View.VISIBLE);
                            mSetLocationAttributionActivity_bt_bottom.setVisibility(View.GONE);
                        }else {
                            mSetLocationAttributionActivity_bt_top.setVisibility(View.GONE);
                            mSetLocationAttributionActivity_bt_bottom.setVisibility(View.VISIBLE);
                        }

                        //重新计算并设置现在图片所在的位置
                        mSetLocationAttributionActivity_iv.layout(left,top,right,bottom);


                        //最后重置开始的点
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://松开
                        //在手指松开的时候记录当前图片的左上角的位置，也就是该图片距离原点（屏幕左上角）的X轴和Y轴的距离
                        //记录是为了下次打开归属地位置设置的时候显示的是上一次设置的位置，并且当来电时显示的是你设置的位置
                        SpUtils.putString(Constant.ATTRIBUTION_LOCATION_X,mSetLocationAttributionActivity_iv.getLeft()+"");
                        SpUtils.putString(Constant.ATTRIBUTION_LOCATION_Y,mSetLocationAttributionActivity_iv.getTop()+"");

                        break;
                }


                //注意：返回true才会响应该触摸事件
                //注：如果想实现给一个控件设置触摸事件(setOnTouchListener)，又设置点击事件，
                // 那么需要将触摸事件(setOnTouchListener) 中函数的返回值设置为false,否则无法响应该控件的点击事件
                return false;
            }
        });


        //注：如果想实现给一个控件设置触摸事件(setOnTouchListener)，又设置点击事件，
        // 那么需要将触摸事件(setOnTouchListener) 中函数的返回值设置为false,否则无法响应该控件的点击事件
        mSetLocationAttributionActivity_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.arraycopy(clicks,1,clicks,0,clicks.length-1);
                clicks[clicks.length-1] = SystemClock.uptimeMillis();
                //如果最后一次点击的时间-第一次点击的时间在500毫秒的范围内，那么就表示是双击事件
                if (clicks[clicks.length-1]-clicks[0]<SPACING_INTERVAL){
                    UiUtils.showToast("双击时间测试成功!");
                    //计算出图片在手机正中间时，当时的图片距离左上右下的距离
                    //图片左边的距离=手机宽度的一半-图片空间的一半
                    int left = mScreenWidth / 2 - mSetLocationAttributionActivity_iv.getWidth()/2;
                    int top = mScreenHeight / 2 - mSetLocationAttributionActivity_iv.getHeight() / 2;
                    int right = mScreenWidth / 2 + mSetLocationAttributionActivity_iv.getWidth() / 2;
                    int bottom = mScreenHeight / 2 + mSetLocationAttributionActivity_iv.getHeight() / 2;

                    //将当前的图片控件设置在手机的正中间位置
                    mSetLocationAttributionActivity_iv.layout(left,top,right,bottom);

                    //记录当前视图的的左上角位置
                    SpUtils.putString(Constant.ATTRIBUTION_LOCATION_X,mSetLocationAttributionActivity_iv.getLeft()+"");
                    SpUtils.putString(Constant.ATTRIBUTION_LOCATION_Y,mSetLocationAttributionActivity_iv.getTop()+"");
                }
            }
        });
    }
}
