package cn.itcast.im.imchat_qq50wh21.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.adapter.ChatAdapter;
import cn.itcast.im.imchat_qq50wh21.bean.Msg;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;
import cn.itcast.im.imchat_qq50wh21.utils.MyDbUtils;

public class ChatActivity extends Activity implements ConnectionManager.MsgObserver {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.et_input)
    private EditText et_input;
    private Chat chat;
    private List<Msg> msgList=new ArrayList<Msg>();
    private String jid;
    private String name;
    private ChatAdapter adapter;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ViewUtils.inject(this);
        //获取与谁聊天的jid和name
        Bundle bundle = getIntent().getBundleExtra("to_chat");
        jid = bundle.getString("chat_jid");
        name = bundle.getString("chat_name");
        //优先显示昵称,要显示到标题上面
        String nameOrJid = CommonUtils.priorityNameOrJid(name, jid);
        String chat_title = getResources().getString(R.string.chat_title);//与%1$s%2$s正在聊天中...
        chat_title=String.format(chat_title,"好人",nameOrJid);//参数1“要格式化的字符串”
        tv_title.setText(chat_title);
        //获取ChatManager和Chat对象
        connectionManager = ConnectionManager.getInstance();
        ChatManager chatManager =connectionManager .getChatManager();
//        chat = chatManager.createChat(jid, messageListener);//废掉
        chat = chatManager.createChat(jid, null);
        //回显数据
        List<Msg> chatRecordList = MyDbUtils.getChatRecordByJid(jid);
        if (chatRecordList!=null&&chatRecordList.size()>0){
            msgList.addAll(chatRecordList);
        }
        //给listview设置适配器
        adapter = new ChatAdapter(ChatActivity.this, msgList);
        listView.setAdapter(adapter);
        listView.setSelection(msgList.size());
        //注册聊天的观察者
        connectionManager.addChatMsgObservers(jid,this);
        //清除未读消息
//        MyDbUtils.clearUnReadMsgCount(jid);
    }
    //同样，监听里面的方法实在子线程里面被调用  chat：当前的会话   Message：接收到的消息
    private MessageListener messageListener=new MessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
//            CommonUtils.showToast(ChatActivity.this,message.getBody());//message.getBody()消息内容
            //构建msg，添加都集合里面
            Msg msg = new Msg(jid, name, Msg.MSG_TYPE_RECEIVE, message.getBody());
            //保存到数据库
            MyDbUtils.saveMsg(msg);
            msgList.add(msg);
            //刷新适配器
            CommonUtils.runOnUIThrad(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    listView.setSelection(msgList.size());
                }
            });
        }
    };
    //发送按钮的点击事件
    public void send(View view){
        String content = et_input.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            //空消息
            CommonUtils.showToast(ChatActivity.this,"无法发送空消息，发送空消息是对好友莫大的侮辱！！！");
        }else{
            try {
                //发送消息
                chat.sendMessage(content);
                //构建Msg，添加到集合里面
                Msg msg = new Msg(jid, name, Msg.MSG_TYPE_SEND, content);
                msgList.add(msg);
                //刷新适配器
                adapter.notifyDataSetChanged();
                listView.setSelection(msgList.size());
                //保存数据到数据库
                MyDbUtils.saveMsg(msg);
                //清除输入框的内容
                et_input.setText("");
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("test", "ChatActivity===>onPause");
        //清除未读消息
        MyDbUtils.clearUnReadMsgCount(jid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("test","ChatActivity===>onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("test", "ChatActivity===>onDestroy");
//        chat.removeMessageListener(messageListener);
        connectionManager.removeMsgObserver(jid);
    }

    @Override
    public void notity(Msg msg) {
        msgList.add(msg);
        //刷新适配器
        CommonUtils.runOnUIThrad(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                listView.setSelection(msgList.size());
            }
        });
    }
}
