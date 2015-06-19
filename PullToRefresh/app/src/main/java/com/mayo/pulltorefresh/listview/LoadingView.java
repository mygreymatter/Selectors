package com.mayo.pulltorefresh.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mayo.pulltorefresh.R;

@SuppressWarnings("deprecated")
public class LoadingView extends ImageView {
    Bitmap bitmap;
    Paint mPaint;
    float progress;
    boolean isAnimate;
    int rotateProgress;

    boolean is90;
    Handler mHandler = new Handler();
    public Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            rotateProgress += 10;
            if (rotateProgress > 360) {
                rotateProgress = 0;
            }

            if (isAnimate) {
                mHandler.postDelayed(this, 100);
            }
            LoadingView.this.invalidate();
        }

    };

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        rotateProgress = 0;
        progress = 0.0f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int minWidth = (int) (this.getWidth() * progress);
        int minHeight = (int) (this.getHeight() * progress);
        if (minWidth > 1 && minHeight > 1) {
            if (isAnimate)
                if (!is90) {
                    bitmap = getProgressBitmap();
                    is90 = true;
                } else {
                    bitmap = getProgress90Bitmap();
                    is90 = false;
                }
            else
                bitmap = getBitmap();
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();

        }
    }

    public Bitmap getBitmap() {
        Bitmap origin;
        BitmapDrawable drawable;

        if (progress >= 1.0) {
            if (Build.VERSION.SDK_INT > 21)
                drawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.down_arrow, null);
            else
                drawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.down_arrow);
            origin = drawable.getBitmap();
        } else {
            if (Build.VERSION.SDK_INT > 21)
                drawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.up_arrow, null);
            else
                drawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.up_arrow);
            origin = drawable.getBitmap();
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float scale = (float) origin.getWidth() / (float) getWidth();
        int maxWidth = (int) (origin.getWidth() / scale);
        int maxHeight = (int) (origin.getHeight() / scale);

        Bitmap temp = Bitmap.createScaledBitmap(origin, maxWidth, maxHeight, true);

        Canvas canvas = new Canvas();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(temp, (getWidth() - temp.getWidth()) / 2, (getHeight() - temp.getHeight()) / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        paint.setXfermode(null);

        temp.recycle();
        return bitmap;
    }

    private Bitmap getProgressBitmap() {
        Bitmap origin1;
        BitmapDrawable drawable1;

        if (Build.VERSION.SDK_INT > 21)
            drawable1 = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_loading, null);
        else
            drawable1 = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_loading);
        origin1 = drawable1.getBitmap();


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float scale = (float) origin1.getWidth() / (float) getWidth();
        int maxWidth = (int) (origin1.getWidth() / scale);
        int maxHeight = (int) (origin1.getHeight() / scale);


        Bitmap temp = Bitmap.createScaledBitmap(origin1, maxWidth, maxHeight, true);

        Canvas canvas = new Canvas();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(temp, (getWidth() - temp.getWidth()) / 2, (getHeight() - temp.getHeight()) / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        paint.setXfermode(null);

        temp.recycle();

        return bitmap;
    }

    private Bitmap getProgress90Bitmap() {
        Bitmap origin1;
        BitmapDrawable drawable1;

        if (Build.VERSION.SDK_INT > 21)
            drawable1 = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_loading_90, null);
        else
            drawable1 = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_loading_90);
        origin1 = drawable1.getBitmap();


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float scale = (float) origin1.getWidth() / (float) getWidth();
        int maxWidth = (int) (origin1.getWidth() / scale);
        int maxHeight = (int) (origin1.getHeight() / scale);


        Bitmap temp = Bitmap.createScaledBitmap(origin1, maxWidth, maxHeight, true);

        Canvas canvas = new Canvas();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(temp, (getWidth() - temp.getWidth()) / 2, (getHeight() - temp.getHeight()) / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        paint.setXfermode(null);

        temp.recycle();

        return bitmap;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        this.invalidate();
    }

    public void startAnimate() {
        if (!isAnimate) {
            isAnimate = true;
            mHandler.post(mRunnable);
        }

    }

    public void stopAnimate() {

        isAnimate = false;
        mHandler.removeCallbacks(mRunnable);
        rotateProgress = 0;

    }

}
