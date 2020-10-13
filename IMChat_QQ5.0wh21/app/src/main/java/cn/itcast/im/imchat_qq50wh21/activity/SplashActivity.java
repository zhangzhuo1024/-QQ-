package cn.itcast.im.imchat_qq50wh21.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.jivesoftware.smack.XMPPException;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;

public class SplashActivity extends Activity {

    private ConnectionManager connectionManager;
    private EditText et_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        et_ip = (EditText) findViewById(R.id.et_ip);
        //获取ConnetionManager对象
        connectionManager = ConnectionManager.getInstance();
        //判断是否已经连接了
        if (connectionManager.isConnect()){
            //已经连接了
            //判断是否登录
            if (connectionManager.isLogin()){
                //已经登录
                //跳转到主界面
                //跳转到主界面
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                //跳转到登录界面
                Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }else {
            //建立连接
            connect(null);
        }
    }

    private void connect(final String ip) {
        CommonUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionManager.connect(ip);
                    CommonUtils.showToast(SplashActivity.this, "连接成功");
                    //跳转到登录界面
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    CommonUtils.showToast(SplashActivity.this, e.getMessage());
                }
            }
        });
    }
    //重新连接
    public void reConnect(View view){
        String ip = et_ip.getText().toString().trim();
        connect(ip);
    }
}
