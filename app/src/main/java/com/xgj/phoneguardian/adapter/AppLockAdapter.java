package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.AppInfoBean;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/12/25/025 18:34
 * @brief: 程序锁ListView适配器
 */

public class AppLockAdapter extends BaseAdapter {


    private Context mContext;

    private List<AppInfoBean> mDataList;

    //是否是已加锁的应用
    private boolean isLockApp;

    public AppLockAdapter(Context context, List<AppInfoBean> dataList, boolean isLockApp) {
        mContext = context;
        mDataList = dataList;
        this.isLockApp = isLockApp;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView==null){
             viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_applock, null);
            viewHolder.iv_applock = (ImageView) convertView.findViewById(R.id.item_applock_iv_applock);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.item_applock_iv_icon);
            viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.item_applock_tv_appname);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppInfoBean appInfoBean = mDataList.get(position);
        viewHolder.iv_icon.setImageDrawable(appInfoBean.getIcon());
        viewHolder.tv_appname.setText(appInfoBean.getAppName());

        viewHolder.iv_applock.setEnabled(isLockApp);

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_appname;
        ImageView iv_applock;
    }




}
