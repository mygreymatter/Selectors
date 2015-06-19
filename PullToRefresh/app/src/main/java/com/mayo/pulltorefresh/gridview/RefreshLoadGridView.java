package com.mayo.pulltorefresh.gridview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.mayo.pulltorefresh.R;


public class RefreshLoadGridView extends RelativeLayout implements
        OnScrollListener {
    static int MAX_PULL_TOP_HEIGHT;
    static int MAX_PULL_BOTTOM_HEIGHT;

    static int REFRESHING_TOP_HEIGHT;
    static int REFRESHING_BOTTOM_HEIGHT;
    RelativeLayout layoutHeader;
    RelativeLayout layoutFooter;
    boolean pullTag = false;
    OnScrollListener mOnScrollListener;
    OnPullHeightChangeListener mOnPullHeightChangeListener;
    private boolean isTop;
    private boolean isBottom;
    private boolean isRefreshing;
    private boolean isAnimation;
    private int mCurrentY = 0;
    private GridView mGridView = new GridView(getContext()) {

        int lastY = 0;

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (isAnimation || isRefreshing) {
                return super.onTouchEvent(ev);
            }
            RelativeLayout parent = (RelativeLayout) mGridView.getParent();

            int currentY = (int) ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://0
                    lastY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE: {//2
                    boolean isToBottom = currentY - lastY >= 0 ? true : false;

                    int step = Math.abs(currentY - lastY);
                    lastY = currentY;

                    if (isTop && mGridView.getTop() >= 0) {

                        if (isToBottom && mGridView.getTop() <= MAX_PULL_TOP_HEIGHT) {
                            MotionEvent event = MotionEvent.obtain(ev);
                            ev.setAction(MotionEvent.ACTION_UP);
                            super.onTouchEvent(ev);
                            pullTag = true;

                            if (mGridView.getTop() > layoutHeader.getHeight()) {
                                step = step / 2;
                            }
                            if ((mGridView.getTop() + step) > MAX_PULL_TOP_HEIGHT) {
                                mCurrentY = MAX_PULL_TOP_HEIGHT;
                                scrollTopTo(mCurrentY);
                            } else {
                                mCurrentY += step;
                                scrollTopTo(mCurrentY);
                            }
                        } else if (!isToBottom && mGridView.getTop() > 0) {
                            MotionEvent event = MotionEvent.obtain(ev);
                            ev.setAction(MotionEvent.ACTION_UP);
                            super.onTouchEvent(ev);
                            if ((mGridView.getTop() - step) < 0) {
                                mCurrentY = 0;
                                scrollTopTo(mCurrentY);
                            } else {
                                mCurrentY -= step;
                                scrollTopTo(mCurrentY);
                            }
                        } else if (!isToBottom && mGridView.getTop() == 0) {
                            if (!pullTag) {
                                return super.onTouchEvent(ev);
                            }

                        }

                        return true;
                    } else if (isBottom
                            && mGridView.getBottom() <= parent.getHeight()) {
                        if (!isToBottom && (parent.getHeight() - mGridView.getBottom()) <= MAX_PULL_BOTTOM_HEIGHT) {
                            MotionEvent event = MotionEvent.obtain(ev);
                            ev.setAction(MotionEvent.ACTION_UP);
                            super.onTouchEvent(ev);
                            pullTag = true;
                            if (parent.getHeight() - mGridView.getBottom() > layoutFooter.getHeight()) {
                                step = step / 2;
                            }

                            if ((mGridView.getBottom() - step) < (parent.getHeight() - MAX_PULL_BOTTOM_HEIGHT)) {
                                mCurrentY = -MAX_PULL_BOTTOM_HEIGHT;
                                scrollBottomTo(mCurrentY);
                            } else {
                                mCurrentY -= step;
                                scrollBottomTo(mCurrentY);
                            }
                        } else if (isToBottom && (mGridView.getBottom() < parent.getHeight())) {
                            if ((mGridView.getBottom() + step) > parent.getHeight()) {
                                mCurrentY = 0;
                                scrollBottomTo(mCurrentY);
                            } else {
                                mCurrentY += step;
                                scrollBottomTo(mCurrentY);
                            }
                        } else if (isToBottom && mGridView.getBottom() == parent.getHeight()) {
                            if (!pullTag) {
                                return super.onTouchEvent(ev);
                            }
                        }
                        return true;
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL://3
                case MotionEvent.ACTION_UP://1
                    pullTag = false;

                    if (mGridView.getTop() > 0) {
                        if (mGridView.getTop() > REFRESHING_TOP_HEIGHT) {
                            animateTopTo(layoutHeader.getMeasuredHeight());
                            isRefreshing = true;
                            if (null != mOnPullHeightChangeListener) {
                                mOnPullHeightChangeListener.onRefreshing(true);
                            }
                        } else {
                            animateTopTo(0);
                        }

                    } else if (mGridView.getBottom() < parent.getHeight()) {
                        if ((parent.getHeight() - mGridView.getBottom()) > REFRESHING_BOTTOM_HEIGHT) {
                            animateBottomTo(-layoutFooter.getMeasuredHeight());
                            isRefreshing = true;
                            if (null != mOnPullHeightChangeListener) {
                                mOnPullHeightChangeListener.onRefreshing(false);
                            }
                        } else {
                            animateBottomTo(0);
                        }
                    }

            }


            return super.onTouchEvent(ev);
        }

    };
	
	/*public void setOnScrollListener(OnScrollListener listener){
		mOnScrollListener = listener;
	}*/

    public RefreshLoadGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setOnPullHeightChangeListener(
            OnPullHeightChangeListener listener) {
        mOnPullHeightChangeListener = listener;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void scrollBottomTo(int y) {
        mGridView.layout(mGridView.getLeft(), y, mGridView.getRight(),
                this.getMeasuredHeight() + y);
        if (null != mOnPullHeightChangeListener) {
            mOnPullHeightChangeListener.onBottomHeightChange(
                    layoutHeader.getHeight(), -y);
        }
    }

    public void animateBottomTo(final int y) {
        ValueAnimator animator = ValueAnimator.ofInt(mGridView.getBottom() - this.getMeasuredHeight(), y);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int frameValue = (Integer) animation.getAnimatedValue();
                mCurrentY = frameValue;
                scrollBottomTo(frameValue);
                if (frameValue == y) {
                    isAnimation = false;
                }
            }
        });
        isAnimation = true;
        animator.start();
    }

    public void scrollTopTo(int y) {
        mGridView.layout(mGridView.getLeft(), y, mGridView.getRight(),
                this.getMeasuredHeight() + y);
        if (null != mOnPullHeightChangeListener) {
            mOnPullHeightChangeListener.onTopHeightChange(
                    layoutHeader.getHeight(), y);
        }
    }


    public void animateTopTo(final int y) {
        ValueAnimator animator = ValueAnimator.ofInt(mGridView.getTop(), y);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int frameValue = (Integer) animation.getAnimatedValue();
                mCurrentY = frameValue;
                scrollTopTo(frameValue);
                if (frameValue == y) {
                    isAnimation = false;
                }
            }
        });
        isAnimation = true;
        animator.start();
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        REFRESHING_TOP_HEIGHT = layoutHeader.getMeasuredHeight();
        REFRESHING_BOTTOM_HEIGHT = layoutFooter.getMeasuredHeight();

        MAX_PULL_TOP_HEIGHT = REFRESHING_TOP_HEIGHT + 20;
        MAX_PULL_BOTTOM_HEIGHT = REFRESHING_BOTTOM_HEIGHT + 20;
    }

    @Override
    public void onFinishInflate() {

        mGridView.setBackgroundColor(0xffffffff);
        mGridView.setCacheColorHint(Color.TRANSPARENT);
        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mGridView.setOnScrollListener(this);
        this.addView(mGridView);

        layoutHeader = (RelativeLayout) this.findViewById(R.id.layoutHeader);
        layoutFooter = (RelativeLayout) this.findViewById(R.id.layoutFooter);


        super.onFinishInflate();
    }


    public GridView getGridView() {
        mGridView.setNumColumns(2);
        return mGridView;
    }

    public void pullUp() {
        isRefreshing = false;
        if (mGridView.getTop() > 0) {
            animateTopTo(0);
        } else if (mGridView.getBottom() < this.getHeight()) {
            animateBottomTo(0);
        }

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (mGridView.getCount() > 0) {
            if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                View lastItem = mGridView
                        .getChildAt(visibleItemCount - 1);
                if (null != lastItem) {

                    if (lastItem.getBottom() == mGridView.getHeight()) {
                        Log.e("my", lastItem.getBottom() + "");
                        isBottom = true;
                    } else {
                        isBottom = false;
                    }
                }
            } else {
                isBottom = false;
            }
        } else {
            isBottom = false;
        }

        if (mGridView.getCount() > 0) {
            if (firstVisibleItem == 0) {
                View firstItem = mGridView.getChildAt(0);
                if (null != firstItem) {
                    isTop = firstItem.getTop() == 0;
                }
            } else {
                isTop = false;
            }
        } else {
            isTop = true;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    // listener call back
    public interface OnPullHeightChangeListener {
        void onTopHeightChange(int headerHeight, int pullHeight);

        void onBottomHeightChange(int footerHeight, int pullHeight);

        void onRefreshing(boolean isTop);
    }
}
