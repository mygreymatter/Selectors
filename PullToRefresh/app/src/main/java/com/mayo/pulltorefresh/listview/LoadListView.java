package com.mayo.pulltorefresh.listview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mayo.pulltorefresh.R;


public class LoadListView extends RelativeLayout implements
        OnScrollListener {
    private static final String TAG = "PullToRefresh";
	static int MAX_PULL_BOTTOM_HEIGHT;

	static int REFRESHING_BOTTOM_HEIGHT;
    RelativeLayout layoutFooter;
    boolean pullTag = false;
    OnScrollListener mOnScrollListener;
    OnPullHeightChangeListener mOnPullHeightChangeListener;
    private boolean isTop;
	private boolean isBottom;
	private boolean isRefreshing;
	private boolean isAnimation;
    private int mCurrentY = 0;
    private ListView mListView = new ListView(getContext()) {

        int lastY = 0;

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (isAnimation || isRefreshing) {
                return super.onTouchEvent(ev);
            }
            RelativeLayout parent = (RelativeLayout) mListView.getParent();

            int currentY = (int) ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://0
                    lastY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE: {//2
                    boolean isToBottom = currentY - lastY >= 0 ? true : false;

                    int step = Math.abs(currentY - lastY);
                    lastY = currentY;

                    if (isBottom
                            && mListView.getBottom() <= parent.getHeight()) {
                        if (!isToBottom && (parent.getHeight() - mListView.getBottom()) <= MAX_PULL_BOTTOM_HEIGHT) {
                            MotionEvent event = MotionEvent.obtain(ev);
                            ev.setAction(MotionEvent.ACTION_UP);
                            super.onTouchEvent(ev);
                            pullTag = true;
                            if (parent.getHeight() - mListView.getBottom() > layoutFooter.getHeight()) {
                                step = step / 2;
                            }

                            if ((mListView.getBottom() - step) < (parent.getHeight() - MAX_PULL_BOTTOM_HEIGHT)) {
                                mCurrentY = -MAX_PULL_BOTTOM_HEIGHT;
                                scrollBottomTo(mCurrentY);
                            } else {
                                mCurrentY -= step;
                                scrollBottomTo(mCurrentY);
                            }
                        } else if (isToBottom && (mListView.getBottom() < parent.getHeight())) {
                            if ((mListView.getBottom() + step) > parent.getHeight()) {
                                mCurrentY = 0;
                                scrollBottomTo(mCurrentY);
                            } else {
                                mCurrentY += step;
                                scrollBottomTo(mCurrentY);
                            }
                        } else if (isToBottom && mListView.getBottom() == parent.getHeight()) {
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

                    if (mListView.getBottom() < parent.getHeight()) {
                        if ((parent.getHeight() - mListView.getBottom()) > REFRESHING_BOTTOM_HEIGHT) {
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

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
	}

    public void setOnPullHeightChangeListener(
            OnPullHeightChangeListener listener) {
        this.mOnPullHeightChangeListener = listener;
    }

	public boolean isRefreshing() {
		return this.isRefreshing;
	}

	public void scrollBottomTo(int y) {
		mListView.layout(mListView.getLeft(), y, mListView.getRight(),
				this.getMeasuredHeight()+y);
		if (null != mOnPullHeightChangeListener) {
			mOnPullHeightChangeListener.onBottomHeightChange(
                    layoutFooter.getHeight(), -y);
        }
	}
	
	public void animateBottomTo(final int y) {
		ValueAnimator animator = ValueAnimator.ofInt(mListView.getBottom()-this.getMeasuredHeight(), y);
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

	@Override 
	public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		REFRESHING_BOTTOM_HEIGHT = layoutFooter.getMeasuredHeight();
		
		MAX_PULL_BOTTOM_HEIGHT = REFRESHING_BOTTOM_HEIGHT + 20;
	}
	
	@Override
	public void onFinishInflate() {

		mListView.setBackgroundColor(0xffffffff);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mListView.setOnScrollListener(this);
		this.addView(mListView);

	layoutFooter = (RelativeLayout) this.findViewById(R.id.layoutFooter);
		
		
		super.onFinishInflate();
	}

	

	public ListView getListView() {
		return this.mListView;
	}

	public void pullUp() {
		isRefreshing = false;

        if (mListView.getBottom() < this.getHeight()) {
            animateBottomTo(0);
		}

	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if(null!=mOnScrollListener){
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
		if (mListView.getCount() > 0) {
			if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
				View lastItem = mListView
						.getChildAt(visibleItemCount - 1);
				if (null != lastItem) {

					if (lastItem.getBottom() == mListView.getHeight()) {
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

		if (mListView.getCount() > 0) {
			if (firstVisibleItem == 0) {
				View firstItem = mListView.getChildAt(0);
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
		if(null!=mOnScrollListener){
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	// listener call back
	public interface OnPullHeightChangeListener {
		void onBottomHeightChange(int footerHeight, int pullHeight);
		void onRefreshing(boolean isTop);
	}
}
