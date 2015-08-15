package com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
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

  /*
   * @note Target shouldn't be just called because Picasso only keeps a week reference.
   * If do that, it will be garbage collected.
   * @see <a href = "https://github.com/square/picasso/issues/669">
   *   onBitmapLoaded method not called the first time</a>
   * @see <a href =
   * "http://stackoverflow.com/questions/24180805/onbitmaploaded-of-target-object-not-called-on-first-load"
   * >onBitmapLoaded is not called on first load</a>
   */
  public View getView(int position, View convertView, ViewGroup parent) {
    final ImageView imageView;
    if (convertView == null) {
      imageView = new ImageView(mContext);
      imageView.setAdjustViewBounds(true);  // Keep original aspect ratio.
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);  // Handle row and column respectively.
    } else {
      imageView = (ImageView)convertView;
    }

    // Load image and store it to data set as bitmap.
    final MovieParcelable movieParcelable = getItem(position);
    final Target target = new Target() {
      @Override
      public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d(LOG_TAG, "Picasso loading start");
      }

      @Override
      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        movieParcelable.poster = bitmap;
        imageView.setImageBitmap(bitmap);
      }

      @Override
      public void onBitmapFailed(Drawable errorDrawable) {
        Log.e(LOG_TAG, "Picasso loading failed");
      }
    };
    imageView.setTag(target); // Target will last as long as its view is alive.
    Picasso.with(mContext).load(movieParcelable.url).into(new WeakReference<>(target).get());
    return imageView;
  }
}
