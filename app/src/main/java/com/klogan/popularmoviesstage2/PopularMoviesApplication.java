package com.klogan.popularmoviesstage2;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Main application class.
 */
public class PopularMoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
