package cn.itcast.im.imchat_qq50wh21.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.swipelayout.bean.SwipeData;

/**
 * Created by Administrator on 2016/7/17.
 *
 * 使用DbUtils创建数据库和表  创建表是通过注解
 */
@Table(name = "msg_table")  //把Msg对象跟表联系在一起，表名叫msg_table
public class Msg extends SwipeData{
    public static final int MSG_TYPE_SEND=0;//发送的消息类型
    public static final int MSG_TYPE_RECEIVE=1;//接收的消息类型
    @Id(column = "_id")
    public int id;//信息保存到数据库里面使用属性，给数据库使用
    @Column(column = "jid")
    public String jid;
    public String name;
    public int type;//消息类型
    public String content;
    public String time;
    public int unReadMsgCount;
    public Msg(){}
    public Msg(String jid, String name, int type, String content) {
        this.jid = jid;
        this.name = name;
        this.type = type;
        this.content = content;
        this.time= CommonUtils.getTime();
    }

    @Override
    public String getJid() {
        return this.jid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public int getMsgCount() {
        return this.unReadMsgCount;
    }
}
