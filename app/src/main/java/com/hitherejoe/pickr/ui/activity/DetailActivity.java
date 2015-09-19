package com.hitherejoe.pickr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.model.Location;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.text_name)
    TextView mNameText;

    @Bind(R.id.layout_name)
    LinearLayout mNameLayout;

    private static final String EXTRA_LOCATION =
            "com.hitherejoe.pickr.ui.activity.DetailActivity.EXTRA_LOCATION";
    private Location mLocation;

    public static Intent getStartIntent(Context context, Location location) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_LOCATION, location);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mLocation = getIntent().getParcelableExtra(EXTRA_LOCATION);
        if (mLocation == null) {
            throw new IllegalArgumentException("DetailActivity requires a Location object!");
        }
        setupToolbar();
        ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMapAsync(this);
        setupLocationData();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = mLocation.latLng;
        if (latLng != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle(mLocation.name);
        }
    }

    private void setupLocationData() {
        String placeName = mLocation.name;
        if (placeName != null && !placeName.isEmpty()) {
            mNameText.setText(mLocation.name);
        } else {
            mNameLayout.setVisibility(View.GONE);
        }
    }

}