package cn.itcast.im.swipelayout.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SwipeLayout extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private ViewGroup menuView;
    private ViewGroup mainView;
    private int mWidth;
    private int mHeight;
    private int maxDragRange;
    private GestureDetector gestureDetector;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //状态
    public enum DragState{
        OPEN,DRAGGING,CLOSE
    }
    private DragState currentState=DragState.CLOSE;
    private DragState preState=DragState.CLOSE;
    public interface OnSwipeStateChangedListener{
        public void onOpen(SwipeLayout swipeLayout);
        public void onClose(SwipeLayout swipeLayout);
    }
    private OnSwipeStateChangedListener onSwipeStateChangedListener;
    public void setOnSwipeStateChangedListener(OnSwipeStateChangedListener onSwipeStateChangedListener) {
        this.onSwipeStateChangedListener = onSwipeStateChangedListener;
    }

    private void init() {
        //1、创建viewDragHelper对象
        viewDragHelper = ViewDragHelper.create(this, callback);
        //创建收拾识别器
        gestureDetector = new GestureDetector(getContext(),simpleOnGestureListener);
    }
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX)>Math.abs(distanceY)){
                requestDisallowInterceptTouchEvent(true);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
    //4、重写回调里面的方法
    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child==mainView){
                if (left>0){
                    left=0;
                }else if (left<-maxDragRange){
                    left=-maxDragRange;
                }
            }else {
                if (left<mWidth-maxDragRange){
                    left=mWidth-maxDragRange;
                }else if (left>mWidth){
                    left=mWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView==mainView){
                menuView.offsetLeftAndRight(dx);
            }else{
                mainView.offsetLeftAndRight(dx);
            }
            //执行监听
            executeListener(mainView.getLeft());
            //重绘
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel==0&&mainView.getLeft()<-maxDragRange*0.5){
                open();
            }else if (xvel<0){
                open();
            }else{
                close(true);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return maxDragRange;
        }
    };

    private void executeListener(int left) {
        //保存上一次状态
        preState=currentState;
        //更新当前状态
        currentState=updateCurrentState(left);
        //执行接口回调
        if (onSwipeStateChangedListener!=null&&preState!=currentState){
            if (currentState==DragState.OPEN){
                onSwipeStateChangedListener.onOpen(this);
            }else if (currentState==DragState.CLOSE){
                onSwipeStateChangedListener.onClose(this);
            }
        }
    }
    //更新当前状态
    private DragState updateCurrentState(int left) {
        if (left==-maxDragRange){
            return DragState.OPEN;
        }else if (left==0){
            return DragState.CLOSE;
        }
        return DragState.DRAGGING;
    }

    //关闭菜单
    public void close(boolean isSmooth) {
        if (isSmooth){
            if (viewDragHelper.smoothSlideViewTo(mainView,0,0)){
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }else{
            //强行摆放关闭状态
            mainView.layout(0, 0, mWidth, mHeight);
            menuView.layout(mWidth,0,mWidth+maxDragRange,mHeight);
            //这才是真正的关闭菜单
            currentState=DragState.CLOSE;
            if (onSwipeStateChangedListener!=null){
                onSwipeStateChangedListener.onClose(this);
            }
        }

    }
    //打开菜单
    private void open() {
        //发起动画
        if (viewDragHelper.smoothSlideViewTo(mainView,-maxDragRange,0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //2、把事件交给viewDragHelper拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        requestDisallowInterceptTouchEvent(true);
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }
    //3、把事件交给viewDragHelper处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        viewDragHelper.processTouchEvent(event);
        return true;
    }
    //初始化控件的位置
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mainView.layout(0, 0, mWidth, mHeight);
        menuView.layout(mWidth,0,mWidth+maxDragRange,mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //菜单的宽度也就是最大拖拽范围
        maxDragRange = menuView.getMeasuredWidth();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //健壮性判断
        if (getChildCount()!=2){
            throw new IllegalStateException("you must have only two children");
        }
        if (!(getChildAt(0) instanceof ViewGroup)||!(getChildAt(1) instanceof ViewGroup)){
            throw new IllegalArgumentException("your child must instance of ViewGroup");
        }
        //获取菜单和主界面布局
        menuView = (ViewGroup) getChildAt(0);
        mainView = (ViewGroup) getChildAt(1);
    }
}
