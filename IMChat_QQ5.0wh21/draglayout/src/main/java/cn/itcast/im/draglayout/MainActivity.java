package cn.itcast.im.draglayout;

import android.app.Activity;
import android.os.Bundle;

import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.draglayout.view.DragLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DragLayout dragLayout= (DragLayout) findViewById(R.id.dragLayout);
        dragLayout.setOnDragStateChangedListener(new MyOnDragStateChangedListener());
    }

    private class MyOnDragStateChangedListener implements DragLayout.OnDragStateChangedListener{

        @Override
        public void onOpen() {
            CommonUtils.showToast(MainActivity.this,"onOpen");
        }

        @Override
        public void onDragging(float percent) {
            CommonUtils.showToast(MainActivity.this,"percent="+percent);
        }

        @Override
        public void onClose() {
            CommonUtils.showToast(MainActivity.this,"onClose");
        }

        @Override
        public boolean menuCanOpen() {
            return false;
        }
    }
}
