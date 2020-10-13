package cn.itcast.im.imchat_qq50wh21.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.bean.Msg;
import cn.itcast.im.imchat_qq50wh21.utils.MyDbUtils;

/**
 * Created by Administrator on 2016/7/14.
 * 负责界面和服务器打交道，界面通过这个类访问asmark，也就是间接访问服务器
 *      1.与服务器建立连接
 *      2.登录
 *      3.获取好友
 *      4.发送消息
 *      5.接收消息
 *      6.退出
 *
 *      。。。
 */
public class ConnectionManager {
    private static final java.lang.String HOST = "192.168.19.62";
    private static final int PORT = 5222;//XMPP默认端口号
    private String host=HOST;
    private XMPPConnection xmppConnection;
    private Roster roster;

    private ConnectionManager(){};
    private static ConnectionManager connectionManager=new ConnectionManager();
    public static ConnectionManager getInstance(){
        return  connectionManager;
    }
    //1.与服务器建立连接
    public void connect(String ip) throws XMPPException {
        if (!TextUtils.isEmpty(ip)){
            host=ip;
        }
        //连接ip和端口号配置
        ConnectionConfiguration configuration=new ConnectionConfiguration(host,PORT);
        configuration.setSASLAuthenticationEnabled(false);
        xmppConnection = new XMPPConnection(configuration);
        //连接
        xmppConnection.connect();
    }
    //2.登录
    public void login(Context context,String account,String pwd) throws XMPPException {
        xmppConnection.login(account, pwd);
        if (isLogin()){
            //创建数据库
            String dbName = CommonUtils.hanZiToPinyin(getAccountJid());
            MyDbUtils.createDb(context,dbName);
            //创建统一接收消息的监听器
            xmppConnection.addPacketListener(packetListener,packetFilter);
        }
    }
    //判断是否已经连接
    public boolean isConnect(){
       return xmppConnection!=null&&xmppConnection.isConnected();
    }
    //判断是否已经登录
    public boolean isLogin(){
       return xmppConnection.isAuthenticated();
    }
    //获取用户的账号
    public String getAccountJid(){
        return xmppConnection.getUser();
    }
    //断开连接，退出
    public void disConnect(){
        xmppConnection.disconnect();
    }
    //获取Rost对象,通过Roster对象的getEntries（）获取所有好友的集合
    public Roster getRoster(){
        if (roster==null){
            roster = xmppConnection.getRoster();
        }
        return roster;
    }
    //获取一个ChatManager对象，用于创建会话Chat
    public ChatManager getChatManager(){
        return xmppConnection.getChatManager();
    }
    //消息的过滤器  Packet是所有消息的统称，包括Message、Presence。。。
    private PacketFilter packetFilter=new PacketFilter() {
        @Override
        public boolean accept(Packet packet) {
            return packet instanceof Message;
        }
    };
    //处理消息的监听器，注意：1、这个监听器里面的processPacket只有在过滤器返回true的时候被调用
    //2、监听器里面的processPacket是在子线程被调用
    //做两件事：1、保存到数据   2、通知界面
    private PacketListener packetListener=new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            Message message= (Message) packet;
            if (!TextUtils.isEmpty(message.getBody())){
                //Log.i("test",message.toXML());
                //1、保存到数据库   String jid, String name, int type, String content
                String fromJid = message.getFrom();//from="aaa@itcast.cn/Spark 2.6.3"
                //去掉设备名称
                fromJid=fromJid.substring(0,fromJid.lastIndexOf("/"));  // 0=<  截取  <end
                //根据jid获取好友
                RosterEntry rosterEntry = roster.getEntry(fromJid);
                String name = rosterEntry.getName();//获取昵称
                Msg msg = new Msg(fromJid, name, Msg.MSG_TYPE_RECEIVE, message.getBody());
//                MyDbUtils.saveMsg(msg);
                MyDbUtils.saveMsgAndCount(msg);//保存接收到的消息和未读消息的数量
                //2、通知消息界面   也称为观察者
                for (MsgObserver msgObserver : msgObserverList) {
                    msgObserver.notity(msg);
                }
                //通知聊天界面,根据jid取出这个好友的观察者再通知
                MsgObserver msgObserver = chatMsgObservers.get(fromJid);
                if (msgObserver!=null){
                    msgObserver.notity(msg);
                }

            }
        }
    };
    //---------------------------------观察者模式-------------------------------------------------------------
    //定义接口
    public interface MsgObserver{
        public void notity(Msg msg);
    }
    //定义存放接口实现类的集合
    private List<MsgObserver>  msgObserverList=new ArrayList<MsgObserver>();
    //往集合里面添加观察者
    public void addMsgObserver(MsgObserver msgObserver){
        msgObserverList.add(msgObserver);
    }
    //从集合里面移除观察者
    public void removeMsgObserver(MsgObserver msgObserver){
        msgObserverList.remove(msgObserver);
    }
    //存放聊天界面不同好友的观察者
    private Map<String,MsgObserver> chatMsgObservers=new HashMap<String,MsgObserver>();
    //往chatMsgObservers集合里面添加聊天的观察者
    public void addChatMsgObservers(String jid,MsgObserver msgObserver){
        chatMsgObservers.put(jid,msgObserver);
    }
    //从往chatMsgObservers集合里面移除聊天的观察者
    public void removeMsgObserver(String jid){
        chatMsgObservers.remove(jid);
    }
}
