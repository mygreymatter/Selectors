package com.mayo.avengers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by mayo on 14/9/15.
 */
public class AvengerAdapter extends RecyclerView.Adapter<AvengerAdapter.ViewHolder> {

    private static Context mContext;

    public AvengerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.r_avenger, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v, i);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int pos) {
        viewHolder.posistion = pos;
        switch (pos) {
            case 0:
                viewHolder.picture.setImageResource(R.drawable.ic_black_widow);
                break;
            case 1:
                viewHolder.picture.setImageResource(R.drawable.ic_captain_america);
                break;
            case 2:
                viewHolder.picture.setImageResource(R.drawable.ic_hulk);
                break;
            case 3:
                viewHolder.picture.setImageResource(R.drawable.ic_iron_man);
                break;
            case 4:
                viewHolder.picture.setImageResource(R.drawable.ic_thor);
                break;
        }

        /*viewHolder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Avengers.getAvengers().position = pos;
                Intent intent = new Intent(mContext, ActAvenger.class);
                *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    String transitionName = mContext.getResources().getString(R.string.avenger);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, viewHolder.picture, transitionName);
                    mContext.startActivity(intent, options.toBundle());
                } else {*//*
                    intent.putExtra(Tags.X, v.getLeft()).
                            putExtra(Tags.Y, v.getTop()).
                            putExtra(Tags.WIDTH, v.getWidth()).
                            putExtra(Tags.HEIGHT, v.getHeight());

                    mContext.startActivity(intent);
//                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public int posistion;

        public ViewHolder(View view, final int pos) {
            super(view);
            picture = (ImageView) view.findViewById(R.id.avenger_pic);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Tags.LOG, "Top: " + v.getTop() + " " + posistion);
                    Avengers.getAvengers().position = posistion;
                    Intent intent = new Intent(mContext, ActAvenger.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        String transitionName = mContext.getResources().getString(R.string.avenger);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, picture, transitionName);
                        mContext.startActivity(intent, options.toBundle());
                    } else {
                        intent.putExtra(Tags.X, v.getLeft()).
                                putExtra(Tags.Y, v.getTop()).
                                putExtra(Tags.WIDTH, v.getWidth()).
                                putExtra(Tags.HEIGHT, v.getHeight());
                        mContext.startActivity(intent);
                    }

                    /*intent.putExtra(Tags.X, v.getLeft()).
                            putExtra(Tags.Y, v.getTop()).
                            putExtra(Tags.WIDTH, v.getWidth()).
                            putExtra(Tags.HEIGHT, v.getHeight());
                    mContext.startActivity(intent);*/
                }
            });
    }
}


}
