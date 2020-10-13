package cn.itcast.im.imchat_qq50wh21.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.activity.ChatActivity;
import cn.itcast.im.imchat_qq50wh21.bean.Msg;

/**
 * Created by Administrator on 2016/7/17.
 */
public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<Msg> msgList;
    public ChatAdapter(Context context, List<Msg> msgList) {
        this.context=context;
        this.msgList=msgList;
    }

    @Override
    public int getCount() {
        return msgList==null?0:msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView==null){
            if (type==Msg.MSG_TYPE_SEND){
                convertView=View.inflate(context, R.layout.lv_chat_item_send,null);
            }else{
                convertView=View.inflate(context, R.layout.lv_chat_item_receive,null);
            }
        }
        ViewHolder holder=ViewHolder.getViewHolder(convertView);
        //获取数据设置到控件上
        Msg msg = msgList.get(position);
        holder.tv_chatTime.setText(msg.time);
        holder.tv_chatContent.setText(msg.content);
        return convertView;
    }

    private static  class ViewHolder{
        private TextView tv_chatTime;
        private TextView tv_chatContent;
        public static ViewHolder getViewHolder(View convertView) {
           ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder();
                holder.tv_chatTime= (TextView) convertView.findViewById(R.id.tv_chatTime);
                holder.tv_chatContent= (TextView) convertView.findViewById(R.id.tv_chatContent);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
