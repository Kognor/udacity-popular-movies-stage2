package com.klogan.popularmoviesstage2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.klogan.popularmoviesstage2.data.MovieContract;
import com.klogan.popularmoviesstage2.domain.Movie;

import java.util.List;

/**
 * Movie Repository for all the movie database operations.
 */
public class MovieRepository {

    private static final String LOG_TAG = MovieRepository.class.getSimpleName();

    public void dropTopRatedMoviesTable(Context context) {
        context.getContentResolver().delete(MovieContract.TopRatedMovieEntry.CONTENT_URI, null, null);
    }

    public void dropPopularMoviesTable(Context context) {
        context.getContentResolver().delete(MovieContract.PopularMovieEntry.CONTENT_URI, null, null);
    }

    public void bulkInsertPopularMovies(Context context, List<Movie> movieList) {
        Log.i(LOG_TAG, "Adding popular movies of size: " + movieList.size());
        ContentValues[] contentValuesArray = getContentValuesFromMovieList(movieList);
        context.getContentResolver().bulkInsert(MovieContract.PopularMovieEntry.CONTENT_URI, contentValuesArray);
    }

    public void bulkInsertTopRatedMovies(Context context, List<Movie> movieList) {
        Log.i(LOG_TAG, "Adding topRated movies of size: " + movieList.size());
        ContentValues[] contentValuesArray = getContentValuesFromMovieList(movieList);
        context.getContentResolver().bulkInsert(MovieContract.TopRatedMovieEntry.CONTENT_URI, contentValuesArray);
    }

    private ContentValues[] getContentValuesFromMovieList(List<Movie> movieList) {
        ContentValues[] contentValuesArray = new ContentValues[movieList.size()];
        for (int k = 0;k < movieList.size(); ++k) {
            Movie movie = movieList.get(k);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.movieId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.posterThumbnailPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW, movie.plotOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.userRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate());
            // Skip duration since we don't have that yet.
            contentValuesArray[k] = contentValues;
        }
        return contentValuesArray;
    }

}
