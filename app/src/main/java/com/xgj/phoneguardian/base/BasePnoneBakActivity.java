package com.xgj.phoneguardian.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xgj.phoneguardian.R;

import com.xgj.phoneguardian.utils.UiUtils;

/**
 * @author pc
 * @project： PhoneGuardian
 * @package：
 * @date：2016/8/9 11:11
 * @brief: 对4个手机防盗的引导页面进行抽取共同的 显示视图，页面之间的跳转，跳转时的动画
 */
public abstract class BasePnoneBakActivity extends Activity {

    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initGestureDetector();
        initData();
        initEvent();



    }




    /**
     * 子类必须实现的的抽象方法
     */
    public abstract void initView();


    /**
     * 为了配合手势识别器而必须重写此方法并绑定
     * 注意：在使用手势识别器时必须和OnTuch事件绑定
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //和手势识别器进行绑定
        mGestureDetector.onTouchEvent(event);


        return super.onTouchEvent(event);

    }




    /**
     * 为了满足实现左滑下一步，右滑上一步的效果就需要使用到手势识别器类
     * 注意：在使用手势识别器时必须和OnTuch事件绑定才会生效
     * 初始化手势识别器
     * 滑动时需判断滑动的速度（滑动时按下的点到滑动时松开的点得移动速度），滑动的间距（滑动时按下的点到滑动时松开的点的间距）
     *
     */
    protected void initGestureDetector(){

        //实例化手势识别器类 ,传入上下文，手势监听器
        mGestureDetector = new GestureDetector(UiUtils.getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            /**
             * 滑动时需判断滑动的速度（滑动时按下的点到滑动时松开的点得移动速度），滑动的间距（滑动时按下的点到滑动时松开的点的间距）
             * @param e1  表示滑动时按下的事件点
             * @param e2  表示滑动时松开的点
             * @param velocityX   表示滑动时X轴移动的速度
             * @param velocityY  表示滑动时Y轴移动的速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //先计算出滑动时的间距
                float difference = e2.getX() - e1.getX();//可能为负数(从右往左滑动)或者正数（从左往右滑动）
                float spacing = Math.abs(difference);//计算出绝对值


                //如果X轴滑动时的速度大于 300像素每秒  并且间距大于100像素  那么才能滑动触发事件
                if (velocityX>200&&spacing>100){

                        Log.i("BasePnoneBakActivity","差值为："+String.valueOf(difference));
                        if (difference<0){//为负数(从右往左滑动)(下一步)
                            next(null);
                        }else if (difference>0){//正数（从左往右滑动）(上一步)
                            back(null);
                        }

                }

                //修改返回true
                return true;
            }
        });

    }


    /**
     * 初始化事件，以为子类 手机防盗引导页面1中没有需要处理的事件，而引导2页面才有那么就将该方法定义为非抽象方法，子类可实现也可不实现
     * 增强了灵活性，拓展性
     */
    protected void initEvent(){


    }

    /**
     * 子类可实现也可不实现的初始化数据的方法
     */
    protected  void initData(){

    }

    /**
     * 对4个引导页面的 上一步 进行向上抽取
     * @param v
     */
    public void back(View v){
        //跳转页面
        backActivity();

        //开始动画
        backAnimation();
    }

    /**
     * 点击上一步要完成的动画
     */
    protected  void backAnimation(){

        //传入  进入的动画，出去的动画
        overridePendingTransition(R.anim.back_in, R.anim.back_out);

    }


    /**
     * 子类必须重写此方法并在其方法中调的startActivity方法
     */
    protected abstract void backActivity();


    /**
     * 开始动画
     * 子类需重写的方法
     * @param type
     */
    protected  void startActivity(Class type){
        Intent intent = new Intent(UiUtils.getContext(), type);
        startActivity(intent);
        finish();
    }







    /**
     * 对4个引导页面的 下一步 进行向上抽取
     */
    public void next(View v){

        //跳转页面
        nextActivity();

        //开始动画
        nextAnimation();

    }

    /**
     * 点击下一步要完成的动画
     */
    protected  void nextAnimation(){

        //传入  进入的动画，出去的动画
        overridePendingTransition(R.anim.next_in,R.anim.next_out);

    };


    /**
     * 子类必须重写此方法并在其方法中调的startActivity方法
     */
    protected abstract void nextActivity();



}
