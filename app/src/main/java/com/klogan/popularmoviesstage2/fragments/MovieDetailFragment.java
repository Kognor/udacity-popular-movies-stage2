package com.klogan.popularmoviesstage2.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klogan.popularmoviesstage2.BuildConfig;
import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.data.MovieContract;
import com.klogan.popularmoviesstage2.domain.Movie;
import com.klogan.popularmoviesstage2.domain.MovieDetails;
import com.klogan.popularmoviesstage2.domain.MovieReview;
import com.klogan.popularmoviesstage2.domain.MovieReviewsPage;
import com.klogan.popularmoviesstage2.domain.MovieVideo;
import com.klogan.popularmoviesstage2.domain.MovieVideos;
import com.klogan.popularmoviesstage2.service.MovieService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment that displays Movie Detail information.
 */
public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    private static final String YOUTUBE_SITE = "YouTube";

    private static final String TRAILER_TYPE = "Trailer";

    public static final String DETAIL_URI = "URI";

    public static final int MOVIE_DETAIL_LOADER = 300;

    public static String[] DETAIL_COLUMNS = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
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

    private Uri movieUri;

    private MovieService movieService;

    private List<MovieVideo> movieVideoList;

    @BindView(R.id.movie_detail_duration)
    TextView durationTextView;

    @BindView(R.id.movie_detail_favorite_button)
    ImageView favoriteButton;

    @BindView(R.id.movie_detail_trailer_container)
    LinearLayout movieTrailerContainer;

    @BindView(R.id.movie_detail_review_container)
    LinearLayout movieReviewContainer;

    @BindView(R.id.movie_detail_title)
    TextView movieTitleTextView;

    @BindView(R.id.movie_detail_poster_image)
    ImageView movieImageView;

    @BindView(R.id.movie_detail_release_date)
    TextView movieReleaseDateTextView;

    @BindView(R.id.movie_detail_user_rating)
    TextView userRatingTextView;

    @BindView(R.id.movie_detail_plot)
    TextView plotOverviewTextView;

    public MovieDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieUri = arguments.getParcelable(DETAIL_URI);
        }

        movieService = MovieService.RETROFIT.create(MovieService.class);

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteMovie();
            }
        });

        if (movieUri != null) {
            rootView.setVisibility(View.VISIBLE);
        } else {
            rootView.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void retrieveMovieDetails(Integer movieId) {
        Call<MovieDetails> movieDetailsCall = movieService.getMovieDetails(movieId,
                BuildConfig.MOVIE_DB_API_KEY);

        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                MovieDetails movieDetails = response.body();
                if (movieDetails == null) {
                    Log.e(LOG_TAG, "MovieDetails not found");
                    return;
                }

                durationTextView.setText(getString(R.string.duration, movieDetails.durationInMinutes()));
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e(LOG_TAG, "Error getting movie details", t);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (movieUri == null) {
            if (getView() != null) {
                getView().setVisibility(View.INVISIBLE);
            }
            return null;
        }
        return new CursorLoader(getActivity(), movieUri, DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            if (getView() != null && getView().getVisibility() != View.VISIBLE) {
                getView().setVisibility(View.VISIBLE);
            }

            Integer movieId = data.getInt(COL_MOVIE_ID);
            String movieTitle = data.getString(COL_MOVIE_TITLE);
            String moviePosterImageUrl = data.getString(COL_MOVIE_POSTER);
            String releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
            Double userRating = data.getDouble(COL_MOVIE_USER_RATING);
            String plotOverview = data.getString(COL_MOVIE_PLOT_OVERVIEW);
            String releaseYearString = releaseDate.substring(0, 4);
            String userRatingString = String.format(Locale.getDefault(), "%.1f/10", userRating);
            Boolean isFavorited = isFavorited(movieId);

            Picasso.with(getActivity()).load(Movie.getPosterImageUrl(moviePosterImageUrl)).into(movieImageView);
            movieTitleTextView.setText(movieTitle);
            movieReleaseDateTextView.setText(releaseYearString);
            userRatingTextView.setText(userRatingString);
            plotOverviewTextView.setText(plotOverview);
            favoriteButton.setSelected(isFavorited);

            retrieveMovieDetails(movieId);
            retrieveMovieVideos(movieId);
            retrieveMovieReviews(movieId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onSortByChanged() {
        this.movieUri = null;
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    private void retrieveMovieVideos(Integer movieId) {

        Call<MovieVideos> movieVideosCall = movieService.getMovieVideos(movieId,
                BuildConfig.MOVIE_DB_API_KEY);

        movieVideosCall.enqueue(new Callback<MovieVideos>() {

            @Override
            public void onResponse(Call<MovieVideos> call, Response<MovieVideos> response) {

                MovieVideos movieVideos = response.body();
                if (movieVideos == null) {
                    return;
                }

                movieVideoList = movieVideos.movieVideoList();

                while (movieTrailerContainer.getChildAt(2) != null) {
                    movieTrailerContainer.removeViewAt(2);
                }

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                for (int k = 0;k < movieVideoList.size(); ++k) {

                    final MovieVideo movieVideo = movieVideoList.get(k);

                    // Skip trailers that are not YouTube
                    // Also skip video types that are not "Trailer"
                    if (!YOUTUBE_SITE.equals(movieVideo.site()) || !TRAILER_TYPE.equals(movieVideo.type())) {
                        continue;
                    }

                    final View view = layoutInflater.inflate(R.layout.list_item_movie_video, movieTrailerContainer, false);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieVideo.key()));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(YOUTUBE_BASE_URL + movieVideo.key()));
                            try {
                                getContext().startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                getContext().startActivity(webIntent);
                            }
                        }
                    });
                    TextView textView = (TextView)view.findViewById(R.id.list_item_movie_trailer_text_view);
                    textView.setText(movieVideo.name());
                    movieTrailerContainer.addView(view);
                }
            }

            @Override
            public void onFailure(Call<MovieVideos> call, Throwable t) {
                Log.e(LOG_TAG, "Error getting videos/trailers", t);
            }
        });
    }

    private void retrieveMovieReviews(Integer movieId) {
        final Call<MovieReviewsPage> movieReviewsCall = movieService
                .getMovieReviewsPage(movieId, "1", BuildConfig.MOVIE_DB_API_KEY);

        movieReviewsCall.enqueue(new Callback<MovieReviewsPage>() {
            @Override
            public void onResponse(Call<MovieReviewsPage> call, Response<MovieReviewsPage> response) {
                MovieReviewsPage movieReviewsPage = response.body();

                if (movieReviewsPage == null) {
                    return;
                }

                List<MovieReview> movieReviewList = movieReviewsPage.movieReviews();

                while (movieReviewContainer.getChildAt(2) != null) {
                    movieReviewContainer.removeViewAt(2);
                }

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                for (int k = 0;k < movieReviewList.size(); ++k) {
                    MovieReview movieReview = movieReviewList.get(k);
                    final View view = layoutInflater.inflate(R.layout.list_item_movie_review, movieReviewContainer, false);
                    TextView authorTextView = (TextView)view.findViewById(R.id.movie_detail_review_author);
                    TextView contentTextView = (TextView)view.findViewById(R.id.movie_detail_review_content);

                    authorTextView.setText(movieReview.author());
                    contentTextView.setText(movieReview.content());
                    movieReviewContainer.addView(view);
                }

            }

            @Override
            public void onFailure(Call<MovieReviewsPage> call, Throwable t) {
                Log.e(LOG_TAG, "Failure getting reviews", t);
            }
        });
    }

    private void toggleFavoriteMovie() {
        Integer theMovieDbMovieId = getTheMovieDbMovieId();
        boolean isFavorited = isFavorited(theMovieDbMovieId);

        // Changing isFavorited to be the opposite of what it previously was.
        favoriteButton.setSelected(!isFavorited);

        if (isFavorited) {
            // delete from DB
            getContext().getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    MovieContract.FavoriteMovieEntry.COLUMN_THEMOVIEDB_MOVIE_ID + " = ?",
                    new String[]{Integer.toString(theMovieDbMovieId)});
        } else {
            // insert into DB
            ContentValues favoriteContentValues = new ContentValues();
            favoriteContentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_THEMOVIEDB_MOVIE_ID, theMovieDbMovieId);
            getContext().getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    favoriteContentValues);
        }
    }

    private boolean isFavorited(Integer theMovieDbMovieId) {
        if (theMovieDbMovieId == null) {
            return false;
        }

        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteMovieEntry.TABLE_NAME + "." + MovieContract.FavoriteMovieEntry._ID},
                MovieContract.FavoriteMovieEntry.COLUMN_THEMOVIEDB_MOVIE_ID + " = ?",
                new String[]{Long.toString(theMovieDbMovieId)},
                null, null);
        if (cursor == null) {
            return false;
        }

        boolean isFavorited = false;
        if (cursor.moveToFirst()) {
            isFavorited = true;
        }
        cursor.close();
        return isFavorited;
    }

    private Integer getTheMovieDbMovieId() {
        if (movieUri == null) {
            return null;
        }

        Cursor movieCursor = getContext().getContentResolver().query(movieUri,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID}, null, null, null);
        if (movieCursor == null) {
            return null;
        }
        Integer theMovieDbMovieId = null;
        if (movieCursor.moveToFirst()) {
            int colIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            theMovieDbMovieId = movieCursor.getInt(colIndex);
        }
        movieCursor.close();

        return theMovieDbMovieId;
    }
}
