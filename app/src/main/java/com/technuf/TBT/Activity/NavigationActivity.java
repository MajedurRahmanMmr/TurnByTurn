package com.technuf.TBT.Activity;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.utils.RouteUtils;
import com.technuf.TBT.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, ProgressChangeListener {

  private NavigationView navigationView;
  private boolean dropoffDialogShown;
  private Location lastKnownLocation;
  private List<Point> points = new ArrayList<>();

  private View spacer;
  private TextView speedWidget;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_AppCompat_NoActionBar);
    super.onCreate(savedInstanceState);
    points = (List<Point>) getIntent().getSerializableExtra(HomeActivity.BUS_TOPS_LIST);


    setContentView(R.layout.activity_navigation_view);
    navigationView = findViewById(R.id.navigationView);
    navigationView.onCreate(savedInstanceState);

    speedWidget = findViewById(R.id.speed_limit);
    spacer = findViewById(R.id.spacer);
    setSpeedWidgetAnchor(R.id.summaryBottomSheet);
    navigationView.getNavigationAsync(this);
  }

  private void setSpeedWidgetAnchor(@IdRes int res) {
    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) spacer.getLayoutParams();
    layoutParams.setAnchorId(res);

    spacer.setLayoutParams(layoutParams);

  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    navigationView.onLowMemory();
  }

  @Override
  public void onBackPressed() {
    // If the navigation view didn't need to do anything, call super
    if (!navigationView.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    navigationView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    navigationView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    navigationView.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  public void onNavigationReady() {
    navigationView.startNavigation(setupOptions(points.remove(0)));
  }

  private NavigationViewOptions setupOptions(Point origin) {
    dropoffDialogShown = false;

    NavigationViewOptions.Builder options = NavigationViewOptions.builder();
    options.navigationListener(this);
    options.progressChangeListener(this);
    options.origin(origin);
    options.destination(points.remove(0));
    options.shouldSimulateRoute(false);
    return options.build();
  }

  @Override
  public void onCancelNavigation() {
    // Navigation canceled, finish the activity
    finish();
  }

  @Override
  public void onNavigationFinished() {
    // Intentionally empty
  }

  @Override
  public void onNavigationRunning() {
    // Intentionally empty
  }

  @Override
  public void onProgressChange(Location location, RouteProgress routeProgress) {
    if (RouteUtils.isArrivalEvent(routeProgress)) {
      lastKnownLocation = location; // Accounts for driver moving after dialog was triggered
      if (!dropoffDialogShown && !points.isEmpty()) {
        showDropoffDialog();
        dropoffDialogShown = true; // Accounts for multiple arrival events
      }
    }
    setSpeed(location);
  }

  private void showDropoffDialog() {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setMessage(getString(R.string.dropoff_dialog_text));
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dropoff_dialog_positive_text),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int in) {
                navigationView.startNavigation(
                        setupOptions(Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude())));
              }
            });
    alertDialog.setCancelable(false);
    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dropoff_dialog_negative_text),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int in) {
                // Do nothing
              }
            });

    alertDialog.show();
  }

  private void setSpeed(Location location) {
    String string = String.format("%d\nMPH", (int) (location.getSpeed() * 2.2369));
    int mphTextSize = getResources().getDimensionPixelSize(R.dimen.mph_text_size);
    int speedTextSize = getResources().getDimensionPixelSize(R.dimen.speed_text_size);

    SpannableString spannableString = new SpannableString(string);
    spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
            string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

    spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
            0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

    speedWidget.setText(spannableString);
    speedWidget.setVisibility(View.VISIBLE);
  }
}
