package cn.itcast.im.imchat_qq50wh21.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MainNoTouchLinearLayout extends LinearLayout {

    private GestureDetector gestureDetector;

    public MainNoTouchLinearLayout(Context context) {
        this(context, null);
    }
    public MainNoTouchLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainNoTouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context,simpleOnGestureListener);
    }
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener=new GestureDetector.SimpleOnGestureListener(){
       //点击
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (onMainNoTouchListener!=null){
                onMainNoTouchListener.closeMenu();
            }
            return super.onSingleTapUp(e);
        }
    };
    //定义接口
    public interface OnMainNoTouchListener{
        public boolean menuIsOpen();
        public void closeMenu();
    }
    private OnMainNoTouchListener onMainNoTouchListener;
    public void setOnMainNoTouchListener(OnMainNoTouchListener onMainNoTouchListener) {
        this.onMainNoTouchListener = onMainNoTouchListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onMainNoTouchListener!=null&&onMainNoTouchListener.menuIsOpen()){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件交给手势识别器处理
        gestureDetector.onTouchEvent(event);
        return true;
    }
}
