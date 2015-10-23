package com.hitherejoe.pickr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.hitherejoe.pickr.data.model.PointOfInterest;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.layout_name)
    LinearLayout mNameLayout;

    @Bind(R.id.text_name)
    TextView mNameText;

    @Bind(R.id.layout_address)
    LinearLayout mAddressLayout;

    @Bind(R.id.text_address)
    TextView mAddressText;

    @Bind(R.id.layout_phone_number)
    LinearLayout mPhoneNumberLayout;

    @Bind(R.id.text_phone_number)
    TextView mPhoneNumberText;

    private static final String EXTRA_POI =
            "com.hitherejoe.pickr.ui.activity.DetailActivity.EXTRA_POI";

    private PointOfInterest mPointOfInterest;

    public static Intent getStartIntent(Context context, PointOfInterest pointOfInterest) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_POI, pointOfInterest);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mPointOfInterest = getIntent().getParcelableExtra(EXTRA_POI);
        if (mPointOfInterest == null) {
            throw new IllegalArgumentException("DetailActivity requires a Location object!");
        }
        ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMapAsync(this);
        setupToolbar();
        setupLocationData();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = mPointOfInterest.latLng;
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
            actionBar.setTitle(mPointOfInterest.name);
        }
    }

    private void setupLocationData() {
        String placeName = mPointOfInterest.name;
        if (placeName != null && !placeName.isEmpty()) {
            mNameText.setText(mPointOfInterest.name);
        } else {
            mNameLayout.setVisibility(View.GONE);
        }

        String placeAddress = mPointOfInterest.address;
        if (placeAddress != null && !placeAddress.isEmpty()) {
            mAddressText.setText(placeAddress);
        } else {
            mAddressLayout.setVisibility(View.GONE);
        }

        String placePhoneNumber = mPointOfInterest.phoneNumber;
        if (placePhoneNumber != null && !placePhoneNumber.isEmpty()) {
            mPhoneNumberText.setText(placePhoneNumber);
        } else {
            mPhoneNumberLayout.setVisibility(View.GONE);
        }
    }

}