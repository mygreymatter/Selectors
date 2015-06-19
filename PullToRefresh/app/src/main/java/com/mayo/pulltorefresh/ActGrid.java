package com.mayo.pulltorefresh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mayo.pulltorefresh.gridview.RefreshLoadGridView;
import com.mayo.pulltorefresh.listview.LoadingView;
import com.mayo.pulltorefresh.listview.RefreshingView;

import static android.R.color.darker_gray;


public class ActGrid extends Activity {

    //private static final String TAG = "PullToRefresh";
    String[] adapterData = new String[]{"A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private Context mContext;

    private BaseAdapter mAdapter = new BaseAdapter() {
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
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, 50)));
            textView.setText(adapterData[position]);
            textView.setTextSize(20);
            textView.setTextColor(0xff000000);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(darker_gray);
            textView.setPadding(50, 0, 0, 0);

            return textView;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_grid);

        mContext = this;
        final RefreshLoadGridView refreshLoadGridView = (RefreshLoadGridView) findViewById(R.id.gridView);
        final RefreshingView refreshingView = (RefreshingView) findViewById(R.id.pullDownView);
        final LoadingView loadingView = (LoadingView) findViewById(R.id.pullUpView);

        final RotateAnimation rotate = new RotateAnimation(0, 300);
        rotate.setDuration(500);

        refreshLoadGridView.getGridView().setAdapter(mAdapter);

        refreshLoadGridView.setOnPullHeightChangeListener(new RefreshLoadGridView.OnPullHeightChangeListener() {
            @Override
            public void onTopHeightChange(int headerHeight, int pullHeight) {
                float progress = (float) pullHeight
                        / (float) headerHeight;

                if (progress < 0.5) {
                    progress = 0.0f;
                } else {
                    progress = (progress - 0.5f) / 0.5f;
                }


                if (progress > 1.0f) {
                    progress = 1.0f;
                }

                if (!refreshLoadGridView.isRefreshing()) {
                    refreshingView.setProgress(progress);
                }
            }

            @Override
            public void onBottomHeightChange(int footerHeight, int pullHeight) {
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

                if (!refreshLoadGridView.isRefreshing()) {
                    loadingView.setProgress(progress);
                }

            }

            @Override
            public void onRefreshing(final boolean isTop) {
                if (isTop) {
                    refreshingView.startAnimate();
                } else {
                    loadingView.startAnimate();
                }

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //once, the loading is done, pullup the listview
                        refreshLoadGridView.pullUp();
                        if (isTop) {
                            refreshingView.stopAnimate();
                        } else {
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
