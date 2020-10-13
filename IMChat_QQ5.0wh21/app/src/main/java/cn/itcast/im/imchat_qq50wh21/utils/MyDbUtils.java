package cn.itcast.im.imchat_qq50wh21.utils;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.DbModelSelector;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.imchat_qq50wh21.bean.Msg;

/**
 * Created by Administrator on 2016/7/17.
 */
public class MyDbUtils {

    private static DbUtils dbUtils;

    //创建数据库
    public static void createDb(Context context,String dbName){
        dbUtils = DbUtils.create(context, dbName);
    }
    //保存聊天信息
    public static void saveMsg(Msg msg){
        try {
            dbUtils.save(msg);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    //根据jid获取与某个好友的聊天记录
    //select * from msg_table where jid=xxx
    public static List<Msg> getChatRecordByJid(String jid){
        Selector selector = Selector.from(Msg.class).where("jid", "=", jid);
        try {
            return dbUtils.findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    //获取消息界面的聊天记录
    //select * from msg_table group by jid order by time desc
    //注意：要自己规定查找哪些列的数据
    public static List<Msg> getSessionRecord(){
        DbModelSelector dbModelSelector = Selector.from(Msg.class).groupBy("jid").orderBy("time", true).select("jid", "name", "content", "type","unReadMsgCount");
        //创建List<Msg> 集合
        List<Msg> msgList=new ArrayList<Msg>();
        try {
            List<DbModel> dbModelAll = dbUtils.findDbModelAll(dbModelSelector);
            if (dbModelAll!=null&&dbModelAll.size()>0){
                for (DbModel dbModel : dbModelAll) {
                    String jid=dbModel.getString("jid");
                    String name=dbModel.getString("name");
                    String content=dbModel.getString("content");
                    int type=dbModel.getInt("type");
                    int unReadMsgCount=dbModel.getInt("unReadMsgCount");
                    Msg msg = new Msg(jid, name, type, content);
                    msg.unReadMsgCount=unReadMsgCount;
                    msgList.add(msg);
                }
            }
            return msgList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    //根据jid删除聊天记录  delete from msg_table where jid=admin@itcast.cn
    public static void deleteSessionRecordByJid(String jid) {
        WhereBuilder whereBuilder = WhereBuilder.b("jid", "=", jid);
        try {
            dbUtils.delete(Msg.class,whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    /*
    *   先不考虑已读的情况
    *   当好友第一次发送消息给我    msg的unReadMsgCount=1   也就是说  数据库里面的unReadMsgCount列的值1
    *   当好友第二次发送消息给我    msg的unReadMsgCount=2   也就是上一次的unReadMsgCount+1
    *   当好友第二次发送消息给我    msg的unReadMsgCount=3   依然上一次的unReadMsgCount+1
    *
    *
    *   当好友第n次发送消息给我      msg的unReadMsgCount=n  n-i次的unReadMsgCount+1
    *
    *   规律：在保存消息之前，要先获取上一次未读消息的数量,也就是获取该好友的上一次msg
    * */
    public static void saveMsgAndCount(Msg msg) {
        Msg recentMsg=getRecentMsgByJid(msg.getJid());
        if (recentMsg==null){
            //说明该好友是第一次给我发消息
            msg.unReadMsgCount=1;
        }else{
            msg.unReadMsgCount=recentMsg.unReadMsgCount+1;
        }
        saveMsg(msg);
    }
    //根据jid获取该好友上一次的聊天记录
    //select * from msg_type where jid=admin@itcast.cn order by time
    private static Msg getRecentMsgByJid(String jid) {
        Selector selector = Selector.from(Msg.class).where("jid", "=", jid).orderBy("time", true);
        try {
            return dbUtils.findFirst(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    //跟jid清除数据库里面的未读消息为0
    //update msg_table set unReadMsgCount=0 where jid=admin@itcast.cn
    public static void clearUnReadMsgCount(String jid){
        Msg msg=new Msg();
        msg.unReadMsgCount=0;//把该属性值赋值给第三个参数指定的列
        WhereBuilder whereBuilder = WhereBuilder.b("jid", "=", jid);
        try {
            dbUtils.update(msg,whereBuilder,"unReadMsgCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
