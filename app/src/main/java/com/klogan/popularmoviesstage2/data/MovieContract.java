package com.klogan.popularmoviesstage2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Movie database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.klogan.popularmoviesstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favoriteMovie";

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_POPULAR_MOVIE = "popularMovie";

    public static final String PATH_TOP_RATED_MOVIE = "topRatedMovie";

    public static class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorite_movie";

        public static final String COLUMN_TITLE = "movie_title";

        public static final String COLUMN_THEMOVIEDB_MOVIE_ID = "themoviedb_movie_id";

        public static Uri buildFavoriteMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static class PopularMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIE;

        public static final String TABLE_NAME = "popular_movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildPopularMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static class TopRatedMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIE;

        public static final String TABLE_NAME = "top_rated_movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTopRatedMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_PLOT_OVERVIEW = "plot_overview";

        public static final String COLUMN_USER_RATING = "user_rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_DURATION = "duration";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
