package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/8/21 16:54
 * @brief: 应用信息适配器
 */
public class AppInfoAdapter  extends BaseAdapter{

    private Context mContext;

    private List<AppInfoBean> userApp;
    private List<AppInfoBean> systemApp;

    /**
     * @param context
     * @param systemApp 系统应用集
     * @param userApp 用户应用集
     */
    public AppInfoAdapter(Context context, ArrayList<AppInfoBean> systemApp, List<AppInfoBean> userApp) {
        mContext = context;
        this.userApp = userApp;
        this.systemApp = systemApp;
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
        // 判断语句中的0表示的是复用系统的，或者当前的位置为最后一个用户应用条目
        if (position==0||position==userApp.size()+1){
            //返回0表示，当前是 常驻悬浮框 的状态
            return 0;
        }else {
            //否则就是 (普通应用头像，名称，是否安装在SD卡)的条目
            return 1;
        }

    }

    @Override
    public int getCount() {
        //条目总个数 = 系统应用总个数+用户应用总个数+2
        // 2(表示系统应用和用户应用这两个常驻悬浮框条目)
        return systemApp.size()+userApp.size()+2;
    }

    @Override
    public Object getItem(int position) {

        //如果当前是 用户应用常驻悬浮框条目 或者当前是 系统应用常驻悬浮框条目 那么返回空
        if (position ==0||position==userApp.size()+1){
            return null;
        }else {
            //如果当前的位置在 系统应用常驻悬浮框条目 的范围内
            if (position<userApp.size()+1){
                //那么返回当前位置的前一个用户应用对象
                return userApp.get(position-1);
            }else {
                //那么返回系统应用对象
                // position-userApp.size()-2 (表示当前的条目-用户的总个数-2个常驻悬浮框)
                return systemApp.get(position-userApp.size()-2);
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

            //如果当前位置为第一个条目，那么设置为用户应用的总个数
            if (position==0){
                titleViewHolder.tv_title.setText("用户应用("+userApp.size()+")");
            }else {
                titleViewHolder.tv_title.setText("系统应用("+systemApp.size()+")");
            }

            return convertView;

        }else {
            //那么就是 (普通应用头像，名称，是否安装在SD卡)条目 的状态
            ViewHolder viewHolder = null;
            if (convertView==null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_appinfo_adapter,null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.appinfoAdapter_iv_icon);
                viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.appinfoAdapter_tv_appname);
                viewHolder.tv_apptype = (TextView) convertView.findViewById(R.id.appinfoAdapter_tv_apptype);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            AppInfoBean appInfoBean = (AppInfoBean) getItem(position);
            viewHolder.iv_icon.setImageDrawable(appInfoBean.getIcon());
            viewHolder.tv_appname.setText(appInfoBean.getAppName());
            if (appInfoBean.isSdcard()){
                viewHolder.tv_apptype.setText("SD卡应用");
            }else {
                viewHolder.tv_apptype.setText("手机应用");
            }

            return convertView;
        }


    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_appname;
        TextView tv_apptype;
    }

    static class TitleViewHolder{
        TextView tv_title;
    }
}
