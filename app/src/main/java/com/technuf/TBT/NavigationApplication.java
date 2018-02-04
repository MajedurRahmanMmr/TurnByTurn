package com.technuf.TBT;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

public class NavigationApplication extends Application {

    private static final String LOG_TAG = NavigationApplication.class.getSimpleName();
    private static final String DEFAULT_MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoibWFqZWR1cm1tciIsImEiOiJjajJyNmV5aHEwMDNoMzJsZXlxNzgxamJ2In0.kQohKMm73fcf5_-Uc1fvGA";

    @Override
    public void onCreate() {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), DEFAULT_MAPBOX_ACCESS_TOKEN);
    }

}
