package com.klogan.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * MovieProvider for accessing Movie data.
 */
public class MovieProvider extends ContentProvider {

    private static final int FAVORITE_CODE = 100;

    private static final int MOVIE_CODE = 200;

    private static final int MOVIE_CODE_WITH_ID = 201;

    private static final int TOP_RATED_MOVIE_CODE = 300;

    private static final int POPULAR_MOVIE_CODE = 400;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder favoritedMoviesQueryBuilder;

    private static final SQLiteQueryBuilder topRatedMoviesQueryBuilder;

    private static final SQLiteQueryBuilder popularMoviesQueryBuilder;

    static {
        favoritedMoviesQueryBuilder = new SQLiteQueryBuilder();
        favoritedMoviesQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                MovieContract.FavoriteMovieEntry.TABLE_NAME +
                " ON " + MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                " = " + MovieContract.FavoriteMovieEntry.TABLE_NAME +
                "." + MovieContract.FavoriteMovieEntry.COLUMN_THEMOVIEDB_MOVIE_ID);

        topRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        topRatedMoviesQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                MovieContract.TopRatedMovieEntry.TABLE_NAME +
                " ON " + MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry._ID +
                " = " + MovieContract.TopRatedMovieEntry.TABLE_NAME +
                "." + MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ID);

        popularMoviesQueryBuilder = new SQLiteQueryBuilder();
        popularMoviesQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                MovieContract.PopularMovieEntry.TABLE_NAME +
                " ON " + MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry._ID +
                " = " + MovieContract.PopularMovieEntry.TABLE_NAME +
                "." + MovieContract.PopularMovieEntry.COLUMN_MOVIE_ID);
    }

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITE_CODE:
                cursor = favoritedMoviesQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_CODE:
                cursor = movieDbHelper.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_CODE_WITH_ID:
                cursor = getMovieCursorWithId(uri, projection, sortOrder);
                break;
            case TOP_RATED_MOVIE_CODE:
                cursor = topRatedMoviesQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POPULAR_MOVIE_CODE:
                cursor = popularMoviesQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITE_CODE:
                return MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            case MOVIE_CODE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_CODE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED_MOVIE_CODE:
                return MovieContract.TopRatedMovieEntry.CONTENT_TYPE;
            case POPULAR_MOVIE_CODE:
                return MovieContract.PopularMovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri insertUri = null;
        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        long id;
        switch (match) {
            case FAVORITE_CODE:
                id = writableDatabase.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertUri = MovieContract.FavoriteMovieEntry.buildFavoriteMovieUri(id);
                } else {
                    throw new SQLException("Failed to insert into row: " + uri);
                }
                break;
            case MOVIE_CODE:
                id = writableDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertUri = MovieContract.MovieEntry.buildMovieUri(id);
                } else {
                    throw new SQLException("Failed to insert into row: " + uri);
                }
                break;
            case TOP_RATED_MOVIE_CODE:
                id = writableDatabase.insert(MovieContract.TopRatedMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertUri = MovieContract.TopRatedMovieEntry.buildTopRatedMovieUri(id);
                } else {
                    throw new SQLException("Failed to insert into row: " + uri);
                }
                break;
            case POPULAR_MOVIE_CODE:
                id = writableDatabase.insert(MovieContract.PopularMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    insertUri = MovieContract.PopularMovieEntry.buildPopularMovieUri(id);
                } else {
                    throw new SQLException("Failed to insert into row: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        int match = uriMatcher.match(uri);
        SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        if (selection == null) {
            selection = "1";
        }
        switch (match) {
            case FAVORITE_CODE:
                rowsDeleted = writableDatabase.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case MOVIE_CODE:
                rowsDeleted = writableDatabase.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case TOP_RATED_MOVIE_CODE:
                rowsDeleted = writableDatabase.delete(MovieContract.TopRatedMovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case POPULAR_MOVIE_CODE:
                rowsDeleted = writableDatabase.delete(MovieContract.PopularMovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated;
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        switch (match) {
            case FAVORITE_CODE:
                rowsUpdated = writableDatabase.update(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case MOVIE_CODE:
                rowsUpdated = writableDatabase.update(MovieContract.MovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case MOVIE_CODE_WITH_ID:
                rowsUpdated = updateMovieWithId(uri, values);
                break;
            case TOP_RATED_MOVIE_CODE:
                rowsUpdated = writableDatabase.update(MovieContract.TopRatedMovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case POPULAR_MOVIE_CODE:
                rowsUpdated = writableDatabase.update(MovieContract.PopularMovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int numSuccessfulInserts = 0;
        switch(match) {
            case MOVIE_CODE:
                writableDatabase.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long id = writableDatabase.insertWithOnConflict(
                                MovieContract.MovieEntry.TABLE_NAME, null, contentValues,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        if (id > 0) {
                            ++numSuccessfulInserts;
                        } else if (id == -1) {
                            Integer movieId = contentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                            long updateId = writableDatabase.update(
                                    MovieContract.MovieEntry.TABLE_NAME,
                                    contentValues,
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                    new String[]{Integer.toString(movieId)});
                            if (updateId > 0) {
                                ++numSuccessfulInserts;
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } finally {
                    writableDatabase.endTransaction();
                }

                if (numSuccessfulInserts > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numSuccessfulInserts;
            case TOP_RATED_MOVIE_CODE:
                writableDatabase.beginTransaction();
                try {
                    for (ContentValues movieContentValues : values) {
                        long id = writableDatabase.insertWithOnConflict(
                                MovieContract.MovieEntry.TABLE_NAME, null, movieContentValues,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        if (id > 0) {
                            // Movie was successfully inserted into the DB. Now insert into the
                            // top_rated_movie table.
                            long topRatedId = insertIntoTopRatedTable(id);

                            if (topRatedId != 0) {
                                ++numSuccessfulInserts;
                            }
                        } else if (id == -1) {
                            // Movie already exists, so get the ID and insert into top_rated table.
                            Integer movieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                            Cursor movieCursor = writableDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                                    new String[]{MovieContract.MovieEntry._ID},
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                    new String[] {Integer.toString(movieId)}, null, null, null);
                            if (movieCursor != null && movieCursor.moveToFirst()) {
                                int idColumnIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                                long dbId = movieCursor.getLong(idColumnIndex);
                                movieCursor.close();
                                long topRatedId = insertIntoTopRatedTable(dbId);
                                if (topRatedId != 0) {
                                    ++numSuccessfulInserts;
                                }
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } finally {
                    writableDatabase.endTransaction();
                }

                if (numSuccessfulInserts > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numSuccessfulInserts;
            case POPULAR_MOVIE_CODE:
                writableDatabase.beginTransaction();
                try {
                    for (ContentValues movieContentValues : values) {
                        long id = writableDatabase.insertWithOnConflict(
                                MovieContract.MovieEntry.TABLE_NAME, null, movieContentValues,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        if (id > 0) {
                            // Movie was successfully inserted into the DB. Now insert into the
                            // top_rated table.
                            long popularId = insertIntoPopularTable(id);

                            if (popularId != 0) {
                                ++numSuccessfulInserts;
                            }
                        } else if (id == -1) {
                            // Movie already exists, so get the ID and insert into popular_movie table.
                            Integer movieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                            Cursor movieCursor = writableDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                                    new String[]{MovieContract.MovieEntry._ID},
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                    new String[] {Integer.toString(movieId)}, null, null, null);
                            if (movieCursor != null && movieCursor.moveToFirst()) {
                                int idColumnIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                                long dbId = movieCursor.getLong(idColumnIndex);
                                movieCursor.close();
                                long popularId = insertIntoPopularTable(dbId);
                                if (popularId != 0) {
                                    ++numSuccessfulInserts;
                                }
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } finally {
                    writableDatabase.endTransaction();
                }

                if (numSuccessfulInserts > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numSuccessfulInserts;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITE_CODE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE_CODE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_CODE_WITH_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TOP_RATED_MOVIE, TOP_RATED_MOVIE_CODE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_POPULAR_MOVIE, POPULAR_MOVIE_CODE);
        return uriMatcher;
    }

    private Cursor getMovieCursorWithId(Uri uri, String[] projection, String sortOrder) {
        long id = ContentUris.parseId(uri);
        String selection = MovieContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(id)};

        return movieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private int updateMovieWithId(Uri uri, ContentValues values) {
        long id = ContentUris.parseId(uri);
        String selection = MovieContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        return movieDbHelper.getWritableDatabase().update(MovieContract.MovieEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    private long insertIntoTopRatedTable(long id) {
        ContentValues topRatedContentValues = new ContentValues();
        topRatedContentValues.put(MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ID, id);
        return movieDbHelper.getWritableDatabase()
                .insertWithOnConflict(MovieContract.TopRatedMovieEntry.TABLE_NAME, null,
                        topRatedContentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private long insertIntoPopularTable(long id) {
        ContentValues popularContentValues = new ContentValues();
        popularContentValues.put(MovieContract.PopularMovieEntry.COLUMN_MOVIE_ID, id);
        return movieDbHelper.getWritableDatabase()
                .insertWithOnConflict(MovieContract.PopularMovieEntry.TABLE_NAME, null,
                        popularContentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
