package com.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gishiru on 2015/08/15.
 */
public class MovieParcelable implements Parcelable {
  String overview = "";
  Bitmap poster = null;
  String title = "";
  String releaseDate = "";
  String rate = "";
  String url = "";

  public MovieParcelable(String overview, Bitmap poster, String title, String releaseDate,
                         String rate, String url) {
    this.overview = overview;
    this.poster = poster;
    this.title = title;
    this.releaseDate = releaseDate;
    this.rate = rate;
    this.url = url;
  }

  public MovieParcelable(Parcel in) {
    overview = in.readString();
    poster = in.readParcelable(Bitmap.class.getClassLoader());
    title = in.readString();
    releaseDate = in.readString();
    rate = in.readString();
    url = in.readString();
  }

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
      return new MovieParcelable(source);
    }
  };
}
