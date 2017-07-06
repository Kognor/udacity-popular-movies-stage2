package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Movie Video data object that contains info for playing movie trailers.
 */
@AutoValue
public abstract class MovieVideo implements Parcelable {

    public abstract String id();

    public abstract String iso_639_1();

    public abstract String iso_3166_1();

    public abstract String key();

    public abstract String name();

    public abstract String site();

    public abstract Integer size();

    public abstract String type();

    public static TypeAdapter<MovieVideo> typeAdapter(Gson gson) {
        return new AutoValue_MovieVideo.GsonTypeAdapter(gson);
    }
}
