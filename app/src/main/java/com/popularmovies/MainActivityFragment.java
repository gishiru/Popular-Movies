package com.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
  private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

  private MovieAdapter mMovieAdapter = null;
  private ArrayList<String> mUrls = null;

  public MainActivityFragment() {
    mUrls = new ArrayList<>();
  }

  @Override
  public void onStart() {
    super.onStart();
    new FetchMovieTask().execute(PreferenceManager.getDefaultSharedPreferences(getActivity())
        .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_sort_order)),
        null, null);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.d("MainActivityFragment", "Start fragment");
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    mMovieAdapter = new MovieAdapter(getActivity(), mUrls);
    ((GridView) rootView.findViewById(R.id.gridview_movies)).setAdapter(mMovieAdapter);
    return rootView;
  }

  /**
   * @todo Delete API_KEY before relese.
   */
  private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {
    // Constants for building URI to call API.
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String SORT_BY = "sort_by";
    private static final String API_KEY = "api_key";
    private static final String OWN_KEY = "73430ad81f5c1925ebcbb9d175381cab";
    private static final String REQUEST_METHOD = "GET";

    // Constants for building URL to get image.
    private static final String IMAGE_DB_SCHEME = "http";
    private static final String IMAGE_DB_AUTHORITY = "image.tmdb.org";
    private static final String IMAGE_DB_PLUSPATH = "t/p";
    private static final String IMAGE_SIZE = "w185";

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mUrls.clear();
    }

    @Override
    protected ArrayList<String> doInBackground(String... sortOrder) {
      BufferedReader reader = null;
      HttpURLConnection urlConnection = null;

      try {
        // Create the request to themoviedb.org, and open the connection
        urlConnection = (HttpURLConnection) new URL(Uri.parse(MOVIE_DB_URL).buildUpon()
            .appendQueryParameter(SORT_BY, sortOrder[0]).appendQueryParameter(API_KEY, OWN_KEY)
            .build().toString()).openConnection();
        urlConnection.setRequestMethod(REQUEST_METHOD);
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

        try {
          JSONObject jsonObject = new JSONObject(buffer.toString());
          JSONArray jsonArray = jsonObject.getJSONArray("results");

          for (int i = 0; i < jsonArray.length(); i++) {
            mUrls.add(new Uri.Builder().scheme(IMAGE_DB_SCHEME).authority(IMAGE_DB_AUTHORITY)
                .path(IMAGE_DB_PLUSPATH).appendPath(IMAGE_SIZE)
                .appendEncodedPath(jsonArray.getJSONObject(i).getString("backdrop_path")).build()
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
      mMovieAdapter.notifyDataSetChanged();
    }
  }
}
