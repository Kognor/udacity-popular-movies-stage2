package com.klogan.popularmoviesstage2.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.klogan.popularmoviesstage2.R;
import com.klogan.popularmoviesstage2.fragments.MovieDetailFragment;

/**
 * Detailed Activity of a Movie.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static Intent getIntent(Context context, Uri movieUri) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.setData(movieUri);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {

            Uri movieUri = getIntent().getData();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.DETAIL_URI, movieUri);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_detail_container, movieDetailFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = SettingsActivity.getIntent(this);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
