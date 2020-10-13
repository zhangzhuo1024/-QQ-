package cn.itcast.im.draglayout.view;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import cn.itcast.im.commonlibs.CommonUtils;

/**
 * Created by Administrator on 2016/7/14.
 */
public class DragLayout extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private ViewGroup menuView;
    private ViewGroup mainView;
    private int mWidth;
    private int mHeight;
    private int maxDragRange;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //定义拖拽的状态
    public enum DragState{
        OPEN,DRAGGING,CLOSE
    }
    //定义当前状态,也就是默认状态
    private DragState currentState=DragState.CLOSE;
    public DragState getCurrentState() {
        return currentState;
    }

    private DragState preState=DragState.CLOSE;
    //定义接口
    public interface OnDragStateChangedListener{
        public void onOpen();
        public void onDragging(float percent);
        public void onClose();
        public boolean menuCanOpen();//true表示能打开，false表示不能
    }
    //定义接口对象
    private OnDragStateChangedListener onDragStateChangedListener;
    //对外界提供方法把接口的实现类传进来
    public void setOnDragStateChangedListener(OnDragStateChangedListener onDragStateChangedListener) {
        this.onDragStateChangedListener = onDragStateChangedListener;
    }

    private void init() {
        //1.创建ViewDragHelper对象
        viewDragHelper = ViewDragHelper.create(this, callback);
    }
    //4.重写viewDragHelper的回调方法
    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        /**
         * 尝试捕获一个view  尝试拖拽一个view  相当于总开关，如果返回false，后面的回调都不会被调用
         * @param child     被拖拽的view
         * @param pointerId     多点触碰  第一个按下的点pointerId=0，后面依次++
         * @return  返回值决定了view能否被拖拽
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }
        //view已经被捕获了
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
        /**
         * 限制view水平方法拖拽的距离
         * @param child   被拖拽的view
         * @param left      告诉ViewDragHelper要拖拽到哪个位置  view.getLeft()+dx
         * @param dx        瞬间的偏移量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            int oldLeft = child.getLeft();
//            Log.i("test", "child = [" + child.getTag() + "], left = [" + left + "], dx = [" + dx + "]" + "], oldLeft = [" + oldLeft + "]");
            if (child==mainView){
               left=fixLeftRange(left);
            }
            return left;
        }

        /**
         * 当view位置反生改变的时候调用
         * @param changedView  位置发生改变的孩子
         * @param left  距离屏幕左边的距离
         * @param top   距离屏幕上边的距离
         * @param dx    水平方向的偏移量
         * @param dy    垂直方向的偏移量
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView==menuView){
                //强行固定menuView的位置不动
                menuView.offsetLeftAndRight(-dx);
                int oldLeft = mainView.getLeft();
                int newLeft=oldLeft+dx;
                //修正拖拽范围
                newLeft=fixLeftRange(newLeft);
                dx=newLeft-oldLeft;
                //把菜单的偏移量给主界面，让主界面移动
                mainView.offsetLeftAndRight(dx);
//                int oldLeft = mainView.getLeft();
//                int newLeft=oldLeft+dx;
//                //修正拖拽范围
//                newLeft=fixLeftRange(newLeft);
//                dx=newLeft-oldLeft;
//                mainView.layout(newLeft,0,mWidth+newLeft,mHeight);
            }
            //计算移动的百分比
            float percent=mainView.getLeft()*1.0f/maxDragRange;
            //执行一系列的伴随动画
            dispatchAnimation(percent);
            //执行接口回调
            executeListener(percent);
        }

        /**
         * 当释放view的时候被调用
         * @param releasedChild  释放的view
         * @param xvel  释放的时候水平方向的瞬间速度
         * @param yvel  释放的时候垂直方向的瞬间速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
//            Log.i("test", "releasedChild = [" + releasedChild.getTag() + "], xvel = [" + xvel + "], yvel = [" + yvel + "]");
            if (xvel==0&&mainView.getLeft()>maxDragRange*0.5){
                open();
            }else if (xvel>0){
                open();
            }else{
                close();
            }
        }

        /**
         * 获取view水平方向的拖拽范围,当有子view可以滑动出现事件冲突是，就会检查拖拽滑动的范围
         * 返回值大于0就可以
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return maxDragRange;
        }
    };
    //执行接口回调
    private void executeListener(float percent) {
        //保存上一次状态
        preState=currentState;
        //判断当前的拖拽状态
        currentState=updateDragState(percent);
        //再执行接口回调
        if (onDragStateChangedListener!=null){
            if (currentState==DragState.CLOSE&&preState!=currentState){
                onDragStateChangedListener.onClose();
            }else if (currentState==DragState.OPEN&&preState!=currentState){
                onDragStateChangedListener.onOpen();
            }else{
                onDragStateChangedListener.onDragging(percent);
            }
        }
    }

    private DragState updateDragState(float percent) {
        if (percent==0){
            return DragState.CLOSE;
        }else if (percent==1.0f){
            return DragState.OPEN;
        }
        return DragState.DRAGGING;
    }

    //伴随动画
    private void dispatchAnimation(float percent) {

        Log.i("test",percent+"");
        //主界面
            //缩放动画  percent:0f--->1.0f   Scale:1.0f--->0.8f
        Float evaluateFloat = CommonUtils.evaluateFloat(percent, 1.0f, 0.8f);
        ViewCompat.setScaleX(mainView,evaluateFloat);
        ViewCompat.setScaleY(mainView, evaluateFloat);
        //菜单界面
            //缩放动画  percent:0f--->1.0f   Scale:0.5f--->1.0f
        evaluateFloat=CommonUtils.evaluateFloat(percent,0.5f,1.0f);
        ViewCompat.setScaleX(menuView, evaluateFloat);
        ViewCompat.setScaleY(menuView, evaluateFloat);
            //平移动画  percent:0f--->1.0f   Translation:-mWidth*0.5f--->0f
        evaluateFloat=CommonUtils.evaluateFloat(percent,-mWidth*0.5f,0);
        ViewCompat.setTranslationX(menuView, evaluateFloat);
            //透明度动画  percent:0f--->1.0f   Alpha:0.3f--->1.0f
        evaluateFloat=CommonUtils.evaluateFloat(percent,0.3f,1.0f);
        ViewCompat.setAlpha(menuView,evaluateFloat);
        //背景亮度变化  注意：DragLayout一定要有背景图片
        int evaluateArgb = CommonUtils.evaluateArgb(percent, Color.BLACK, Color.TRANSPARENT);
        getBackground().setColorFilter(evaluateArgb, PorterDuff.Mode.SRC_OVER);
    }

    //关闭菜单
    public void close() {
//        mainView.layout(0,0,mWidth,mHeight);
        if (viewDragHelper.smoothSlideViewTo(mainView,0,0)){
//            invalidate();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    //打开菜单
    private void open() {
//        mainView.layout(maxDragRange,0,maxDragRange+mWidth,mHeight);
        //如果返回true，表示要执行滑动动画
        if (viewDragHelper.smoothSlideViewTo(mainView,maxDragRange,0)){
            //重绘
//            invalidate();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    //计算view的滑动的值
    @Override
    public void computeScroll() {
        super.computeScroll();
       if (viewDragHelper.continueSettling(true)){
           //重绘
//           invalidate();
           ViewCompat.postInvalidateOnAnimation(this);
       }
    }

    private int fixLeftRange(int left) {
        if (left>maxDragRange){
            left=maxDragRange;
        }else if (left<0){
            left=0;
        }
        return left;
    }

    //2.把事件交给viewDragHelper去拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //交给viewDragHelper去拦截之前，先根据外面的侧拉删除条目的打开数量判断要不要拦截事件
        if (currentState==DragState.CLOSE&&onDragStateChangedListener!=null){
            if (!onDragStateChangedListener.menuCanOpen()){
                return false;//不拦截
            }
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }
    //3.把事件交给viewDragHelper处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
    //当布局加载完毕的时候被调
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
        //获取菜单和主界面
        menuView = (ViewGroup) getChildAt(0);
        mainView = (ViewGroup) getChildAt(1);
    }
    //获取屏幕的宽高，控件的宽高
    //这个方法在onMeasure（）方法执行完了之后调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //控件的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //最大拖拽范围
        maxDragRange = (int) (mWidth*0.6);
    }
}
