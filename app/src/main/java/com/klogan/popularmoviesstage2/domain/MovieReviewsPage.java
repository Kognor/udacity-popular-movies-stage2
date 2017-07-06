package com.klogan.popularmoviesstage2.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * MovieReviewsPage
 */
@AutoValue
public abstract class MovieReviewsPage implements Parcelable {

    @SerializedName("id")
    public abstract Integer reviewId();

    @SerializedName("page")
    public abstract Integer pageNumber();

    @SerializedName("total_pages")
    public abstract Integer totalPages();

    @SerializedName("total_results")
    public abstract Integer totalResults();

    @SerializedName("results")
    public abstract List<MovieReview> movieReviews();

    public static TypeAdapter<MovieReviewsPage> typeAdapter(Gson gson) {
        return new AutoValue_MovieReviewsPage.GsonTypeAdapter(gson);
    }

}
