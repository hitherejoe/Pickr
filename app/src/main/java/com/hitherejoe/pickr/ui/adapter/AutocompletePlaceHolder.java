package com.hitherejoe.pickr.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.model.PointOfInterest;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_place)
public class AutocompletePlaceHolder extends ItemViewHolder<PointOfInterest> {

    @ViewId(R.id.text_name)
    TextView mPlaceNameText;

    @ViewId(R.id.text_address)
    TextView mPlaceAddressText;

    public AutocompletePlaceHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(PointOfInterest pointOfInterest, PositionInfo positionInfo) {
        mPlaceNameText.setText(pointOfInterest.name);
        mPlaceAddressText.setText(pointOfInterest.address);
    }

    @Override
    public void onSetListeners() {
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationListener listener = getListener(LocationListener.class);
                if (listener != null) listener.onLocationPress(getItem());
            }
        });
    }

    public interface LocationListener {
        void onLocationPress(PointOfInterest pointOfInterest);
    }

}