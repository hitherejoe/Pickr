package com.hitherejoe.pickr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.model.Location;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout mCollapsingToolbar;

    private static final String EXTRA_LOCATION =
            "com.hitherejoe.androidboilerplate.ui.activity.CharacterActivity.EXTRA_LOCATION";
    private Location mLocation;

    public static Intent getStartIntent(Context context, Location location) {
        Intent intent = new Intent(context, DetailActivity.class);
        //intent.putExtra(EXTRA_LOCATION, location);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        ButterKnife.bind(this);
        mLocation = getIntent().getParcelableExtra(EXTRA_LOCATION);
        if (mLocation == null) {
            throw new IllegalArgumentException("CharacterActivity requires a Character object!");
        }
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbar.setTitle(mLocation.name);
        }
    }

}