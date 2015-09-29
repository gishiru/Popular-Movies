package com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gishiru on 2015/09/27.
 */
public class DetailAdapter extends ArrayAdapter<MovieParcelable> {
  private static final int VIEW_TYPE_COUNT = 3;
  private static final int VIEW_TYPE_REVIEW = 0;
  private static final int VIEW_TYPE_TRAILERS = 1;
  private static final int VIEW_TYPE_TOPVIEW = 2;

  private DetailFragment mActivity = null;
  private Context mContext = null;
  private boolean mLiked = false;

  public DetailAdapter(Activity c, List<MovieParcelable> movieParcelables, DetailFragment activity) {
    super(c, 0, movieParcelables);

    mActivity = activity;
    mContext = c;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view;
    int viewType = getItemViewType(position);
    if (convertView != null) {
      view = convertView;
    } else  {
      if (viewType == VIEW_TYPE_TOPVIEW) {
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.listview_detail_topview, parent, false);

        // Get a reference to image buttons, and set click listener.
        final ImageButton likeButton = (ImageButton)view.findViewById(R.id.like_dislike_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(DetailAdapter.class.getSimpleName(), "clicked");
            if (mLiked) {
              mLiked = false;
              likeButton.setImageResource(R.drawable.dislike_button);
            } else {
              mLiked = true;
              likeButton.setImageResource(R.drawable.like_button);
            }
          }
        });
      } else if (viewType == VIEW_TYPE_TRAILERS) {
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.listview_detail_trailers, parent, false);

        // Get a reference to image buttons, and set click listener.
        ImageButton trailerButton = (ImageButton)view.findViewById(R.id.trailer_button);
        trailerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(DetailAdapter.class.getSimpleName(), "launch trailer" + position);
          }
        });
      } else {
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.listview_detail_reviews, parent, false);
      }
    }

    // Set views.
    MovieParcelable movieParcelable = getItem(position);
    if (viewType == VIEW_TYPE_TOPVIEW) {
      ((TextView)view.findViewById(R.id.overview)).setText(movieParcelable.overview);
      ((ImageView)view.findViewById(R.id.thumbnail)).setImageBitmap(movieParcelable.poster);
      ((TextView)view.findViewById(R.id.original_title)).setText(movieParcelable.title);
      ((TextView)view.findViewById(R.id.release_date)).setText(movieParcelable.releaseDate);
      ((TextView)view.findViewById(R.id.vote_average)).setText(movieParcelable.voteAverage + "/10");
    } else if (viewType == VIEW_TYPE_TRAILERS) {
      if (position == 1) {
        TextView trailers = (TextView) view.findViewById(R.id.trailers);
        trailers.setText("Trailers:");
        trailers.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);  // Set text size here to reuse layout.
      }
      ((TextView) view.findViewById(R.id.trailer)).setText("Trailer " + position);
    } else {
      if ((mActivity.numberOfTrailers + 1) == position) {
        TextView reviews = (TextView) view.findViewById(R.id.reviews);
        reviews.setText("Reviews:");
        reviews.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);  // Set text size here to reuse layout.
      }
      ((TextView)view.findViewById(R.id.author)).setText(movieParcelable.author);
      ((TextView)view.findViewById(R.id.content)).setText('"' + movieParcelable.content + '"');
    }
    return view;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_TYPE_TOPVIEW;
    } else {
      if (mActivity.numberOfTrailers >= position) {
        return VIEW_TYPE_TRAILERS;
      } else {
        return VIEW_TYPE_REVIEW;
      }
    }
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }
}
