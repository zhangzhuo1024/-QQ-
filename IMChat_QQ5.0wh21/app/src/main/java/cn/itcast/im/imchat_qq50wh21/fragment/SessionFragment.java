package cn.itcast.im.imchat_qq50wh21.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.activity.ChatActivity;
import cn.itcast.im.imchat_qq50wh21.adapter.SessionAdapter;
import cn.itcast.im.imchat_qq50wh21.bean.Msg;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;
import cn.itcast.im.imchat_qq50wh21.utils.MyDbUtils;
import cn.itcast.im.swipelayout.adapter.SwipeAdapter;
import cn.itcast.im.swipelayout.bean.SwipeData;
import cn.itcast.im.swipelayout.view.SwipeLayout;

/**
 * Created by Administrator on 2016/7/15.
 */
public class SessionFragment extends BaseFragment implements ConnectionManager.MsgObserver {
    @ViewInject(R.id.listView)
    private ListView listView;
    private SwipeAdapter adapter;
    private List<Msg> msgList=new ArrayList<Msg>();
    private ConnectionManager connectionManager;

    @Override
    protected View getViewLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.session_fragment,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionManager = ConnectionManager.getInstance();
        connectionManager.addMsgObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("test", "SessionFragment===>onResume");
        setAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("test","onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("test", "onStop");
    }
    private boolean menuIsOpen=false;//如果菜单是打开状态，就不允许刷新适配器重绘界面
    @Override
    public void setMenuIsOpen(boolean menuIsOpen) {
        this.menuIsOpen = menuIsOpen;
        setAdapter();
    }

    private void setAdapter() {
       if (!menuIsOpen){
           //从数据库中获取聊天记录
           List<Msg> sessionRecord = MyDbUtils.getSessionRecord();
           if (sessionRecord!=null&&sessionRecord.size()>0){
               msgList.clear();//移除之前的数据
               msgList.addAll(sessionRecord);
           }
           //给listview设置适配器
           if (adapter==null){
               adapter = new SwipeAdapter(mContext, msgList);
               listView.setAdapter(adapter);
               //给适配器设置监听事件
               adapter.setOnSwipeAdapterCallBack(onSwipeAdapterCallBack);
            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Msg msg = adapter.getItem(position);
                    //跳转到聊天界面
                    Intent intent=new Intent(mContext, ChatActivity.class);
                    //携带数据
                    Bundle bundle=new Bundle();
                    bundle.putString("chat_jid",msg.jid);
                    bundle.putString("chat_name",msg.name);
                    intent.putExtra("to_chat",bundle);
                    startActivity(intent);
                }
            });*/
           }else{
               adapter.notifyDataSetChanged();
           }
       }
    }
    private SwipeAdapter.OnSwipeAdapterCallBack onSwipeAdapterCallBack=new SwipeAdapter.OnSwipeAdapterCallBack() {
        @Override
        public void onItemClick(SwipeData swipeData) {
            //跳转到聊天界面
            Intent intent=new Intent(mContext, ChatActivity.class);
            //携带数据
            Bundle bundle=new Bundle();
            bundle.putString("chat_jid",swipeData.getJid());
            bundle.putString("chat_name",swipeData.getName());
            intent.putExtra("to_chat",bundle);
            startActivity(intent);
        }

        @Override
        public void onDeleteItem(SwipeData swipeData) {
            //真正的删除条目就要删除数据库里面的数据
            MyDbUtils.deleteSessionRecordByJid(swipeData.getJid());
        }
        @Override
        public void updateUnReadMsgCount(SwipeData swipeData) {
            MyDbUtils.clearUnReadMsgCount(swipeData.getJid());
        }
    };
    //获取SwipeAdapter打开的条目数量的方法
    @Override
    public int getOpenSwipeLayoutCount(){
       return adapter.getOpenSwipeLayoutCount();
    }
    //关闭所有侧拉删除的条目
    @Override
    public void closeAllSwipeLayout() {
       adapter.closeAllSwipeLayout(false);
    }

    @Override
    public void notity(Msg msg) {
        CommonUtils.runOnUIThrad(new Runnable() {
            @Override
            public void run() {
                setAdapter();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeMsgObserver(this);
    }
}
