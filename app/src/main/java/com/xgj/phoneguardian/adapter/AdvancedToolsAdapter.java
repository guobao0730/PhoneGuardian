package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.bean.AdvancedToolsBean;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/7/29 15:23
 * @brief: 高级工具中的GV中的适配器
 */
public class AdvancedToolsAdapter extends BaseAdapter{


    private Context mContext;
    private List<AdvancedToolsBean> mAdvancedToolsBeanList;

    public AdvancedToolsAdapter(Context context, List<AdvancedToolsBean> advancedToolsBeanList) {
        mContext = context;
        mAdvancedToolsBeanList = advancedToolsBeanList;
    }

    @Override
    public int getCount() {
        return mAdvancedToolsBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAdvancedToolsBeanList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_advancedtoolsadapter, null);
            viewHolder.iv_img= (ImageView) convertView.findViewById(R.id.item_iv_img);
            viewHolder.tv_msg = (TextView) convertView.findViewById(R.id.item_tv_msg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AdvancedToolsBean advancedToolsBean = mAdvancedToolsBeanList.get(position);
        viewHolder.iv_img.setImageResource(advancedToolsBean.getImg());
        viewHolder.tv_msg.setText(advancedToolsBean.getStr());

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_img;
        TextView tv_msg;
    }


}
