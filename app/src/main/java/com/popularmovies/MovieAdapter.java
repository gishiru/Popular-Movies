package com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by gishiru on 2015/07/31.
 */
public class MovieAdapter extends ArrayAdapter<MovieParcelable> {
  /** Log tag. */
  private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

  private Context mContext = null;

  public MovieAdapter(Activity c, List<MovieParcelable> movieParcelables) {
    super(c, 0, movieParcelables);

    mContext = c;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    final ImageView imageView;
    final MovieParcelable movieParcelable = getItem(position);

    if (convertView == null) {
      imageView = new ImageView(mContext);
      imageView.setAdjustViewBounds(true);  // Keep original aspect ratio.
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);  // Handle row and column respectively.
    } else {
      imageView = (ImageView)convertView;
    }

    // Load image and store it to data set as bitmap.
    Picasso.with(mContext).load(movieParcelable.url).into(
        new Target() {
          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {

          }

          @Override
          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            movieParcelable.poster = bitmap;
            imageView.setImageBitmap(bitmap);
          }

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {

          }
        }
    );
    return imageView;
  }
}
