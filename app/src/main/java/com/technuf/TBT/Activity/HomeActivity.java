package com.technuf.TBT.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mapbox.geojson.Point;
import com.technuf.TBT.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public static final String BUS_TOPS_LIST = "BusStopsList";
    List<Point> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        /** Todo
         * Need to replace add Bus stops LatLng here
         */

        points.add(Point.fromLngLat(-77.2673379, 39.1751199));
        points.add(Point.fromLngLat(-77.2677795, 39.1775459));
        points.add(Point.fromLngLat(-77.2660765, 39.1776911));
        points.add(Point.fromLngLat(-77.26183, 39.1831462));



        findViewById(R.id.start_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NavigationActivity.class).putExtra(BUS_TOPS_LIST, (Serializable) points));
            }
        });
    }
}
