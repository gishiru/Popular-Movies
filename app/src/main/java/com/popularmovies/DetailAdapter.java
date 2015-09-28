package com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gishiru on 2015/09/27.
 */
public class DetailAdapter extends ArrayAdapter<MovieParcelable> {
  private static final int VIEW_TYPE_COUNT = 2;
  private static final int VIEW_TYPE_LIST = 0;
  private static final int VIEW_TYPE_TOPVIEW = 1;

  private Context mContext = null;

  public DetailAdapter(Activity c, List<MovieParcelable> movieParcelables) {
    super(c, 0, movieParcelables);

    mContext = c;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    int viewType = getItemViewType(position);
    if (convertView != null) {
      view = convertView;
    } else  {
      if (viewType == VIEW_TYPE_LIST) {
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.listview_detail_list, parent, false);
      } else {
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.listview_detail_topview, parent, false);
      }
    }

    // Set views.
    MovieParcelable movieParcelable = getItem(position);
    if (viewType == VIEW_TYPE_LIST) {
      if (position == 1) {
        TextView trailers = (TextView) view.findViewById(R.id.trailers);
        trailers.setText("Trailers:");
        trailers.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);  // Set text size here to reuse layout.
      }
    } else {
      ((TextView)view.findViewById(R.id.overview)).setText(movieParcelable.overview);
      ((ImageView)view.findViewById(R.id.thumbnail)).setImageBitmap(movieParcelable.poster);
      ((TextView)view.findViewById(R.id.original_title)).setText(movieParcelable.title);
      ((TextView)view.findViewById(R.id.release_date)).setText(movieParcelable.releaseDate);
      ((TextView)view.findViewById(R.id.vote_average)).setText(movieParcelable.voteAverage + "/10");
    }
    return view;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_TOPVIEW : VIEW_TYPE_LIST;
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }
}
