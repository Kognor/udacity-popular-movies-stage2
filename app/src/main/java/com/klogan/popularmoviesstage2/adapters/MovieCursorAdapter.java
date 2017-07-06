package com.klogan.popularmoviesstage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.domain.Movie;
import com.klogan.popularmoviesstage2.fragments.MoviesFragment;
import com.squareup.picasso.Picasso;

/**
 * Cursor Adapter for movies.
 */
public class MovieCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.grid_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String posterThumbnailPath = cursor.getString(MoviesFragment.COL_MOVIE_POSTER);
        String imageUrl = Movie.getPosterImageUrl(posterThumbnailPath);
        Picasso.with(context).load(imageUrl).into(viewHolder.posterImageView);
    }

    public static class ViewHolder {

        public final ImageView posterImageView;

        public ViewHolder(View view) {
            posterImageView = (ImageView)view;
        }
    }
}
