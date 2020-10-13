package cn.itcast.im.imchat_qq50wh21.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.jivesoftware.smack.RosterEntry;

import java.util.List;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;

/**
 * Created by Administrator on 2016/7/15.
 */
public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<RosterEntry> rosterEntryList;
    public ContactAdapter(Context mContext, List<RosterEntry> rosterEntryList) {
        this.mContext=mContext;
        this.rosterEntryList=rosterEntryList;
    }

    @Override
    public int getCount() {
        return rosterEntryList==null?0:rosterEntryList.size();
    }

    @Override
    public RosterEntry getItem(int position) {
        return rosterEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=View.inflate(mContext, R.layout.lv_contact_item,null);
        }
        ViewHolder holder=ViewHolder.getViewHolder(convertView);
        //获取数据设置到控件上
        RosterEntry rosterEntry = rosterEntryList.get(position);
        String nameOrJid = CommonUtils.priorityNameOrJid(rosterEntry);
        //转成拼音
        String currentFirstLetter = CommonUtils.hanZiToPinyin(nameOrJid).charAt(0)+"";
        //分组
        if (position==0){
            holder.tv_pinyin.setVisibility(View.VISIBLE);
            holder.tv_pinyin.setText(currentFirstLetter);
        }else{
            RosterEntry preRosterEntry = rosterEntryList.get(position - 1);
            String preNameOrJid = CommonUtils.priorityNameOrJid(preRosterEntry);
            //转成拼音
            String preFirstLetter = CommonUtils.hanZiToPinyin(preNameOrJid).charAt(0)+"";
            if (TextUtils.equals(preFirstLetter,currentFirstLetter)){
                //一样
                holder.tv_pinyin.setVisibility(View.GONE);
            }else {
                holder.tv_pinyin.setVisibility(View.VISIBLE);
                holder.tv_pinyin.setText(currentFirstLetter);
            }
        }
        holder.tv_name.setText(nameOrJid);
        return convertView;
    }

    private static class ViewHolder{
        private TextView tv_pinyin;
        private TextView tv_name;
        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder();
                holder.tv_pinyin= (TextView) convertView.findViewById(R.id.tv_pinyin);
                holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
