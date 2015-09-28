package com.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gishiru on 2015/08/15.
 */
public class MovieParcelable implements Parcelable {
  /** Movie parameters */
  String author = "";
  String content = "";
  String id = "";
  String key = "";
  String overview = "";
  Bitmap poster = null;
  String posterPath = "";
  String releaseDate = "";
  String title = "";
  String voteAverage = "";

  public MovieParcelable(String author, String content, String id, String key, String overview,
                         Bitmap poster, String posterPath, String releaseDate, String title,
                         String voteAverage) {
    this.author = author;
    this.content = content;
    this.id = id;
    this.key = key;
    this.overview = overview;
    this.poster = poster;
    this.posterPath = posterPath;
    this.releaseDate = releaseDate;
    this.title = title;
    this.voteAverage = voteAverage;
  }

  /**
   * @note The reading order of the parameters must to be the same described in writeToParcel.
   * @param in
   */
  public MovieParcelable(Parcel in) {
    author = in.readString();
    content = in.readString();
    id = in.readString();
    key = in.readString();
    overview = in.readString();
    poster = in.readParcelable(Bitmap.class.getClassLoader());
    posterPath = in.readString();
    releaseDate = in.readString();
    title = in.readString();
    voteAverage = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(author);
    dest.writeString(content);
    dest.writeString(id);
    dest.writeString(key);
    dest.writeString(overview);
    dest.writeParcelable(poster, flags);
    dest.writeString(posterPath);
    dest.writeString(releaseDate);
    dest.writeString(title);
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
