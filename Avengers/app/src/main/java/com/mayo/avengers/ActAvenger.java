package com.mayo.avengers;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ActAvenger extends AppCompatActivity {

    private ImageView picture;
    private float scaleX;
    private float scaleY;
    private Bundle args;
    private float posX;
    private float posY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_avenger);

        picture = (ImageView) findViewById(R.id.avenger_pic_big);

        switch (Avengers.getAvengers().position) {
            case 0:
                picture.setImageResource(R.drawable.ic_black_widow);
                break;
            case 1:
                picture.setImageResource(R.drawable.ic_captain_america);
                break;
            case 2:
                picture.setImageResource(R.drawable.ic_hulk);
                break;
            case 3:
                picture.setImageResource(R.drawable.ic_iron_man);
                break;
            case 4:
                picture.setImageResource(R.drawable.ic_thor);
                break;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            args = getIntent().getExtras();
            posX = args.getInt(Tags.X);
            posY = args.getInt(Tags.Y);

            ViewTreeObserver observer = picture.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    picture.getViewTreeObserver().removeOnPreDrawListener(this);

                    scaleX = (float) args.getInt(Tags.WIDTH) / picture.getWidth();
                    scaleY = (float) args.getInt(Tags.HEIGHT) / picture.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }

        /*ViewTreeObserver observer = picture.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                picture.getViewTreeObserver().removeOnPreDrawListener(this);

                scaleX = (float) args.getInt(Tags.WIDTH) / picture.getWidth();
                scaleY = (float) args.getInt(Tags.HEIGHT) / picture.getHeight();

                runEnterAnimation();

                return true;
            }
        });*/

    }

    private void runEnterAnimation() {

        picture.setPivotX(0);
        picture.setPivotY(0);
        picture.setScaleX(scaleX);
        picture.setScaleY(scaleY);
        picture.setTranslationX(posX);
        picture.setTranslationY(posY);


        picture.animate().setDuration(1000).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            runExitAnimation();
        }else
            super.onBackPressed();
    }

    private void runExitAnimation() {

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            picture.animate().setDuration(1000).
                    scaleX(scaleX).scaleY(scaleY).
                    translationX(posX).translationY(posY).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        /*} else {
            picture.animate().setDuration(1000).
                    scaleX(scaleX).scaleY(scaleY).
                    translationX(posX).translationY(posY).withEndAction(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }*/
    }
}