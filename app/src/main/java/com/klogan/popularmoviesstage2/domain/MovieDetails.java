package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Details about a particular movie.
 */
@AutoValue
public abstract class MovieDetails implements Parcelable {

    @SerializedName("id")
    public abstract Integer movieId();

    @SerializedName("title")
    public abstract String title();

    @SerializedName("poster_path")
    public abstract String posterThumbnailPath();

    @SerializedName("overview")
    public abstract String plotOverview();

    @SerializedName("vote_average")
    public abstract Double userRating();

    @SerializedName("release_date")
    public abstract String releaseDate();

    @SerializedName("runtime")
    public abstract Integer durationInMinutes();

    public static TypeAdapter<MovieDetails> typeAdapter(Gson gson) {
        return new AutoValue_MovieDetails.GsonTypeAdapter(gson);
    }

}
