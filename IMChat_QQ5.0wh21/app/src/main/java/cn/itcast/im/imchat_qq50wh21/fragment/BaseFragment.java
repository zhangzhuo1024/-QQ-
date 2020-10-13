package cn.itcast.im.imchat_qq50wh21.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;

import cn.itcast.im.imchat_qq50wh21.R;

/**
 * Created by Administrator on 2016/7/15.
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    private View rootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext=activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView==null){
            rootView = getViewLayout(inflater);
            //把rootView注入到当前的子类里面，就可以在当前的子类里面通过注解找到控件
            ViewUtils.inject(this,rootView);
        }
        return rootView;
    }
    protected abstract View getViewLayout(LayoutInflater inflater);
    //声明获取SwipeAdapter打开的条目数量的方法，SessionFragment会去重写该方法
    public int getOpenSwipeLayoutCount(){return 0;}
    //声明关闭所有侧拉删除的条目，SessionFragment会去重写该方法
    public void closeAllSwipeLayout(){};
    //获取菜单是否打开 ，SessionFragment会去重写该方法
    public void setMenuIsOpen(boolean menuIsOpen){};
}
