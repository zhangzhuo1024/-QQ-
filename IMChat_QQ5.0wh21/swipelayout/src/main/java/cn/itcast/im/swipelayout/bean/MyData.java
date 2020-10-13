package cn.itcast.im.swipelayout.bean;

import cn.itcast.im.commonlibs.Cheeses;

/**
 * Created by Administrator on 2016/7/17.
 */
public class MyData extends SwipeData {
    private int index;
    public MyData(int index) {
        super();
        this.index=index;
    }

    @Override
    public String getJid() {
        return index+"";
    }

    @Override
    public String getName() {
        return Cheeses.NAMES[index];
    }

    @Override
    public String getContent() {
        return Cheeses.sCheeseStrings[index];
    }

    @Override
    public int getMsgCount() {
        return index;
    }
}
