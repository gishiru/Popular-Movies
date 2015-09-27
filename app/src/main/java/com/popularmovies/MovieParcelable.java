package com.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gishiru on 2015/08/15.
 */
public class MovieParcelable implements Parcelable {
  /** Movie parameters */
  String id = "";
  String overview = "";
  Bitmap poster = null;
  String releaseDate = "";
  String title = "";
  String url = "";
  String voteAverage = "";

  public MovieParcelable(String id, String overview, Bitmap poster, String releaseDate, String title,
                         String url, String voteAverage) {
    this.id = id;
    this.overview = overview;
    this.poster = poster;
    this.releaseDate = releaseDate;
    this.title = title;
    this.url = url;
    this.voteAverage = voteAverage;
  }

  /**
   * @note The reading order of the parameters must to be the same described in writeToParcel.
   * @param in
   */
  public MovieParcelable(Parcel in) {
    id = in.readString();
    overview = in.readString();
    poster = in.readParcelable(Bitmap.class.getClassLoader());
    releaseDate = in.readString();
    title = in.readString();
    url = in.readString();
    voteAverage = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(overview);
    dest.writeParcelable(poster, flags);
    dest.writeString(releaseDate);
    dest.writeString(title);
    dest.writeString(url);
    dest.writeString(voteAverage);
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
