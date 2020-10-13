package cn.itcast.im.swipelayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.commonlibs.Cheeses;
import cn.itcast.im.commonlibs.CommonUtils;
import cn.itcast.im.swipelayout.adapter.SwipeAdapter;
import cn.itcast.im.swipelayout.bean.MyData;
import cn.itcast.im.swipelayout.bean.SwipeData;
import cn.itcast.im.swipelayout.view.GooView;
import cn.itcast.im.swipelayout.view.SwipeLayout;

public class MainActivity extends Activity {

    private SwipeAdapter<MyData> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SwipeLayout swipeLayout= (SwipeLayout) findViewById(R.id.swipeLayout);
//        swipeLayout.setOnSwipeStateChangedListener(swipeStateChangedListener);
        List<MyData> myDataList=new ArrayList<MyData>();
        for (int i = 0; i < Cheeses.NAMES.length; i++) {
            myDataList.add(new MyData(i));
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new SwipeAdapter<>(MainActivity.this, myDataList);
        listView.setAdapter(adapter);
        adapter.setOnSwipeAdapterCallBack(new SwipeAdapter.OnSwipeAdapterCallBack() {
            @Override
            public void onItemClick(SwipeData swipeData) {
                CommonUtils.showToast(MainActivity.this,swipeData.getName());
            }

            @Override
            public void onDeleteItem(SwipeData swipeData) {
                CommonUtils.showToast(MainActivity.this,swipeData.getName());
            }

            @Override
            public void updateUnReadMsgCount(SwipeData swipeData) {
                CommonUtils.showToast(MainActivity.this,swipeData.getMsgCount()+"");
            }
        });
       /* GooView gooView=new GooView(MainActivity.this);
        gooView.initGooViewPosition(100,100).setGooViewText("66");
        setContentView(gooView);
        gooView.setOnGooViewChangedListener(new GooView.OnGooViewChangedListener() {
            @Override
            public void disappear() {
                CommonUtils.showToast(MainActivity.this,"disappear");
            }

            @Override
            public void reset() {
                CommonUtils.showToast(MainActivity.this,"reset");
            }
        });*/
    }
    public void obtainOpenSwipeLayoutCount(View view){
        int count=adapter.getOpenSwipeLayoutCount();
        CommonUtils.showToast(MainActivity.this,count+"");
    }
    public void closeAll(View view){
        adapter.closeAllSwipeLayout(true);
    }
    /*private SwipeLayout.OnSwipeStateChangedListener swipeStateChangedListener=new SwipeLayout.OnSwipeStateChangedListener() {
        @Override
        public void onOpen() {
            CommonUtils.showToast(MainActivity.this,"onOpen");
        }

        @Override
        public void onClose() {
            CommonUtils.showToast(MainActivity.this,"onClose");
        }
    };*/
}
