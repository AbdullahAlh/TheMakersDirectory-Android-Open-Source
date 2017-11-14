package com.themakersdirectory.adapter;

import android.content.Intent;
import android.view.View;

import com.themakersdirectory.R;
import com.themakersdirectory.activity.detail.ThingDetailActivity;
import com.themakersdirectory.entity.Thing;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xlethal on 6/16/16.
 */

public class ProjectThingsSquareAdapter extends SquareCardBaseListAdapter {

    public ProjectThingsSquareAdapter(ArrayList arrayList) {
        this.list = arrayList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.nameTextView.setText(((Thing) list.get(position)).name());

        holder.view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.THING_CARD_TAG, ((Thing) getItem(holder.getAdapterPosition())).type + " " + ((Thing) getItem(holder.getAdapterPosition())).name);
                Intent intent = new Intent(v.getContext().getApplicationContext(), ThingDetailActivity.class);
                intent.putExtra(DETAIL_EXTRA, (Serializable) getItem(holder.getAdapterPosition()));
                holder.view.getContext().startActivity(intent);
            }
        });

        FirebaseStorageManager.init().getImageFromUrl(((Thing) list.get(position)).img, holder.imageView);
    }
}
