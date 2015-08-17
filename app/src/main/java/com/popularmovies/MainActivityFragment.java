package com.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MainActivityFragment extends Fragment {
  /** Log tag. */
  private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

  /** Constant for Extra. */
  static final String EXTRA_KEY_MOVIE_DATA = "movie parcelable";

  /** Constant for saved instance. */
  private static final String BUNDLE_KEY_GRID_INDEX = "index";

  private GridView mGridView = null;
  private int mIndex = 0;
  private MovieAdapter mMovieAdapter = null;
  private ArrayList<MovieParcelable> mMovieList = null;

  public MainActivityFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize
    mMovieList = new ArrayList<>();
    mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);
  }

  @Override
  public void onStart() {
    super.onStart();

    // Start background task.
    new FetchMovieTask().execute(
        // Pass preference information to AsyncTask regarding sort order.
        PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
            getString(R.string.pref_key_sort_order),
            getString(R.string.pref_default_sort_order)),
        null, null);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    // Get a reference to grid view, set adapter and it's options.
    mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
    mGridView.setAdapter(mMovieAdapter);
    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Start DetailActivity.
        startActivity(new Intent(getActivity(), DetailActivity.class)
            .putExtra(EXTRA_KEY_MOVIE_DATA, mMovieList.get(position)));
      }
    });

    // Get saved instances.
    if((savedInstanceState != null) && ((savedInstanceState.containsKey(BUNDLE_KEY_GRID_INDEX)))) {
      mIndex = savedInstanceState.getInt(BUNDLE_KEY_GRID_INDEX);
    }

    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(BUNDLE_KEY_GRID_INDEX, mGridView.getFirstVisiblePosition());
  }

  /**
   * @// TODO: 2015/08/08  Should be called notifyDataSetChanged return from Settings only.
   */
  @Override
  public void onStop() {
    super.onStop();

    // Clear old database.
    mMovieList.clear();
    mMovieAdapter.notifyDataSetChanged();
  }

  /**
   * @// TODO: 2015/08/08 Delete PARAM_API_KEY before release.
   */
  private class FetchMovieTask extends AsyncTask<String, Void, Void> {
    /** Constants for building URI to call API. */
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String QUERY_SORT_BY = "sort_by";
    private static final String QUERY_API_KEY = "api_key";
    private static final String PARAM_API_KEY = "73430ad81f5c1925ebcbb9d175381cab";
    private static final String REQUEST_METHOD = "GET";

    /** Constants for building URL to get image. */
    private static final String IMAGE_DB_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";

    /** Constants for JSON data. */
    private static final String JSON_KEY_OVERVIEW = "overview";
    private static final String JSON_KEY_POSTER_PATH = "poster_path";
    private static final String JSON_KEY_TITLE = "original_title";
    private static final String JSON_KEY_RELEASE_DATE = "release_date";
    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_VOTE_AVERAGE = "vote_average";

    private JSONArray jsonArray = null;

    @Override
    protected Void doInBackground(String... sortOrder) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(Uri.parse(MOVIE_DB_URL)
            .buildUpon()
            .appendQueryParameter(QUERY_SORT_BY, sortOrder[0])
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

        try {
          // Parse JSON string received from API and store it to data set.
          jsonArray = new JSONObject(buffer.toString()).getJSONArray(JSON_KEY_RESULTS);
          for (int i = 0; i < jsonArray.length(); i++) {
            mMovieList.add(i, new MovieParcelable(
                jsonArray.getJSONObject(i).getString(JSON_KEY_OVERVIEW),
                null,
                jsonArray.getJSONObject(i).getString(JSON_KEY_TITLE),
                jsonArray.getJSONObject(i).getString(JSON_KEY_RELEASE_DATE),
                jsonArray.getJSONObject(i).getString(JSON_KEY_VOTE_AVERAGE),
                (new URL(
                    Uri.parse(IMAGE_DB_URL)
                        .buildUpon()
                        .appendPath(IMAGE_SIZE)
                        .appendEncodedPath(
                            jsonArray.getJSONObject(i).getString(JSON_KEY_POSTER_PATH))
                        .toString()))
                    .toString()
            ));
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
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

    @Override
    protected void onPostExecute(Void result) {
      // Refresh adapter and list visible position.
      mMovieAdapter.notifyDataSetChanged();
      mGridView.post(new Runnable() {
        @Override
        public void run() {
          Log.d(LOG_TAG, "saved index = " + mIndex);
          mGridView.setSelection(mIndex);
        }
      });
    }
  }
}
