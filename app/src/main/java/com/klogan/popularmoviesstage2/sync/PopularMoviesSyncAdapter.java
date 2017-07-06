package com.klogan.popularmoviesstage2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.klogan.popularmoviesstage2.BuildConfig;
import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.domain.MoviePage;
import com.klogan.popularmoviesstage2.repository.MovieRepository;
import com.klogan.popularmoviesstage2.service.MovieService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SyncAdapter for movies.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();

    private static final int SYNC_INTERVAL = 60 * 180;

    private static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;

    private MovieService movieService;

    private MovieRepository movieRepository;

    ContentResolver contentResolver;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
        movieService = MovieService.RETROFIT.create(MovieService.class);
        movieRepository = new MovieRepository();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {

        Log.i(LOG_TAG, "onPerformSync() is called");

        // Do a refresh of data for topRated and popular movies.
        movieRepository.dropTopRatedMoviesTable(getContext());
        movieRepository.dropPopularMoviesTable(getContext());

        // Get both popular movies and topRated movies since both can be selected
        Call<MoviePage> popularMoviePageCall = movieService
                .getPopularMovies("1", BuildConfig.MOVIE_DB_API_KEY);
        Call<MoviePage> topRatedMoviePageCall = movieService
                .getTopRatedMovies("1", BuildConfig.MOVIE_DB_API_KEY);

        popularMoviePageCall.enqueue(new Callback<MoviePage>() {
            @Override
            public void onResponse(Call<MoviePage> call, Response<MoviePage> response) {
                MoviePage moviePage = response.body();
                if (moviePage == null) {
                    return;
                }
                movieRepository.bulkInsertPopularMovies(getContext(), moviePage.movieList());
            }

            @Override
            public void onFailure(Call<MoviePage> call, Throwable t) {
                // Handle Failure
                Log.w(LOG_TAG, "Problem getting movies", t);
            }
        });

        topRatedMoviePageCall.enqueue(new Callback<MoviePage>() {
            @Override
            public void onResponse(Call<MoviePage> call, Response<MoviePage> response) {
                MoviePage moviePage = response.body();
                if (moviePage == null) {
                    return;
                }
                movieRepository.bulkInsertTopRatedMovies(getContext(), moviePage.movieList());
            }

            @Override
            public void onFailure(Call<MoviePage> call, Throwable t) {
                // Handle Failure
                Log.w(LOG_TAG, "Problem getting movies", t);
            }
        });

    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account account = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(account) == null) {
            // account doesn't exist

            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(account, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(account, context);
        }

        return account;
    }

    private static void onAccountCreated(Account account, Context context) {
        // An account has been created, so set sync periodically
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEX_TIME);

        // Set Sync to happen automatically
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);

        // sync to get things started
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
