package com.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
  private MainActivityFragment mActivity = null;

  public DetailActivityFragment() {
    mActivity = new MainActivityFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    // Get extras.
    MovieParcelable movieParcelable = getActivity().getIntent()
        .getParcelableExtra(mActivity.EXTRA_KEY_MOVIE_DATA);

    // Set views.
    ((TextView)rootView.findViewById(R.id.overview)).setText(movieParcelable.overview);
    ((ImageView)rootView.findViewById(R.id.thumbnail)).setImageBitmap(movieParcelable.poster);
    ((TextView)rootView.findViewById(R.id.original_title)).setText(movieParcelable.title);
    ((TextView)rootView.findViewById(R.id.release_date)).setText(movieParcelable.releaseDate);
    ((TextView)rootView.findViewById(R.id.rating)).setText(movieParcelable.voteAverage + "/10");

    return rootView;
  }
}
