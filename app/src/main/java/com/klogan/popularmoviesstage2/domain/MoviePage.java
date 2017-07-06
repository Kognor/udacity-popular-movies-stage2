package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response that comes back when calling popular movies or top_rated movies API. The response is
 * paginated so this is the object representation for a page of data.
 */
@AutoValue
public abstract class MoviePage implements Parcelable {

    @SerializedName("page")
    public abstract Integer pageNumber();

    @SerializedName("total_results")
    public abstract Integer totalResults();

    @SerializedName("total_pages")
    public abstract Integer totalPages();

    @SerializedName("results")
    public abstract List<Movie> movieList();

    public static TypeAdapter<MoviePage> typeAdapter(Gson gson) {
        return new AutoValue_MoviePage.GsonTypeAdapter(gson);
    }
}
