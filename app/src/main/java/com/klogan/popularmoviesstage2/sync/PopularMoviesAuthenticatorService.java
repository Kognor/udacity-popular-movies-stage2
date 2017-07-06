package com.klogan.popularmoviesstage2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Popular Movies Authenticator Service.
 */
public class PopularMoviesAuthenticatorService extends Service {

    private PopularMoviesAuthenticator popularMoviesAuthenticator;

    @Override
    public void onCreate() {
        popularMoviesAuthenticator = new PopularMoviesAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return popularMoviesAuthenticator.getIBinder();
    }
}
