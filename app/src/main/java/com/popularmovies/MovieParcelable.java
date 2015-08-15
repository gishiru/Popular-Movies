package com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gishiru on 2015/08/15.
 */
public class MovieParcelable implements Parcelable {
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

  }

  public static final Parcelable.Creator<MovieParcelable> CREATOR
      = new Parcelable.Creator<MovieParcelable>() {
    @Override
    public MovieParcelable[] newArray(int size) {
      return new MovieParcelable[size];
    }

    @Override
    public MovieParcelable createFromParcel(Parcel source) {
      return new MovieParcelable();
    }
  };
}
