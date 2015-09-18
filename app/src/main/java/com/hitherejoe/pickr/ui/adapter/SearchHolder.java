package com.hitherejoe.pickr.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.hitherejoe.pickr.R;

import timber.log.Timber;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_place)
public class SearchHolder extends ItemViewHolder<Place> {

    @ViewId(R.id.text_name)
    TextView mLocationNameText;

    @ViewId(R.id.text_description)
    TextView mLocationText;

    public SearchHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Place place, PositionInfo positionInfo) {
        mLocationNameText.setText(place.getName());
        mLocationText.setText(place.getAddress());
    }

    @Override
    public void onSetListeners() {
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationListener listener = getListener(LocationListener.class);
                Timber.e("CLICKKKKK");
                if (listener != null) listener.onLocationPress(getItem());
            }
        });
    }

    public interface LocationListener {
        void onLocationPress(Place location);
    }

}