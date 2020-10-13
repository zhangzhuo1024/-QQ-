package cn.itcast.im.imchat_qq50wh21.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.bean.Msg;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SessionAdapter extends BaseAdapter {
    private Context mContext;
    private List<Msg> msgList;
    public SessionAdapter(Context mContext, List<Msg> msgList) {
        this.mContext=mContext;
        this.msgList=msgList;
    }

    @Override
    public int getCount() {
        return msgList==null?0:msgList.size();
    }

    @Override
    public Msg getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=View.inflate(mContext, R.layout.lv_session_item,null);
        }
        ViewHolder holder=ViewHolder.getViewHolder(convertView);
        //获取数据
        Msg msg = msgList.get(position);
        //优先显示昵称
        String nameOrJid = CommonUtils.priorityNameOrJid(msg.name, msg.jid);
        holder.tv_name.setText(nameOrJid);
        holder.tv_content.setText(msg.content);
        return convertView;
    }
    private static class ViewHolder{
        private TextView tv_name;
        private TextView tv_content;
        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder();
                holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_content= (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
