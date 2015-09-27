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
  private String mMovieId = "";

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
    mMovieId = movieParcelable.id;

    // Set views.
    ((TextView)rootView.findViewById(R.id.overview)).setText(movieParcelable.overview);
    ((ImageView)rootView.findViewById(R.id.thumbnail)).setImageBitmap(movieParcelable.poster);
    ((TextView)rootView.findViewById(R.id.original_title)).setText(movieParcelable.title);
    ((TextView)rootView.findViewById(R.id.release_date)).setText(movieParcelable.releaseDate);
    ((TextView)rootView.findViewById(R.id.vote_average)).setText(movieParcelable.voteAverage + "/10");

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();

    new FetchMovieDetailTask().execute(mMovieId);
  }

  private class FetchMovieDetailTask extends AsyncTask<String, Void, Void> {
    private static final String MOVIE_ENDPOINT = "movie";
    private static final String VIDEO_ENDPOINT = "videos";

    @Override
    protected Void doInBackground(String... movieId) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(
            Uri.parse(MovieFragment.FetchMovieTask.MOVIE_DB_URL)
                .buildUpon()
                .appendPath(MOVIE_ENDPOINT)
                .appendPath(movieId[0])
                .appendPath(VIDEO_ENDPOINT)
                .appendQueryParameter(
                    MovieFragment.FetchMovieTask.QUERY_API_KEY,
                    MovieFragment.FetchMovieTask.PARAM_API_KEY)
                .build().toString()).openConnection();
        urlConnection.setRequestMethod(MovieFragment.FetchMovieTask.REQUEST_METHOD);
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
