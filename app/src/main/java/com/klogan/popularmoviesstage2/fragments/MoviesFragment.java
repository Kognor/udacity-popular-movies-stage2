package com.klogan.popularmoviesstage2.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.klogan.popularmoviesstage2.BuildConfig;
import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.adapters.MovieCursorAdapter;
import com.klogan.popularmoviesstage2.data.MovieContract;
import com.klogan.popularmoviesstage2.domain.MoviePage;
import com.klogan.popularmoviesstage2.listeners.EndlessScrollListener;
import com.klogan.popularmoviesstage2.repository.MovieRepository;
import com.klogan.popularmoviesstage2.service.MovieService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment used to show Movies.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 0;

    public static String[] MOVIE_COLUMNS = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_DURATION
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    public static final int COL_MOVIE_PLOT_OVERVIEW = 4;
    public static final int COL_MOVIE_USER_RATING = 5;
    public static final int COL_MOVIE_RELEASE_DATE = 6;
    public static final int COL_MOVIE_DURATION = 7;

    @BindView(R.id.gridview_movies)
    GridView gridView;

    private MovieService movieService;

    private MovieRepository movieRepository;

    private MovieCursorAdapter movieCursorAdapter;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        movieCursorAdapter = new MovieCursorAdapter(getContext(), null, 0);
        movieService = MovieService.RETROFIT.create(MovieService.class);
        movieRepository = new MovieRepository();

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, rootView);

        gridView.setAdapter(movieCursorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri movieUri = MovieContract.MovieEntry.buildMovieUri(id);
                ((MovieSelectedCallback)getActivity()).onItemSelected(movieUri);
            }
        });

        addScrollListenerToGridView();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortByPopular = getString(R.string.pref_sort_popular);
        String sortByTopRated = getString(R.string.pref_sort_top_rated);
        String sortByFavorites = getString(R.string.pref_sort_favorites);

        String sortByPreference = getSortByPreference();

        if (sortByPreference.equals(sortByPopular)) {
            return new CursorLoader(getContext(),
                    MovieContract.PopularMovieEntry.CONTENT_URI, MOVIE_COLUMNS, null,
                    null, MovieContract.PopularMovieEntry.TABLE_NAME + "." +
                    MovieContract.PopularMovieEntry._ID + " ASC");
        } else if (sortByPreference.equals(sortByTopRated)) {
            return new CursorLoader(getContext(),
                    MovieContract.TopRatedMovieEntry.CONTENT_URI, MOVIE_COLUMNS, null,
                    null, MovieContract.TopRatedMovieEntry.TABLE_NAME + "." +
                    MovieContract.TopRatedMovieEntry._ID + " ASC");
        } else if (sortByPreference.equals(sortByFavorites)) {
            return new CursorLoader(getContext(),
                    MovieContract.FavoriteMovieEntry.CONTENT_URI, MOVIE_COLUMNS, null,
                    null, MovieContract.FavoriteMovieEntry.TABLE_NAME + "." +
                    MovieContract.FavoriteMovieEntry._ID + " ASC");
        } else {
            Log.e(LOG_TAG, "Unknown sort preference: " + sortByPreference);
        }

        return new CursorLoader(getContext(),
                MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieCursorAdapter.swapCursor(data);

        // TODO: smoothScroll here
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieCursorAdapter.swapCursor(null);
    }

    public void onSortByChanged() {
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        addScrollListenerToGridView();
    }

    private void addScrollListenerToGridView() {
        final String sortByPopular = getString(R.string.pref_sort_popular);
        final String sortByTopRated = getString(R.string.pref_sort_top_rated);
        final String sortByFavorites = getString(R.string.pref_sort_favorites);

        final String sortByPreference = getSortByPreference();
        if (sortByPreference.equals(sortByFavorites)) {
            // Don't add a scroll listener for favorites because there is no network call being
            // made to grab more data if needed.
            gridView.setOnScrollListener(null);
            return;
        }

        EndlessScrollListener endlessScrollListener = new EndlessScrollListener(new EndlessScrollListener.Callback() {
            @Override
            public boolean onLoadMore(int nextPage) {
                Log.i(LOG_TAG, "Loading more data...page=" + nextPage);

                if (sortByPreference.equals(sortByPopular)) {
                    loadMorePopularMovies(nextPage);
                } else if (sortByPreference.equals(sortByTopRated)) {
                    loadMoreTopRatedMovies(nextPage);
                } else {
                    Log.e(LOG_TAG, "Unknown sortByPreference: " + sortByPreference);
                }

                return true;
            }
        });

        gridView.setOnScrollListener(endlessScrollListener);
    }

    private String getSortByPreference() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
    }

    private void loadMorePopularMovies(int page) {
        Call<MoviePage> popularMoviePageCall = movieService
                .getPopularMovies(Integer.toString(page), BuildConfig.MOVIE_DB_API_KEY);
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
    }

    private void loadMoreTopRatedMovies(int page) {
        Call<MoviePage> topRatedMoviePageCall = movieService
                .getTopRatedMovies(Integer.toString(page), BuildConfig.MOVIE_DB_API_KEY);
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

    public interface MovieSelectedCallback {
        void onItemSelected(final Uri uri);
    }
}
