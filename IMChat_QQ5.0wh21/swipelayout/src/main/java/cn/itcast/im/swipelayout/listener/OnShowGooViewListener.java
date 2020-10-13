package cn.itcast.im.swipelayout.listener;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.itcast.im.swipelayout.view.GooView;

/**
 * Created by Administrator on 2016/7/18.
 */
public class OnShowGooViewListener implements View.OnTouchListener, GooView.OnGooViewChangedListener {

    private GooView gooView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    public OnShowGooViewListener(Context context){
        //创建粘性控件
        gooView = new GooView(context);
        //获取WindowManager窗体
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //一定要给WindowManager设置布局参数，尤其是透明色
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //给粘性控件添加监听
        gooView.setOnGooViewChangedListener(this);
    }
    private View mView;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            mView=v;
            //取出点击红点所在的的位置
            int position= (int) v.getTag();
            //隐藏TextView
            v.setVisibility(View.INVISIBLE);
            //设置GooView的显示位置
            String text = ((TextView) v).getText().toString();
            gooView.initGooViewPosition(rawX,rawY,position).setGooViewText(text);
            //添加到窗体里面
            windowManager.addView(gooView,params);
        }
        //把触摸事件交给gooView
        gooView.onTouchEvent(event);
        return true;
    }
    @Override
    public void disappear(int position) {
        //从窗体上移除粘性控件,一定要判断，否则可能有异常
        if (gooView.getParent()!=null){
            windowManager.removeView(gooView);
        }
    }

    @Override
    public void reset() {
        if (gooView.getParent()!=null){
            windowManager.removeView(gooView);
        }
        //显示TextView
        mView.setVisibility(View.VISIBLE);
    }
}
