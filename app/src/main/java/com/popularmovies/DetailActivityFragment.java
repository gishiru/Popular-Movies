package com.popularmovies;

import android.graphics.Bitmap;
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
    Bundle extras = getActivity().getIntent().getExtras();
    ((TextView)rootView.findViewById(R.id.overview))
        .setText(extras.getString(mActivity.EXTRA_KEY_OVERVIEW));
    ((ImageView) rootView.findViewById(R.id.thumbnail))
        .setImageBitmap((Bitmap) extras.getParcelable(mActivity.EXTRA_KEY_POSTER));
    ((TextView)rootView.findViewById(R.id.original_title))
        .setText(extras.getString(mActivity.EXTRA_KEY_TITLE));
    ((TextView)rootView.findViewById(R.id.release_date))
        .setText(extras.getString(mActivity.EXTRA_KEY_RELEASE_DATE));
    ((TextView)rootView.findViewById(R.id.rating))
        .setText(extras.getString(mActivity.EXTRA_KEY_VOTE_AVERAGE) + "/10");
    return rootView;
  }
}
