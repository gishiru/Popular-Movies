package com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
public class MovieFragment extends Fragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  /** Constants for saved instances. */
  private static final String BUNDLE_KEY_GRID_INDEX = "index";
  private static final String BUNDLE_KEY_MOVIE_LIST = "movie list";

  /** Constant for Extra. */
  static final String EXTRA_KEY_MOVIE_DATA = "movie parcelable";

  /** Log tag. */
  private static final String LOG_TAG = MovieFragment.class.getSimpleName();

  private GridView mGridView = null;
  private int mIndex = 0;
  private boolean mIsPreferenceChanged = false;
  private MovieAdapter mMovieAdapter = null;
  private ArrayList<MovieParcelable> mMovieList = null;
  private SharedPreferences mPrefs = null;

  public MovieFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize
    mMovieList = new ArrayList<>();
    mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mPrefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    // Get saved instances.
    if(savedInstanceState != null) {
      mIndex = savedInstanceState.getInt(BUNDLE_KEY_GRID_INDEX);
      mMovieList = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_MOVIE_LIST);
      Log.d(LOG_TAG, "saved index = " + mIndex);
    }

    // Initialize
    mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);

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

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();

    if ((mMovieList.size() == 0) || mIsPreferenceChanged) {
      // Check network connection before fetch data.
      NetworkInfo networkInfo = ((ConnectivityManager)getActivity()
          .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
      if ((networkInfo != null) && networkInfo.isConnected()) {
        // Start background task.
        new FetchMovieTask()
            .execute(
                // Pass preference information to AsyncTask regarding sort order.
                mPrefs.getString(getString(R.string.pref_key_sort_order),
                    getString(R.string.pref_default_sort_order)),
                null, null);

        Log.d(LOG_TAG, "fetch data");
      } else {
        Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    int index = mGridView.getFirstVisiblePosition();
    outState.putInt(BUNDLE_KEY_GRID_INDEX, index);
    outState.putParcelableArrayList(BUNDLE_KEY_MOVIE_LIST, mMovieList);
    Log.d(LOG_TAG, "index = " + index);

    // Always call the superclass so it can save the view hierarchy state.
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    // Clear
    mPrefs.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(getString(R.string.pref_key_sort_order))) {
      mIsPreferenceChanged = true;

      Log.d(LOG_TAG, "preference is changed");
    }
  }

  class FetchMovieTask extends AsyncTask<String, Void, Void> {
    /** Constants for building URI to call API. */
    static final String MOVIE_DB_URL = "http://api.themoviedb.org/3";
    private static final String DISCOVER_ENDPOINT = "discover/movie";
    static final String PARAM_API_KEY = "73430ad81f5c1925ebcbb9d175381cab";
    private static final String QUERY_SORT_BY = "sort_by";
    static final String QUERY_API_KEY = "api_key";
    static final String REQUEST_METHOD = "GET";

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

      // Clear old database info.
      mIndex = 0;
      mMovieList.clear();
    }

    @Override
    protected Void doInBackground(String... sortOrder) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection.
        urlConnection = (HttpURLConnection) new URL(Uri.parse(MOVIE_DB_URL)
            .buildUpon()
            .appendEncodedPath(DISCOVER_ENDPOINT)
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

        getMovieDataFromJson(buffer.toString());
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

    /*
     * @note setSelectionFromTop is better than setSelection.
     * @note Use post() method to wait for list refresh.
     * @see <a href =
     * "http://stackoverflow.com/questions/6942582/smoothscrolltoposition-after-notifydatasetchanged-not-working-in-android"
     * >smoothScrollToPosition after notifyDataSetChanged</a>
     */
    @Override
    protected void onPostExecute(Void result) {
      // Refresh adapter and list visible position.
      mIsPreferenceChanged = false;
      mMovieAdapter.notifyDataSetChanged();
      mGridView.post(new Runnable() {
        @Override
        public void run() {
          mGridView.setSelection(mIndex);
          Log.d(LOG_TAG, "restored index = " + mIndex);
        }
      });
    }
  }

  private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
    /** Constants for JSON data. */
    final String JSON_KEY_ID = "id";
    final String JSON_KEY_OVERVIEW = "overview";
    final String JSON_KEY_POSTER_PATH = "poster_path";
    final String JSON_KEY_RELEASE_DATE = "release_date";
    final String JSON_KEY_RESULTS = "results";
    final String JSON_KEY_TITLE = "original_title";
    final String JSON_KEY_VOTE_AVERAGE = "vote_average";

    // Parse JSON string received from API and store it to data set.
    JSONArray jsonArray = new JSONObject(movieJsonStr).getJSONArray(JSON_KEY_RESULTS);
    for (int i = 0; i < jsonArray.length(); i++) {
      mMovieList.add(i, new MovieParcelable(
          jsonArray.getJSONObject(i).getString(JSON_KEY_ID),
          jsonArray.getJSONObject(i).getString(JSON_KEY_OVERVIEW),
          null,
          jsonArray.getJSONObject(i).getString(JSON_KEY_POSTER_PATH),
          jsonArray.getJSONObject(i).getString(JSON_KEY_RELEASE_DATE),
          jsonArray.getJSONObject(i).getString(JSON_KEY_TITLE),
          jsonArray.getJSONObject(i).getString(JSON_KEY_VOTE_AVERAGE)
      ));
    }
  }
}
