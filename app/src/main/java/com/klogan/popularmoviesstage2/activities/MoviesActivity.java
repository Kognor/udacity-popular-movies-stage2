package com.klogan.popularmoviesstage2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.fragments.MovieDetailFragment;
import com.klogan.popularmoviesstage2.fragments.MoviesFragment;
import com.klogan.popularmoviesstage2.sync.PopularMoviesSyncAdapter;

public class MoviesActivity extends AppCompatActivity
        implements MoviesFragment.MovieSelectedCallback {

    private static final String LOG_TAG = MoviesActivity.class.getSimpleName();

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MovieDetailFragmentTag";

    private boolean showTwoPane;

    private String sortByPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sortByPreference = getSortByPreference();

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            showTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            showTwoPane = false;
        }

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortByPreference = getSortByPreference();
        if (!sortByPreference.equals(this.sortByPreference)) {
            this.sortByPreference = sortByPreference;
            MoviesFragment moviesFragment = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (moviesFragment != null) {
                moviesFragment.onSortByChanged();
            }

            MovieDetailFragment movieDetailFragment = (MovieDetailFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
            if (movieDetailFragment != null) {
                movieDetailFragment.onSortByChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.action_settings:
                Intent settingsIntent = SettingsActivity.getIntent(this);
                startActivity(settingsIntent);
                break;
            default:
                Log.w(LOG_TAG, "Unknown selectedItemId: " + selectedItemId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(final Uri movieUri) {
        if (showTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.DETAIL_URI, movieUri);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = MovieDetailActivity.getIntent(this, movieUri);
            startActivity(intent);
        }
    }

    private String getSortByPreference() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
    }
}
