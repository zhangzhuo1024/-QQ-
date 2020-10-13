package cn.itcast.im.imchat_qq50wh21.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.commonlibs.Cheeses;
import cn.itcast.im.draglayout.view.DragLayout;
import cn.itcast.im.imchat_qq50wh21.R;
import cn.itcast.im.imchat_qq50wh21.core.ConnectionManager;
import cn.itcast.im.imchat_qq50wh21.fragment.BaseFragment;
import cn.itcast.im.imchat_qq50wh21.fragment.ContactFragment;
import cn.itcast.im.imchat_qq50wh21.fragment.SessionFragment;
import cn.itcast.im.imchat_qq50wh21.view.MainNoTouchLinearLayout;
import cn.itcast.im.imchat_qq50wh21.view.NoScrollViewPager;

public class MainActivity extends FragmentActivity {
    @ViewInject(R.id.dragLayout)
    private DragLayout dragLayout;
    //菜单里面的控件
    @ViewInject(R.id.tv_account)
    private TextView tv_account;
    @ViewInject(R.id.lv_menu)
    private ListView lv_menu;
    //主界面里面的控件
    @ViewInject(R.id.iv_main_icon)
    private ImageView iv_main_icon;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.noScrollViewPager)
    private NoScrollViewPager noScrollViewPager;
    @ViewInject(R.id.rg_session_contact)
    private RadioGroup rg_session_contact;
    @ViewInject(R.id.main_layout)
    private MainNoTouchLinearLayout mainNoTouchLinearLayout;

    private ConnectionManager connectionManager;
    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        //获取ConnetionManager对象
        connectionManager = ConnectionManager.getInstance();

        //初始化菜单界面和逻辑
        initMenuViewLayout();
        //初始化主界面和逻辑
        initMainViewLayout();
    }
    private void initMenuViewLayout() {
        //获取账号
        String accountJid = connectionManager.getAccountJid();
        tv_account.setText(accountJid);
        //给listview设置适配器
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,
        android.R.id.text1, Cheeses.QQ_FUCTIONS){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView  textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        };
        lv_menu.setAdapter(adapter);
        //条目的点击事件
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == Cheeses.QQ_FUCTIONS.length - 1) {
                    //退出,实际就是断开连接
                    connectionManager.disConnect();
                    //跳转到登录界面
                    //Caused by: java.lang.IllegalStateException: Not connected to server.
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void initMainViewLayout() {
        //给dragLayout设置状态改变的监听
        dragLayout.setOnDragStateChangedListener(onDragStateChangedListener);
        //初始化Fragment
        initFragmetns();
        //给noScrollViewPager设置适配器
        noScrollViewPager.setAdapter(fragmentPagerAdapter);
        //给RadioGroup设置点击切换的监听
        rg_session_contact.setOnCheckedChangeListener(onCheckedChangeListener);
        //默认选中“聊天”
        rg_session_contact.check(R.id.rb_session);
        //给mainNoTouchLinearLayout设置监听，当菜单打开的时候拦截触摸事件
        mainNoTouchLinearLayout.setOnMainNoTouchListener(onMainNoTouchListener);
    }
    //初始化Fragment
    private void initFragmetns() {
        fragments = new ArrayList<BaseFragment>();
        fragments.add(new SessionFragment());
        fragments.add(new ContactFragment());
    }

    //dragLayout设置状态改变的监听方法
    private DragLayout.OnDragStateChangedListener onDragStateChangedListener=new DragLayout.OnDragStateChangedListener() {
        @Override
        public void onOpen() {
            //告诉SessionFragment菜单打开了了
            fragments.get(0).setMenuIsOpen(true);
        }

        @Override
        public void onDragging(float percent) {
            //percent  0-->1.0     Alpha:1.0-->0
            ViewCompat.setAlpha(iv_main_icon,1-percent);
        }

        @Override
        public void onClose() {
            //Object target,  执行动画的view
            // String propertyName, //动画类型  平移
            // float... values
            ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(iv_main_icon,"translationX",5);
            objectAnimator.setInterpolator(new CycleInterpolator(5));
            objectAnimator.setDuration(300);
            objectAnimator.start();
            //告诉SessionFragment菜单关闭了
            fragments.get(0).setMenuIsOpen(false);
        }

        @Override
        public boolean menuCanOpen() {
            int count = fragments.get(0).getOpenSwipeLayoutCount();
            return count==0;
        }
    };
    //noScrollViewPager的监听事件
    private FragmentPagerAdapter fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    };
    //RadioGroup的点击切换监听
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //找到点击的RadioButton
            RadioButton rb_checked= (RadioButton) group.findViewById(checkedId);
            //找到索引
            int index = group.indexOfChild(rb_checked);
            noScrollViewPager.setCurrentItem(index);
            //修改标题
            tv_title.setText(index==0?"消息":"联系人");
            //如果点击的是联系人，那么就要关闭所有的侧拉条目
            if (index==1){
                fragments.get(0).closeAllSwipeLayout();
            }
        }
    };
    private MainNoTouchLinearLayout.OnMainNoTouchListener onMainNoTouchListener=new MainNoTouchLinearLayout.OnMainNoTouchListener() {
        @Override
        public boolean menuIsOpen() {
            return dragLayout.getCurrentState()== DragLayout.DragState.OPEN;
        }
        @Override
        public void closeMenu() {
            dragLayout.close();
        }
    };
}
