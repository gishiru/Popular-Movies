package com.popularmovies;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

  public MainActivityFragment() {
  }

  @Override
  public void onStart() {
    super.onStart();
    new FetchMovieTask().execute(null, null, null);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.d("MainActivityFragment", "Start fragment");
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movies);
    gridview.setAdapter(new MovieAdapter(getActivity()));
    return rootView;
  }

  private class FetchMovieTask extends AsyncTask<Void, Void, ImageView> {
    private ImageView mMovieImage;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mMovieImage = new ImageView(getActivity());
    }

    @Override
    protected ImageView doInBackground(Void... params) {
      return mMovieImage;
    }
  }
}
