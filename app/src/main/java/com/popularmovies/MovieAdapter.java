package com.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gishiru on 2015/07/31.
 */
public class MovieAdapter extends BaseAdapter {
  private Context mContext;
  private ArrayList<String> mList = null;

  public MovieAdapter(Context c, ArrayList<String> list) {
    mContext = c;
    mList = list;
  }

  public int getCount() {
    return mList.size();
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

    // Load image.
    Picasso.with(mContext).load(mList.get(position)).into(imageView);
    return imageView;
  }
}
