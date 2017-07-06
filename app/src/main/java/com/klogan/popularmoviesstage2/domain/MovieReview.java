package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Data of a movie reviewed.
 */
@AutoValue
public abstract class MovieReview implements Parcelable {

    public abstract String id();

    public abstract String author();

    public abstract String content();

    public abstract String url();

    public static TypeAdapter<MovieReview> typeAdapter(Gson gson) {
        return new AutoValue_MovieReview.GsonTypeAdapter(gson);
    }
}
