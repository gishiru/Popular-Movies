package com.popularmovies;

import android.app.Activity;
import android.content.Context;
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
  private Context mContext = null;

  public DetailAdapter(Activity c, List<MovieParcelable> movieParcelables) {
    super(c, 0, movieParcelables);

    mContext = c;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    if (convertView != null ) {
      view = convertView;
    } else  {
      view = LayoutInflater.from(mContext).inflate(R.layout.listview_detail_topview, parent, false);
    }

    // Set views.
    MovieParcelable movieParcelable = getItem(position);
    ((TextView)view.findViewById(R.id.overview)).setText(movieParcelable.overview);
    ((ImageView)view.findViewById(R.id.thumbnail)).setImageBitmap(movieParcelable.poster);
    ((TextView)view.findViewById(R.id.original_title)).setText(movieParcelable.title);
    ((TextView)view.findViewById(R.id.release_date)).setText(movieParcelable.releaseDate);
    ((TextView)view.findViewById(R.id.vote_average)).setText(movieParcelable.voteAverage + "/10");
    return view;
  }
}
