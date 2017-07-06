package com.klogan.popularmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie data such as favorites.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_SQL_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW + " TEXT," +
                MovieContract.MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL," +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_DURATION + " INTEGER);";

        final String CREATE_SQL_FAVORITE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.FavoriteMovieEntry.COLUMN_THEMOVIEDB_MOVIE_ID + " INTEGER UNIQUE NOT NULL);";

        final String CREATE_SQL_TOP_RATED_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.TopRatedMovieEntry.TABLE_NAME + " (" +
                MovieContract.TopRatedMovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                " FOREIGN KEY (" + MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ID +
                ") REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + "));";

        final String CREATE_SQL_POPULAR_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.PopularMovieEntry.TABLE_NAME + " (" +
                MovieContract.PopularMovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.PopularMovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL," +
                " FOREIGN KEY (" + MovieContract.PopularMovieEntry.COLUMN_MOVIE_ID +
                ") REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + "));";

        final String CREATE_MOVIE_ID_INDEX = "CREATE INDEX " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "_idx ON " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + ");";

        sqLiteDatabase.execSQL(CREATE_SQL_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_SQL_FAVORITE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_SQL_POPULAR_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_SQL_TOP_RATED_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_MOVIE_ID_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.PopularMovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TopRatedMovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
