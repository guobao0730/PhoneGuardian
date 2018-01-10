package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.BlacklistBean;
import com.xgj.phoneguardian.db.BlacklistDao;
import com.xgj.phoneguardian.utils.Constant;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/8/9 9:29
 * @brief: 黑名单中ListView的适配器
 */
public class BlacklistAdapter extends BaseAdapter {

    private Context mContext;
    private List<BlacklistBean> mBlacklistBeanList;


    public BlacklistAdapter(Context context, List<BlacklistBean> blacklistBeanList) {
        mContext = context;
        mBlacklistBeanList = blacklistBeanList;
    }


    @Override
    public int getCount() {
        return mBlacklistBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlacklistBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView==null){
             viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_blacklistadapter, null);
            viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.blacklistAdapter_tv_phone);
            viewHolder.tv_model = (TextView) convertView.findViewById(R.id.blacklistAdapter_tv_model);
            viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.blacklistAdapter_iv_delete);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final BlacklistBean blacklistBean = mBlacklistBeanList.get(position);
        viewHolder.tv_phone.setText(blacklistBean.getPhoneNumber());
        String model = blacklistBean.getModel();
        // 1表示短信 2表示电话 3表示所有
        if (model.equals(Constant.INTERCEPT_MESSAGES)){
            viewHolder.tv_model.setText("拦截短信");
        }else if (model.equals(Constant.INTERCEPT_PHONE)){
            viewHolder.tv_model.setText("拦截电话");
        }else if (model.equals(Constant.INTERCEPT_ALL)){
            viewHolder.tv_model.setText("拦截所有");
        }

        //删除
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除数据库中对应的黑名单数据
                BlacklistDao.getInstance(mContext).delete(blacklistBean);
                //删除集合中的该条数据
                mBlacklistBeanList.remove(position);
                //通知适配器更新数据
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    //这里将ViewHolder定义为静态的static是为了避免去创建多个对象
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_model;
        ImageView iv_delete;
    }
}
