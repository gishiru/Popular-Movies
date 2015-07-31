package com.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by gishiru on 2015/07/31.
 */
public class MovieAdapter extends BaseAdapter {
  private Context mContext;

  public MovieAdapter(Context c) {
    mContext = c;
  }

  public int getCount() {
    return mThumbIds.length;
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView;
    if (convertView == null) {
      imageView = new ImageView(mContext);
      imageView.setAdjustViewBounds(true);  // Keep original aspect ratio.
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);  // Handle row and column respectively.
    } else {
      imageView = (ImageView) convertView;
    }

    imageView.setImageResource(mThumbIds[position]);
    return imageView;
  }

  // references to our images
  private Integer[] mThumbIds = {
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0,
      R.drawable.sample_0, R.drawable.sample_0
  };
}
