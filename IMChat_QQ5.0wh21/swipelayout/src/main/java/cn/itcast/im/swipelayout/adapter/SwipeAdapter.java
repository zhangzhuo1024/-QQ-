package cn.itcast.im.swipelayout.adapter;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.swipelayout.R;
import cn.itcast.im.swipelayout.bean.SwipeData;
import cn.itcast.im.swipelayout.listener.OnShowGooViewListener;
import cn.itcast.im.swipelayout.view.GooView;
import cn.itcast.im.swipelayout.view.SwipeLayout;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SwipeAdapter<T extends SwipeData> extends BaseAdapter {
    private Context context;
    private List<T> swipeDataList;
    private Set<SwipeLayout> swipeLayouts=new HashSet<SwipeLayout>();
    private int openSwipeLayoutCount;
    private OnShowGooViewListener onShowGooViewListener;

    public SwipeAdapter(Context context, List<T> swipeDataList) {
        this.context = context;
        this.swipeDataList = swipeDataList;
        onShowGooViewListener = new OnShowGooViewListener(context){
            @Override
            public void disappear(int position) {
                super.disappear(position);
                if (onSwipeAdapterCallBack!=null){
                    onSwipeAdapterCallBack.updateUnReadMsgCount(SwipeAdapter.this.swipeDataList.get(position));
                }
            }
        };
    }
    //定义接口
    public interface OnSwipeAdapterCallBack{
        public void onItemClick(SwipeData swipeData);
        public void onDeleteItem(SwipeData swipeData);
        public void updateUnReadMsgCount(SwipeData swipeData);
    }
    private OnSwipeAdapterCallBack onSwipeAdapterCallBack;
    public void setOnSwipeAdapterCallBack(OnSwipeAdapterCallBack onSwipeAdapterCallBack) {
        this.onSwipeAdapterCallBack = onSwipeAdapterCallBack;
    }

    @Override
    public int getCount() {
        return swipeDataList==null?0:swipeDataList.size();
    }

    @Override
    public T getItem(int position) {
        return swipeDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=View.inflate(context, R.layout.lv_swipelayout_item,null);
        }
        ViewHolder holder=ViewHolder.getViewHolder(convertView);
        //一上来就关闭条目   新加载的条目默认关闭
        holder.swipeLayout.close(false);
        //获取数据显示到控件上
        SwipeData swipeData = swipeDataList.get(position);
        //优先显示昵称
        String nameOrJid = CommonUtils.priorityNameOrJid(swipeData.getName(), swipeData.getJid());
        holder.tv_swipe_name.setText(nameOrJid);
        holder.tv_swipe_content.setText(swipeData.getContent());
        //判断未读消息是否是0
        if (swipeData.getMsgCount()==0){
            holder.tv_swipe_msgCount.setVisibility(View.INVISIBLE);
        }else{
            holder.tv_swipe_msgCount.setVisibility(View.VISIBLE);
            holder.tv_swipe_msgCount.setText(swipeData.getMsgCount() + "");//注意：int类型会被当成id找资源
            //给小红点设置触摸事件
            holder.tv_swipe_msgCount.setTag(position);
            holder.tv_swipe_msgCount.setOnTouchListener(onShowGooViewListener);
        }
        //给swipeLayout设置打开和关闭的监听
        holder.swipeLayout.setOnSwipeStateChangedListener(onSwipeStateChangedListener);
        //给适配器的每个条目添加点击事件
        holder.rl_main.setTag(position);
        holder.rl_main.setOnClickListener(onItemClickListener);
        //点击“删除”，就删除条目
        holder.tv_swipe_delete.setTag(position);
        holder.tv_swipe_delete.setOnClickListener(onDeleteItemListener);
        return convertView;
    }
    //点击“删除”，就删除条目  :1、通知界面(界面要删除数据库)  2、删除集合里面的数据，刷新适配器
    private View.OnClickListener onDeleteItemListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position= (int) v.getTag();
            //注意：通知界面(要先执行)
            if (onSwipeAdapterCallBack!=null){
                onSwipeAdapterCallBack.onDeleteItem(swipeDataList.get(position));
            }
            //2、删除集合里面的数据，刷新适配器
            swipeDataList.remove(position);
            notifyDataSetChanged();
        }
    };
    //点击条目：当集成到主项目里面，要跳转到聊天界面，也就是说，要通知界面跳转
    private View.OnClickListener onItemClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position= (int) v.getTag();
            if (onSwipeAdapterCallBack!=null){
                onSwipeAdapterCallBack.onItemClick(swipeDataList.get(position));
            }
        }
    };
    private SwipeLayout.OnSwipeStateChangedListener onSwipeStateChangedListener=new SwipeLayout.OnSwipeStateChangedListener() {
        @Override
        public void onOpen(SwipeLayout swipeLayout) {
            for (SwipeLayout sl : swipeLayouts) {
                sl.close(true);
            }
            swipeLayouts.add(swipeLayout);
        }

        @Override
        public void onClose(SwipeLayout swipeLayout) {
            swipeLayouts.remove(swipeLayout);
        }
    };
    //获取已经打开的条目数量
    public int getOpenSwipeLayoutCount() {
        return swipeLayouts.size();
    }
    //关闭所有条目
    public void closeAllSwipeLayout(boolean isSmooth) {
        for (SwipeLayout sl : swipeLayouts) {
            sl.close(isSmooth);
        }
    }

    private static class ViewHolder{
        private SwipeLayout swipeLayout;
        private TextView tv_swipe_delete;
        private TextView tv_swipe_name;
        private TextView tv_swipe_content;
        private TextView tv_swipe_msgCount;
        private RelativeLayout rl_main;
        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder();
                holder.swipeLayout= (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
                holder.tv_swipe_delete= (TextView) convertView.findViewById(R.id.tv_swipe_delete);
                holder.tv_swipe_name= (TextView) convertView.findViewById(R.id.tv_swipe_name);
                holder.tv_swipe_content= (TextView) convertView.findViewById(R.id.tv_swipe_content);
                holder.tv_swipe_msgCount= (TextView) convertView.findViewById(R.id.tv_swipe_msgCount);
                holder.rl_main= (RelativeLayout) convertView.findViewById(R.id.rl_main);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
