package com.hitherejoe.pickr.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.ui.activity.DetailActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_location)
public class LocationHolder extends ItemViewHolder<PointOfInterest> {

    @ViewId(R.id.text_name)
    TextView mLocationNameText;

    public LocationHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(PointOfInterest pointOfInterest, PositionInfo positionInfo) {
        mLocationNameText.setText(pointOfInterest.name);
    }

    @Override
    public void onSetListeners() {
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(DetailActivity.getStartIntent(getContext(), getItem()));
            }
        });
        getView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LocationListener listener = getListener(LocationListener.class);
                if (listener != null) listener.onLocationLongPress(getItem());
                return false;
            }
        });
    }

    public interface LocationListener {
        void onLocationLongPress(PointOfInterest pointOfInterest);
    }

}