package com.themakersdirectory.adapter;

import android.content.Intent;
import android.view.View;

import com.themakersdirectory.R;
import com.themakersdirectory.activity.detail.ProjectDetailActivity;
import com.themakersdirectory.entity.Project;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xlethal on 3/28/16.
 */
public class ProjectsAdapter extends CardBaseListAdapter {

    public ProjectsAdapter(ArrayList arrayList) {
        list = arrayList;
    }

    @Override
    public void onBindViewHolder(final CardBaseListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.nameTextView.setText(((Project) list.get(position)).name());

        holder.view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.PROJECT_CARD_TAG, ((Project) getItem(holder.getAdapterPosition())).name);
                Intent intent = new Intent(v.getContext().getApplicationContext(), ProjectDetailActivity.class);
                intent.putExtra(DETAIL_EXTRA, (Serializable) getItem(holder.getAdapterPosition()));
                holder.view.getContext().startActivity(intent);
            }
        });

        FirebaseStorageManager.init().getImageFromUrl(((Project) list.get(position)).img, holder.imageView);

    }
}
