package com.mayo.pulltorefresh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mayo.pulltorefresh.listview.LoadListView;
import com.mayo.pulltorefresh.listview.LoadingView;


public class ActLoadView extends Activity {

    String[] adapterData = new String[]{"A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private Context mContext;

    private BaseAdapter mAdapter = new BaseAdapter() {
        int color;

        @Override
        public int getCount() {
            return adapterData.length;
        }

        @Override
        public Object getItem(int position) {
            return adapterData[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            color = getResources().getColor(android.R.color.darker_gray);
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, dp2px(mContext, 50)));
            textView.setText(adapterData[position]);
            textView.setTextSize(20);
            textView.setTextColor(0xff000000);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(color);
            textView.setPadding(10, 0, 0, 0);

            return textView;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_load_view);

        mContext = this;
        final LoadListView refreshLoadListView = (LoadListView) findViewById(R.id.pullDownListView);
        final LoadingView loadingView = (LoadingView) findViewById(R.id.pullUpView);

        final RotateAnimation rotate = new RotateAnimation(0, 300);
        rotate.setDuration(500);

        refreshLoadListView.getListView().setAdapter(mAdapter);

        refreshLoadListView.setOnPullHeightChangeListener(new LoadListView.OnPullHeightChangeListener() {


            @Override
            public void onBottomHeightChange(int footerHeight,
                                             int pullHeight) {

                float progress = (float) pullHeight
                        / (float) footerHeight;

                if (progress < 0.5) {
                    progress = 0.0f;
                } else {
                    progress = (progress - 0.5f) / 0.5f;
                }

                if (progress > 1.0f) {
                    progress = 1.0f;
                }

                if (!refreshLoadListView.isRefreshing()) {
                    loadingView.setProgress(progress);
                }

            }

            @Override
            public void onRefreshing(final boolean isTop) {

                if (!isTop) {
                    loadingView.startAnimate();
                }

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //once, the loading is done, pullup the listview
                        refreshLoadListView.pullUp();
                        if (!isTop) {
                            loadingView.stopAnimate();
                        }
                    }

                }, 3000);


            }

        });
    }

    private int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}