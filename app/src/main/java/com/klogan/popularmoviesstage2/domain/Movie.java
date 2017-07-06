package com.klogan.popularmoviesstage2.domain;


import android.net.Uri;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Domain object to represent a movie. Note: an enhancement to this class would be to use
 * something like AutoValue
 */
@AutoValue
public abstract class Movie implements Parcelable {

    // Sample image: https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    private static final String IMAGE_SIZE = "w185";

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

    public String getPosterImageUrl() {
        return Uri.parse(Movie.IMAGE_BASE_URL)
                .buildUpon()
                .appendEncodedPath(Movie.IMAGE_SIZE + posterThumbnailPath())
                .toString();
    }

    public static String getPosterImageUrl(String posterThumbnailPath) {
        return Uri.parse(Movie.IMAGE_BASE_URL)
                .buildUpon()
                .appendEncodedPath(Movie.IMAGE_SIZE + posterThumbnailPath)
                .toString();
    }

    public static TypeAdapter<Movie> typeAdapter(Gson gson) {
        return new AutoValue_Movie.GsonTypeAdapter(gson);
    }
}
