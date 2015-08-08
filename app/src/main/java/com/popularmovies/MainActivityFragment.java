package com.popularmovies;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;

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

  /** Constants for JSON data. */
  private static final String JSON_KEY_OVERVIEW = "overview";
  private static final String JSON_KEY_POSTER_PATH = "poster_path";
  private static final String JSON_KEY_TITLE = "original_title";
  private static final String JSON_KEY_RELEASE_DATE = "release_date";
  private static final String JSON_KEY_RESULTS = "results";
  private static final String JSON_KEY_VOTE_AVERAGE = "vote_average";

  /** Constants for Extras. */
  private static final String EXTRA_KEY_OVERVIEW = "movie overview";
  private static final String EXTRA_KEY_POSTER = "movie poster";
  private static final String EXTRA_KEY_TITLE = "movie title";
  private static final String EXTRA_KEY_RELEASE_DATE = "movie release date";
  private static final String EXTRA_KEY_VOTE_AVERAGE = "movie rate";

  private JSONArray mJsonArray = null;
  private MovieAdapter mMovieAdapter = null;
  private ArrayList<ImageView> mPoster = null;
  private ArrayList<String> mUrls = null;

  public MainActivityFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize
    mPoster = new ArrayList<>();
    mUrls = new ArrayList<>();
    mMovieAdapter = new MovieAdapter(getActivity(), mPoster, mUrls);
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
  public void onStop() {
    super.onStop();

    // Clear old database.
    mPoster.clear();
    mUrls.clear();
    mMovieAdapter.notifyDataSetChanged();  // Should be called this?
  }

  /**
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return Root view.
   * @// TODO: 2015/08/08  Review image view position. Why position add 2 to call getItem?
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    // Get a reference to grid view, set adapter and it's options.
    GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
    gridView.setAdapter(mMovieAdapter);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
          // Put extras.
          Bundle bundle = new Bundle();
          bundle.putString(EXTRA_KEY_OVERVIEW,
              mJsonArray.getJSONObject(position).getString(JSON_KEY_OVERVIEW));
          bundle.putParcelable(EXTRA_KEY_POSTER,
              ((BitmapDrawable) mMovieAdapter.getItem(position + 2).getDrawable()).getBitmap());
          bundle.putString(EXTRA_KEY_TITLE,
              mJsonArray.getJSONObject(position).getString(JSON_KEY_TITLE));
          bundle.putString(EXTRA_KEY_RELEASE_DATE,
              mJsonArray.getJSONObject(position).getString(JSON_KEY_RELEASE_DATE));
          bundle.putString(EXTRA_KEY_VOTE_AVERAGE,
              mJsonArray.getJSONObject(position).getString(JSON_KEY_VOTE_AVERAGE));

          // Start DetailActivity.
          startActivity(new Intent(getActivity(), DetailActivity.class).putExtras(bundle));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
    return rootView;
  }

  /**
   * @// TODO: 2015/08/08 Delete PARAM_API_KEY before relese.
   */
  private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {
    /** Constants for building URI to call API. */
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String QUERY_SORT_BY = "sort_by";
    private static final String QUERY_API_KEY = "api_key";
    private static final String PARAM_API_KEY = "73430ad81f5c1925ebcbb9d175381cab";
    private static final String REQUEST_METHOD = "GET";

    /** Constants for building URL to get image. */
    private static final String IMAGE_DB_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";

    @Override
    protected ArrayList<String> doInBackground(String... sortOrder) {
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
          // Parse JSON string received from API.
          JSONObject jsonObject = new JSONObject(buffer.toString());
          mJsonArray = jsonObject.getJSONArray(JSON_KEY_RESULTS);
          for (int i = 0; i < mJsonArray.length(); i++) {
            mUrls.add((new URL(
                Uri.parse(IMAGE_DB_URL)
                    .buildUpon()
                    .appendPath(IMAGE_SIZE)
                    .appendEncodedPath(mJsonArray.getJSONObject(i).getString(JSON_KEY_POSTER_PATH))
                    .toString()))
                .toString());
          }
          Log.d(LOG_TAG, "url = " + mUrls);
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
      return mUrls;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
      // Refresh adapter.
      mMovieAdapter.notifyDataSetChanged();
    }
  }
}
