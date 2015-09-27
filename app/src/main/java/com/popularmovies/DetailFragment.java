package com.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
  private MovieFragment mActivity = null;

  public DetailFragment() {
    mActivity = new MovieFragment();
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
    ((TextView)rootView.findViewById(R.id.vote_average)).setText(movieParcelable.voteAverage + "/10");

    return rootView;
  }

  private class FetchMovieDetailTask extends AsyncTask<String, Void, Void> {
    /** Constants for building URI to call API. */
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie";
    private static final String PARAM_API_KEY = "73430ad81f5c1925ebcbb9d175381cab";
    private static final String QUERY_API_KEY = "api_key";
    private static final String REQUEST_METHOD = "GET";

    @Override
    protected Void doInBackground(String... params) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(Uri.parse(MOVIE_DB_URL)
            .buildUpon()
            .appendQueryParameter(QUERY_API_KEY, PARAM_API_KEY)
            .build().toString()).openConnection();
        urlConnection.setRequestMethod(REQUEST_METHOD);
        urlConnection.connect();

        // Read the input stream.
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
        Log.d(FetchMovieDetailTask.class.getSimpleName(), "json " + buffer);
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

      return null;
    }
  }
}
