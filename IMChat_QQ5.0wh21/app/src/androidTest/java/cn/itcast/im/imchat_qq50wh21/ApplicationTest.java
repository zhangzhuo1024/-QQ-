package cn.itcast.im.imchat_qq50wh21;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

import cn.itcast.im.commonlibs.CommonUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testHanZiToPinyin(){
        CommonUtils.hanZiToPinyin("asd传 智@#%播客");
    }
}