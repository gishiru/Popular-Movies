package com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

  public DetailActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    try {
      JSONObject jsonObject = new JSONObject(getActivity().getIntent()
          .getStringExtra(Intent.EXTRA_TEXT));
      Log.d("DetailActivityFragment", "original_title = " + jsonObject.getString("original_title"));
      Log.d("DetailActivityFragment", "overview = " + jsonObject.getString("overview"));
      Log.d("DetailActivityFragment", "release_date = " + jsonObject.getString("release_date"));
      Log.d("DetailActivityFragment", "vote_average = " + jsonObject.getString("vote_average"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }
}
