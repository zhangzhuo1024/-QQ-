package cn.itcast.im.imchat_qq50wh21.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.itcast.im.commonlibs.Cheeses;
import cn.itcast.im.commonlibs.CommonUtils;

/**
 * Created by Administrator on 2016/7/15.
 */
public class QuickIndexBar extends View {

    private Paint paint;
    private float cellWidth;
    private float cellHeight;
    private Rect rect;

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        paint.setColor(Color.GRAY);//颜色
        paint.setTypeface(Typeface.DEFAULT_BOLD);//粗体
//        paint.setFakeBoldText(true);//粗体
        paint.setTextSize(CommonUtils.spToPx(context, 12f));//设置文字大小
        //创建矩阵
        rect = new Rect();
    }
    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < Cheeses.LETTERS.length; i++) {
            //计算文本的宽高并存到矩阵里面
            paint.getTextBounds(Cheeses.LETTERS[i], 0, 1, rect);
            //文本的宽高
            int textWidth = rect.width();
            int textHeight = rect.height();
            //绘制字母
            /**
             * @param text  文本
             * @param x     x轴坐标
             * @param y     y轴坐标
             * @param paint 画笔
             */
            float x=cellWidth*0.5f-textWidth*0.5f;
            float y=cellHeight*0.5f+textHeight*0.5f+cellHeight*i;
            paint.setColor(i==currentIndex?Color.DKGRAY:Color.GRAY);
            canvas.drawText(Cheeses.LETTERS[i],x,y,paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //单元格宽高
        cellWidth = getMeasuredWidth();
        cellHeight = getMeasuredHeight() *1.0f/ Cheeses.LETTERS.length;
    }
    //定义接口
    public interface OnLetterChangedListener{
        public void letterChanged(String letter);
    }
    private OnLetterChangedListener onLetterChangedListener;
    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }
    private int currentIndex=-1;
    private int preIndex=-1;
    //处理触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                preIndex=currentIndex;
                //计算下标
                currentIndex= (int) (y/cellHeight);
                if (currentIndex>=0&&currentIndex<Cheeses.LETTERS.length){
//                    CommonUtils.showToast(getContext(),Cheeses.LETTERS[currentIndex]);
                    if (onLetterChangedListener!=null&&preIndex!=currentIndex){
                        //告诉外界
                        onLetterChangedListener.letterChanged(Cheeses.LETTERS[currentIndex]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                currentIndex=-1;
                break;
            default:break;
        }
        //重绘
        invalidate();
        return true;
    }
}
