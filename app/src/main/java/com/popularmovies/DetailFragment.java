package com.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
  private static final String LOG_TAG = DetailFragment.class.getSimpleName();
  private static final String MOVIE_ENDPOINT = "movie";
  private static final String REVIEW_ENDPOINT = "reviews";
  private static final String VIDEO_ENDPOINT = "videos";

  private MovieFragment mActivity = null;
  private DetailAdapter mDetailAdapter = null;
  private ArrayList<MovieParcelable> mDetailList = null;
  private MovieParcelable mMovieParcelable = null;

  public DetailFragment() {
    mActivity = new MovieFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Get extras.
    mMovieParcelable = getActivity().getIntent().getParcelableExtra(mActivity.EXTRA_KEY_MOVIE_DATA);

    // Initialize
    mDetailList = new ArrayList<>();
    mDetailList.add(0, new MovieParcelable(
        null,
        null,
        mMovieParcelable.id,
        null,
        mMovieParcelable.overview,
        mMovieParcelable.poster,
        mMovieParcelable.posterPath,
        mMovieParcelable.releaseDate,
        mMovieParcelable.title,
        mMovieParcelable.voteAverage
    ));
    mDetailAdapter = new DetailAdapter(getActivity(), mDetailList);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    // Get a reference to a list view, and attach this adapter to it.
    ListView listView = (ListView) rootView.findViewById(R.id.listview_detail);
    listView.setAdapter(mDetailAdapter);

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();

    // Check network connection before fetch data.
    NetworkInfo networkInfo = ((ConnectivityManager)getActivity()
        .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    if ((networkInfo != null) && networkInfo.isConnected()) {
      // Start background task.
      new FetchMovieDetailTask().execute();

      Log.d(LOG_TAG, "fetch data");
    } else {
      Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
    }
  }

  private class FetchMovieDetailTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(buildFetchTrailerUri()).openConnection();
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

        getTrailerDataFromJson(buffer.toString());

        // Reset last connection.
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

        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(buildFetchReviewUri()).openConnection();
        urlConnection.setRequestMethod(MovieFragment.FetchMovieTask.REQUEST_METHOD);
        urlConnection.connect();
        
        // Read the input stream.
        inputStream = urlConnection.getInputStream();
        if (inputStream == null) {
          return null;
        }
        buffer = new StringBuffer();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
          buffer.append(line + "\n");
        }
        if (buffer.length() == 0) {
          return null;
        }

        getReviewDataFromJson(buffer.toString());
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      } catch (JSONException e) {
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

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      // Refresh adapter.
      mDetailAdapter.notifyDataSetChanged();
    }
  }

  private String  buildFetchReviewUri() {
    return Uri.parse(MovieFragment.MOVIE_DB_URL)
        .buildUpon()
        .appendPath(MOVIE_ENDPOINT)
        .appendPath(mMovieParcelable.id)
        .appendPath(REVIEW_ENDPOINT)
        .appendQueryParameter(MovieFragment.QUERY_API_KEY, MovieFragment.PARAM_API_KEY)
        .build().toString();
  }

  private String  buildFetchTrailerUri() {
    return Uri.parse(MovieFragment.MOVIE_DB_URL)
        .buildUpon()
        .appendPath(MOVIE_ENDPOINT)
        .appendPath(mMovieParcelable.id)
        .appendPath(VIDEO_ENDPOINT)
        .appendQueryParameter(MovieFragment.QUERY_API_KEY, MovieFragment.PARAM_API_KEY)
        .build().toString();
  }

  private void getReviewDataFromJson(String trailerJsonStr) throws JSONException {
    /** Constants for JSON data. */
    final String JSON_KEY_AUTHOR = "author";
    final String JSON_KEY_CONTENT = "content";
    final String JSON_KEY_RESULTS = "results";

    JSONArray jsonArray = new JSONObject(trailerJsonStr).getJSONArray(JSON_KEY_RESULTS);
    for (int i = 0; i < jsonArray.length(); i++) {
      Log.d(LOG_TAG, "author " + jsonArray.getJSONObject(i).getString(JSON_KEY_AUTHOR));
    }
  }

  private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
    /** Constants for JSON data. */
    final String JSON_KEY_KEY = "key";
    final String JSON_KEY_RESULTS = "results";

    JSONArray jsonArray = new JSONObject(trailerJsonStr).getJSONArray(JSON_KEY_RESULTS);
    for (int i = 0; i < jsonArray.length(); i++) {
      Log.d(LOG_TAG, "key " + jsonArray.getJSONObject(i).getString(JSON_KEY_KEY));
    }
  }
}
