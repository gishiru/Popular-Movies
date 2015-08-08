package com.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    try {
      // Get strings.
      JSONObject jsonObject = new JSONObject(
          getActivity().getIntent().getStringExtra("results"));
      ((TextView)rootView.findViewById(R.id.original_title))
          .setText(jsonObject.getString("original_title"));
      ((TextView)rootView.findViewById(R.id.release_date))
          .setText(jsonObject.getString("release_date"));
      ((TextView)rootView.findViewById(R.id.rating))
          .setText(jsonObject.getString("vote_average") + "/10");
      ((TextView)rootView.findViewById(R.id.overview))
          .setText(jsonObject.getString("overview"));

      // Get thumbnail.
      Picasso.with(getActivity()).load(getActivity().getIntent().getStringExtra("url"))
          .into((ImageView)rootView.findViewById(R.id.thumbnail));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return rootView;
  }
}
