package cn.itcast.im.swipelayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.commonlibs.GeometryUtil;

/**
 * Created by Administrator on 2016/7/18.
 */
public class GooView extends View {

    private Paint paint;
    private Path path;
    private int statusBarHeihgt;

    public GooView(Context context) {
        this(context, null);
    }

    public GooView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        paint.setColor(Color.RED);
    }
    public interface OnGooViewChangedListener{
        public void disappear(int position);
        public void reset();
    }
    private OnGooViewChangedListener onGooViewChangedListener;
    public void setOnGooViewChangedListener(OnGooViewChangedListener onGooViewChangedListener) {
        this.onGooViewChangedListener = onGooViewChangedListener;
    }

    //固定圆的圆心
    private PointF stickCenter = new PointF(200f, 200f);
    //固定圆的半径
    private float stickRadius = 9f;
    //拖拽圆的圆心
    private PointF dragCenter = new PointF(200f, 200f);
    //拖拽圆的半径
    private float dragRadius = 9f;
    //固定圆的两个附着点
    private PointF[] stickPoints = new PointF[]{
            new PointF(200f, 300f),
            new PointF(200f, 350f)
    };
    //拖拽圆圆的两个附着点
    private PointF[] dragPoints = new PointF[]{
            new PointF(100f, 300f),
            new PointF(100f, 350f)
    };
    //控制点
    private PointF controlPoint = new PointF(150f, 325f);
    //规定，两个圆的最大距离80f,固定圆的半径最小值4
    private float maxDistance=80f;
    private float minStickRadius=4f;
    private int swipeLayoutItemPosition=0;
    //初始化粘性控件的位置
    public GooView initGooViewPosition(float x,float y ,int position){
        stickCenter.set(x, y);
        dragCenter.set(x,y);
        this.swipeLayoutItemPosition=position;
        return this;
    }
    //提供给外界设置文本内容
    private String text;
    public void setGooViewText(String text){
        this.text=text;
    }
    private  Rect rect=new Rect();
    //绘制文本内容
    private void drawGooViewText(Canvas canvas){
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(true);//加粗
        //计算文本的宽高
        paint.getTextBounds(text,0,text.length(),rect);
        int textWidth=rect.width();
        int textHeight = rect.height();
        float x=dragCenter.x-textWidth*0.5f;
        float y=dragCenter.y+textHeight*0.5f;
        canvas.drawText(text, x, y, paint);
        paint.setColor(Color.RED);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //向上移动画布
        canvas.translate(0,-statusBarHeihgt);
        //固定圆的半径随着两个圆的圆心距离增大而变小  规定，两个圆的最大距离80f,固定圆的半径最小值4
        //获取两个圆圆心之间的距离
        float distance = GeometryUtil.getDistanceBetween2Points(stickCenter, dragCenter);
        distance=Math.min(distance, maxDistance);
        //计算拖拽的百分比
        float percent=distance/maxDistance;
        Float tempStickRadius = CommonUtils.evaluateFloat(percent, stickRadius, minStickRadius);
        Log.i("test",percent+"");
        //画最大拖拽范围
//        paint.setStyle(Paint.Style.STROKE);//空心圆
//        canvas.drawCircle(stickCenter.x, stickCenter.y, maxDistance, paint);
//        paint.setStyle(Paint.Style.FILL);
        //计算四个附着点和控制点的真实值
        //计算xy的差值
        float offsetY = stickCenter.y - dragCenter.y;
        float offsetX = stickCenter.x - dragCenter.x;
        //计算斜率
        double lineK = 0;
        if (offsetX != 0) {
            lineK = offsetY / offsetX;
        }
        //计算固定圆的两个附着点    获取 通过指定圆心，斜率为lineK的直线与圆的交点。
        stickPoints = GeometryUtil.getIntersectionPoints(stickCenter, tempStickRadius, lineK);
        //计算拖拽圆的两个附着点    获取 通过指定圆心，斜率为lineK的直线与圆的交点。
        dragPoints = GeometryUtil.getIntersectionPoints(dragCenter, dragRadius, lineK);
        //计算控制点    获得两点连线的中点
        controlPoint = GeometryUtil.getMiddlePoint(stickCenter, dragCenter);
       if (!isDisappear){
           if (!isOutOfRange){
               path = new Path();
               //画四个附着点
//               paint.setColor(Color.BLUE);
//               //画固定圆的两个附着点
//               canvas.drawCircle(stickPoints[0].x, stickPoints[0].y, 1f, paint);
//               canvas.drawCircle(stickPoints[1].x, stickPoints[1].y, 1f, paint);
//               //画拖拽圆的两个附着点
//               canvas.drawCircle(dragPoints[0].x, dragPoints[0].y, 1f, paint);
//               canvas.drawCircle(dragPoints[1].x, dragPoints[1].y, 1f, paint);
//               paint.setColor(Color.RED);
               //画不规则图形
               //规定一个起始点  移到第1个点
               path.moveTo(stickPoints[0].x, stickPoints[0].y);
               //绘制点1--》点2之间的曲线
               path.quadTo(controlPoint.x, controlPoint.y, dragPoints[0].x, dragPoints[0].y);
               //绘制点2--》点3之间的直线
               path.lineTo(dragPoints[1].x, dragPoints[1].y);
               //绘制点3--》点4之间的曲线
               path.quadTo(controlPoint.x, controlPoint.y, stickPoints[1].x, stickPoints[1].y);
//        path.close();//封闭路径，不调用该方法也会自动封闭
               canvas.drawPath(path, paint);
               //绘制一个固定圆
               canvas.drawCircle(stickCenter.x, stickCenter.y, tempStickRadius, paint);
           }
           //绘制一个拖拽圆
           canvas.drawCircle(dragCenter.x, dragCenter.y, dragRadius, paint);
           //注意：一定要在圆绘制完了之后再绘制文本,否则画圆的时候把文本覆盖掉了
           drawGooViewText(canvas);
       }
        //保存当前状态重置画布
        canvas.save();
        canvas.restore();
    }
    private boolean isOutOfRange=false;//是否超出最大范围
    private boolean isDisappear=false;//是否消失
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                dragCenter.set(rawX, rawY);
                isOutOfRange=false;
                isDisappear=false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                rawX = event.getRawX();
                rawY = event.getRawY();
                dragCenter.set(rawX,rawY);
                //如果超出最大距离，就断开
                float distance = GeometryUtil.getDistanceBetween2Points(stickCenter, dragCenter);
                if (distance>maxDistance){
                    isOutOfRange=true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isOutOfRange){
                    //断开了
                    distance = GeometryUtil.getDistanceBetween2Points(stickCenter, dragCenter);
                    if (distance>maxDistance){//超出最大范围
                        isDisappear=true;
                        //消失
                        if (onGooViewChangedListener!=null){
                            onGooViewChangedListener.disappear(swipeLayoutItemPosition);
                        }
                    }else{
                        //没有超出最大范围
                        dragCenter.set(stickCenter.x,stickCenter.y);
                        //重置
                        if (onGooViewChangedListener!=null){
                            onGooViewChangedListener.reset();
                        }
                    }
                }else{//至始至终都没有断开过
                    final PointF oldPointF=new PointF(dragCenter.x,dragCenter.y);
                    ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1.0f);//如果给的是1.0f，就相当于在0--》1.0之间变化
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float fraction = valueAnimator.getAnimatedFraction();//获取某一时刻的百分比
                            PointF newPoint = GeometryUtil.getPointByPercent(oldPointF, stickCenter, fraction);
                            dragCenter.set(newPoint.x,newPoint.y);
                            invalidate();
                        }
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //重置
                            if (onGooViewChangedListener!=null){
                                onGooViewChangedListener.reset();
                            }
                        }
                    });
                    valueAnimator.setInterpolator(new OvershootInterpolator(3));
                    valueAnimator.setDuration(500);
                    valueAnimator.start();
                }
                invalidate();
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取状态栏的高度
        statusBarHeihgt = CommonUtils.getStatusBarHeihgt(this);
    }
}
