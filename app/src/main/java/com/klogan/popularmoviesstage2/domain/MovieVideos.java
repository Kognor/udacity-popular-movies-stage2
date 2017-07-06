package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Movie videos returned from TheMovieDB API. Query is /movie/{id}/videos
 */
@AutoValue
public abstract class MovieVideos implements Parcelable {

    @SerializedName("id")
    public abstract Integer movieId();

    @SerializedName("results")
    public abstract List<MovieVideo> movieVideoList();

    public static TypeAdapter<MovieVideos> typeAdapter(Gson gson) {
        return new AutoValue_MovieVideos.GsonTypeAdapter(gson);
    }
}
