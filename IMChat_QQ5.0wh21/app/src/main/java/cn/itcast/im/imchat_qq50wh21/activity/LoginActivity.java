package cn.itcast.im.imchat_qq50wh21.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.XMPPException;

import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;

public class LoginActivity extends Activity {
    @ViewInject(R.id.et_account)
    private EditText et_account;
    @ViewInject(R.id.et_pwd)
    private EditText et_pwd;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        //获取connectionManager对象
        connectionManager = ConnectionManager.getInstance();
    }
    //点击登录
    public void login(View view){
        String account = et_account.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        //判断非空
        if (TextUtils.isEmpty(account)||TextUtils.isEmpty(pwd)){
            //至少有一给为空
            Toast.makeText(LoginActivity.this,"用户名和密码不能为空，操作太另类！！！",Toast.LENGTH_SHORT).show();
        }else{
            try {
                connectionManager.login(LoginActivity.this,account,pwd);
                //跳转到主界面
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } catch (XMPPException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
