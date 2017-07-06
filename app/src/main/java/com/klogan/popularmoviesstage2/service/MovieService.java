package com.klogan.popularmoviesstage2.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klogan.popularmoviesstage2.domain.MovieDetails;
import com.klogan.popularmoviesstage2.domain.MoviePage;
import com.klogan.popularmoviesstage2.domain.MovieReviewsPage;
import com.klogan.popularmoviesstage2.domain.MovieVideos;
import com.klogan.popularmoviesstage2.factory.AutoValueGsonFactory;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit 2 service for TheMovieDb API calls.
 */
public interface MovieService {

    String MOVIE_DB_API_BASE_URL = "http://api.themoviedb.org/3/";

    Gson gson = new GsonBuilder().registerTypeAdapterFactory(AutoValueGsonFactory.create())
            .create();

    GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

    Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(MOVIE_DB_API_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build();

    @GET("movie/popular")
    Call<MoviePage> getPopularMovies(@Query("page") String pageNum, @Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviePage> getTopRatedMovies(@Query("page") String pageNum, @Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<MovieDetails> getMovieDetails(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<MovieVideos> getMovieVideos(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<MovieReviewsPage> getMovieReviewsPage(@Path("id") Integer movieId,
            @Query("page") String pageNum, @Query("api_key") String apiKey);
}
