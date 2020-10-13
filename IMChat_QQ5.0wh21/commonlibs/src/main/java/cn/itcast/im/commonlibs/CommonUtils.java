package cn.itcast.im.commonlibs;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.jivesoftware.smack.RosterEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/14.
 * 公共的工具类
 */
public class CommonUtils {
    private static  ExecutorService threadPool = Executors.newCachedThreadPool();
    private static HanyuPinyinOutputFormat format;

    //1.在子线程中执行任务
    public static void runInThread(Runnable task){
        threadPool.execute(task);
    }
    //2.创建handler
    private static Handler handler=new Handler(Looper.getMainLooper());
    public static Handler getHandler() {
        return handler;
    }
    //3.在主线程执行任务
    public static void runOnUIThrad(Runnable task){
        handler.post(task);
    }
    //4.在主线程里面吐司
    private static Toast toast;
    public static void showToast(final Context context, final String text){
        runOnUIThrad(new Runnable() {
            @Override
            public void run() {
                if (toast==null){
                    toast=Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
                toast.setText(text);
                toast.show();
            }
        });
    }
    //5.dp-->px
    public static float dpToPx(Context context,float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }
    //6.sp-->px
    public static float spToPx(Context context,float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }
    //浮点型估值
    public static Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    //颜色估值
    public static int evaluateArgb(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
    //汉字转拼音
    public static String hanZiToPinyin(String hanZi){
        if (format==null){
            //设置输出配置
            format = new HanyuPinyinOutputFormat();
        }
        //设置大写
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        //去掉声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder sb=new StringBuilder();
        char[] chars = hanZi.toCharArray();
        for (char aChar : chars) {
            if (Character.isWhitespace(aChar)){
                continue;
            }
            if (Character.toString(aChar).matches("[\\u4E00-\\u9FA5]")){
                //是汉字
                try {
                    String pinyin = PinyinHelper.toHanyuPinyinStringArray(aChar, format)[0];
                    sb.append(pinyin);

                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            }else {
                if (Character.isLetter(aChar)){
                    //是字母
                    sb.append(Character.toUpperCase(aChar));
                }else{
                    //乱七八糟的字符，看不懂！#@#@%￥￥%……%&
                    sb.append("#");
                }
            }

        }
        Log.i("test",sb.toString());
        return sb.toString();
    }
    //优先显示好友的昵称
    public static String priorityNameOrJid(RosterEntry rosterEntry){
        if (!TextUtils.isEmpty(rosterEntry.getName())){
            return rosterEntry.getName();
        }
        return rosterEntry.getUser();
    }
    //优先显示好友的昵称
    public static String priorityNameOrJid(String name,String jid){
        if (!TextUtils.isEmpty(name)){
            return name;
        }
        return jid;
    }
    //获取当前的时间
    public static String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = simpleDateFormat.format(new Date());
        return time;
    }
    //获取状态栏的高度
    public static int getStatusBarHeihgt(View view){
        Rect rect=new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
}
