package com.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
  private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

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
    private static final String movieDbUrl =
        "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=73430ad81f5c1925ebcbb9d175381cab";
    private ImageView mMovieImage;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mMovieImage = new ImageView(getActivity());
    }

    @Override
    protected ImageView doInBackground(Void... params) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection
        urlConnection = (HttpURLConnection) new URL(movieDbUrl).openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        if (inputStream == null) {
          return null;
        }
        StringBuffer buffer = new StringBuffer();
        String line;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
          buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
          return null;
        }
        Log.d(LOG_TAG, "buffer = " + buffer.toString());
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return mMovieImage;
    }
  }
}
