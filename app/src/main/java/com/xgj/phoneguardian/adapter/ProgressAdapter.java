package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.ProgressBean;
import com.xgj.phoneguardian.utils.Constant;
import com.xgj.phoneguardian.utils.SpUtils;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/8/30 11:28
 * @brief: 进程管理 中 ListView的适配器
 */
public class ProgressAdapter extends BaseAdapter {


    private Context mContext;

    //系统进程
    private List<ProgressBean> mSystemProgress;
    //用户进程
    private List<ProgressBean> mUserProgress;


    /**
     *
     * @param context
     * @param systemProgress 系统进程集合
     * @param userProgress 用户进程集合
     */
    public ProgressAdapter(Context context, List<ProgressBean> systemProgress, List<ProgressBean> userProgress) {
        mContext = context;
        mSystemProgress = systemProgress;
        mUserProgress = userProgress;
    }

    /**
     * 获取条目类型的总个数(普通应用头像，名称，是否安装在SD卡)+常驻悬浮框  ，总共2种条目类型
     * @return
     */
    @Override
    public int getViewTypeCount() {
        //因为 super.getViewTypeCount()默认返回的就是1，那么只需要在这基础上+1即可
        return super.getViewTypeCount()+1;
    }

    @Override
    public int getItemViewType(int position) {
        // 判断语句中的0表示的是复用系统的，或者当前的位置为最后一个用户进程条目
        if (position==0||position==mUserProgress.size()+1){
            //返回0表示，当前是 常驻悬浮框 的状态
            return 0;
        }else {
            //否则就是 (普通应用头像，名称，是否安装在SD卡)的条目
            return 1;
        }
    }

    @Override
    public int getCount() {

        //为了实现隐藏获与显示系统进程，那么可以再此处以控制返回个数的方式来实现
        // 隐藏系统进程就是只显示用户进程+1个用户进程常驻悬浮框，而显示系统进程就是显示用户进程+系统进程+2个常驻悬浮框）
        if (SpUtils.getBoolean(Constant.IS_SYSTEM_PROCESS,false)){
            //总条目 = 系统进程+用户进程+2个常驻悬浮框条目（系统进程和用户进程）
            return mSystemProgress.size()+mUserProgress.size()+2;
        }else {
            return mUserProgress.size()+1;
        }

    }

    @Override
    public Object getItem(int position) {
        //如果当前是 用户进程常驻悬浮框条目 或者当前是 系统进程常驻悬浮框条目 那么返回空
        if (position ==0||position==mUserProgress.size()+1){
            return null;
        }else {
            //如果当前的位置在 系统进程常驻悬浮框条目 的范围内
            if (position<mUserProgress.size()+1){
                //那么返回当前位置的前一个用户进程对象
                return mUserProgress.get(position-1);
            }else {
                //那么返回系统进程对象
                // position-userApp.size()-2 (表示当前的条目-用户的总个数-2个常驻悬浮框)
                return mSystemProgress.get(position-mUserProgress.size()-2);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int itemViewType = getItemViewType(position);

        if (itemViewType==0){
            //如果当前是 常驻悬浮框 的状态
            TitleViewHolder titleViewHolder = null;
            if (convertView==null){
                titleViewHolder = new TitleViewHolder();
                convertView = View.inflate(mContext, R.layout.item_appinfo_adapter_title,null);
                titleViewHolder.tv_title = (TextView) convertView.findViewById(R.id.appinfoAdapter_tv_title);
                convertView.setTag(titleViewHolder);
            }else {
                titleViewHolder = (TitleViewHolder) convertView.getTag();
            }

            //如果当前位置为第一个条目，那么设置为用户进程的总个数
            if (position==0){
                titleViewHolder.tv_title.setText("用户进程("+mUserProgress.size()+")");
            }else {
                titleViewHolder.tv_title.setText("系统进程("+mSystemProgress.size()+")");
            }

            return convertView;

        }else {
            //那么就是 (普通应用头像，名称，是否安装在SD卡)条目 的状态
            ViewHolder viewHolder = null;
            if (convertView==null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_progressinfo_adapter,null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.progressinfoAdapter_iv_icon);
                viewHolder.tv_progressname = (TextView) convertView.findViewById(R.id.progressinfoAdapter_tv_progressname);
                viewHolder.tv_progressSize = (TextView) convertView.findViewById(R.id.progressinfoAdapter_tv_progressSize);
                viewHolder.iv_isChecked = (ImageView) convertView.findViewById(R.id.progressinfoAdapter_iv_isChecked);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProgressBean progressBean = (ProgressBean) getItem(position);
            viewHolder.iv_icon.setImageDrawable(progressBean.getIcon());
            viewHolder.tv_progressname.setText(progressBean.getProgressName());
            //格式化当前的内存占用数值
            String s = Formatter.formatFileSize(mContext, progressBean.getProcessSize());
            viewHolder.tv_progressSize.setText("内存以占用："+s);

            //如果当前显示的是本应用进程，那么隐藏当前的viewHolder.iv_isChecked 视图
            if (progressBean.getPackageName().equals(mContext.getPackageName())){
                viewHolder.iv_isChecked.setVisibility(View.GONE);
            }else {
                viewHolder.iv_isChecked.setVisibility(View.VISIBLE);
            }

            //设置是否选中
            viewHolder.iv_isChecked.setEnabled(progressBean.isCheck());

            return convertView;
        }


    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_progressname;
        TextView tv_progressSize;
        ImageView iv_isChecked;
    }

    static class TitleViewHolder{
        TextView tv_title;
    }
}
