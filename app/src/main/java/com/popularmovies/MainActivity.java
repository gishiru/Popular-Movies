package com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callback {
  private static final String DETAIL_FRAGMENT_TAG = "DETAIL";
  private boolean mTwoPain = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Tablet UI.
    if (findViewById(R.id.movie_detail_container) != null) {
      mTwoPain = true;
      Log.d(MainActivity.class.getSimpleName(), "this is tablet.");
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
            .add(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
            .commit();
      }
    } else {
      mTwoPain = false;
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      // Start settings activity.
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onItemSelected(MovieParcelable movieParcelable) {
    Log.d(MainActivity.class.getSimpleName(), "item selected");
    if (mTwoPain) {
      Bundle bundle = new Bundle();
      bundle.putParcelable(MovieFragment.EXTRA_KEY_MOVIE_DATA, movieParcelable);
      DetailFragment fragment = new DetailFragment();
      fragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG).commit();
    } else {
      startActivity(new Intent(this, DetailActivity.class)
          .putExtra(MovieFragment.EXTRA_KEY_MOVIE_DATA, movieParcelable));
    }
  }
}
