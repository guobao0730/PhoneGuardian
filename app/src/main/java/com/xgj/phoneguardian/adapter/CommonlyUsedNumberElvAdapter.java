package com.xgj.phoneguardian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xgj.phoneguardian.R;
import com.xgj.phoneguardian.engine.Child;
import com.xgj.phoneguardian.engine.Group;

import java.util.List;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.adapter
 * @date： 2017/9/18 16:50
 * @brief: 常用号码页面中ExpandableListView所需的适配器
 */
public class CommonlyUsedNumberElvAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<Group> mGroupList;

    public CommonlyUsedNumberElvAdapter(Context context, List<Group> groupList) {
        mContext = context;
        mGroupList = groupList;
    }



    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder = null;
        if (convertView==null){
             groupViewHolder = new GroupViewHolder();
            convertView = View.inflate(mContext, R.layout.item_commonly_used_number_group, null);
            groupViewHolder.tv_index = (TextView) convertView.findViewById(R.id.commonlyUsedNumberElvAdapter_group_tv_index);
            groupViewHolder.tv_title = (TextView) convertView.findViewById(R.id.commonlyUsedNumberElvAdapter_group_tv_title);

            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        Group group = mGroupList.get(groupPosition);
        groupViewHolder.tv_title.setText(group.getName());

        //如果当前的父类中没有子类数据，那么就隐藏父条目中左侧的箭头
        if (getChildrenCount(groupPosition) <= 0) {
            groupViewHolder.tv_index.setBackgroundResource(R.mipmap.elv_right);

            groupViewHolder.tv_index.setVisibility(View.INVISIBLE);
        } else {
            groupViewHolder.tv_index.setVisibility(View.VISIBLE);
            //如果当前的扩张的状态，那么显示向下的箭头，否则显示向右的箭头
            if (isExpanded) {
                groupViewHolder.tv_index.setBackgroundResource(R.mipmap.elv_down);
            } else {
                groupViewHolder.tv_index.setBackgroundResource(R.mipmap.elv_right);
            }
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder childViewHolder = null;
        if (convertView==null){
             childViewHolder = new ChildViewHolder();
            convertView = View.inflate(mContext, R.layout.item_commonly_used_number_child,null);
            childViewHolder.ll_root = (LinearLayout) convertView.findViewById(R.id.commonlyUsedNumberElvAdapter_child_ll);
            childViewHolder.tv_name = (TextView) convertView.findViewById(R.id.commonlyUsedNumberElvAdapter_child_tv_name);
            childViewHolder.tv_number = (TextView) convertView.findViewById(R.id.commonlyUsedNumberElvAdapter_child_tv_number);
            convertView.setTag(childViewHolder);

        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final Child child = (Child) getChild(groupPosition, childPosition);
        childViewHolder.tv_name.setText(child.getName());
        childViewHolder.tv_number.setText(child.getNumber());

        /*childViewHolder.ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String number = child.getNumber();
                UiUtils.showToast(number+"号码被点击了");

            }
        });*/


        return convertView;
    }


    /**
     * 表示子节点是否可以被选中，可以被点击，如果需要点击子节点做某项操作，那么返回true,否则返回false
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }




    static class GroupViewHolder {
        TextView tv_index;
        TextView tv_title;
    }

    static class ChildViewHolder{
        LinearLayout ll_root;
        TextView tv_name;
        TextView tv_number;
    }
}
