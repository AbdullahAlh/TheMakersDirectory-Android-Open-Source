package com.themakersdirectory.adapter;

import android.content.Intent;
import android.view.View;

import com.themakersdirectory.R;
import com.themakersdirectory.activity.detail.ThingDetailActivity;
import com.themakersdirectory.entity.Project;
import com.themakersdirectory.entity.Thing;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.util.ArrayList;

/**
 * Created by xlethal on 3/28/16.
 */
public class ThingsAdapter extends CardBaseListAdapter {

    public ThingsAdapter(ArrayList arrayList) {
        list = arrayList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.nameTextView.setText(((Thing) list.get(position)).name());

        holder.view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thing thing = (Thing) getItem(holder.getAdapterPosition());
                FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.THING_CARD_TAG, thing.type + ": " + thing.name);
                Intent intent = new Intent(v.getContext().getApplicationContext(), ThingDetailActivity.class);
                intent.putExtra(DETAIL_EXTRA, thing);
                holder.view.getContext().startActivity(intent);
            }
        });

        FirebaseStorageManager.init().getImageFromUrl(((Thing) list.get(position)).img, holder.imageView);

    }
}
