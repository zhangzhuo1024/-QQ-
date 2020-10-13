package cn.itcast.im.imchat_qq50wh21.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.activity.ChatActivity;
import cn.itcast.im.imchat_qq50wh21.adapter.ContactAdapter;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;
import cn.itcast.im.imchat_qq50wh21.view.QuickIndexBar;

/**
 * Created by Administrator on 2016/7/15.
 */
public class ContactFragment extends BaseFragment {
    @ViewInject(R.id.tv_showLetter)
    private TextView tv_showLetter;
    @ViewInject(R.id.quickIndexBar)
    private QuickIndexBar quickIndexBar;
    @ViewInject(R.id.listView)
    private ListView listView;
    private ConnectionManager connectionManager;
    private Roster roster;
    private List<RosterEntry> rosterEntryList=new ArrayList<RosterEntry>();
    private ContactAdapter adapter;

    @Override
    protected View getViewLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.contact_fragment,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //给QuickIndexBar设置字母改变的监听
        quickIndexBar.setOnLetterChangedListener(onLetterChangedListener);
        //获取ConnetionManager对象
        connectionManager = ConnectionManager.getInstance();
        //获取roster对象
        roster = connectionManager.getRoster();
        //获取联系人
        Collection<RosterEntry> rosterEntries = roster.getEntries();
        //一定要判断，如果rosterEntries为null，就会空指针
        if (rosterEntries!=null&&rosterEntries.size()>0){
            rosterEntryList.addAll(rosterEntries);
        }
        //给rosterEntryList集合里面的数据排序
        sort();
        //设置适配器
        adapter = new ContactAdapter(mContext, rosterEntryList);
        listView.setAdapter(adapter);
        //添加更新好友的监听
        roster.addRosterListener(rosterListener);
        //day03的内容--------------------------------------------------------------
        //给listview添加条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RosterEntry rosterEntry = adapter.getItem(position);
                //获取昵称和jid
                String jid = rosterEntry.getUser();
                String name = rosterEntry.getName();
                //跳转到聊天界面
                Intent intent=new Intent(mContext, ChatActivity.class);
                //携带数据
                Bundle bundle=new Bundle();
                bundle.putString("chat_jid",jid);
                bundle.putString("chat_name",name);
                intent.putExtra("to_chat",bundle);
                startActivity(intent);
            }
        });
    }
    //排序的方法
    private void sort() {
        Collections.sort(rosterEntryList, new Comparator<RosterEntry>() {
            @Override
            public int compare(RosterEntry lhs, RosterEntry rhs) {
                String lhsNameOrJid = CommonUtils.priorityNameOrJid(lhs);
                String rhsNameOrJid = CommonUtils.priorityNameOrJid(rhs);
                //转成拼音
                String lhsPinyin = CommonUtils.hanZiToPinyin(lhsNameOrJid);
                String rhsPinyin = CommonUtils.hanZiToPinyin(rhsNameOrJid);
                return lhsPinyin.compareTo(rhsPinyin);
            }
        });
    }

    private QuickIndexBar.OnLetterChangedListener onLetterChangedListener=new QuickIndexBar.OnLetterChangedListener() {
        @Override
        public void letterChanged(String letter) {
            //tv_showLetter显示
            tv_showLetter.setVisibility(View.VISIBLE);
            //设置文本
            tv_showLetter.setText(letter);
            CommonUtils.getHandler().postDelayed(showLetterTask,2000);
            //把listview条目选到该字母处
            for (int i = 0; i < rosterEntryList.size(); i++) {
                RosterEntry rosterEntry = rosterEntryList.get(i);
                String nameOrJid = CommonUtils.priorityNameOrJid(rosterEntry);
                //转拼音
                String firstLetter = CommonUtils.hanZiToPinyin(nameOrJid).charAt(0)+"";
                if (firstLetter.equals(letter)){
                    listView.setSelection(i);
                    //找到第一个就跳出循环
                    break;
                }
            }
        }
    };
    private Runnable showLetterTask=new Runnable() {
        @Override
        public void run() {
            tv_showLetter.setVisibility(View.INVISIBLE);
        }
    };
    private void updateEntries(){
        //重新获取好友列表
        Collection<RosterEntry> rosterEntries = roster.getEntries();
        //一定要判断，如果rosterEntries为null，就会空指针
        if (rosterEntries!=null&&rosterEntries.size()>0){
            //清除之前的数据
            rosterEntryList.clear();
            rosterEntryList.addAll(rosterEntries);
        }
        //给rosterEntryList集合里面的数据排序
        sort();
        //刷新适配器
        CommonUtils.runOnUIThrad(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }
    //添加更新好友的监听的方法,注意点：监听里面的所有回调方法都是在子线程里面执行
    private RosterListener rosterListener=new RosterListener() {
        //添加好友的时候被调用
        @Override
        public void entriesAdded(Collection<String> collection) {
           /* for (String jid : collection) {
                Log.i("test","entriesAdded==>"+jid);
            }*/
            updateEntries();
        }
        //添加好友的时候也会被调用，更新好友的时候调用
        @Override
        public void entriesUpdated(Collection<String> collection) {
           /* for (String jid : collection) {
                Log.i("test","entriesUpdated==>"+jid);
            }*/
            updateEntries();
        }
        //删除好友的时候调用
        @Override
        public void entriesDeleted(Collection<String> collection) {
           /* for (String jid : collection) {
                Log.i("test","entriesDeleted==>"+jid);
            }*/
            updateEntries();
        }
        //好友在线状态改变的时候调用
        @Override
        public void presenceChanged(Presence presence) {
//            Log.i("test","presenceChanged==>"+presence);
        }
    };
}
