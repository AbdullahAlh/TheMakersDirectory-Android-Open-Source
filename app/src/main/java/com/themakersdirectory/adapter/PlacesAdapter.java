package com.themakersdirectory.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;

import com.themakersdirectory.R;
import com.themakersdirectory.activity.detail.PlaceDetailActivity;
import com.themakersdirectory.entity.Place;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by xlethal on 3/28/16.
 */
public class PlacesAdapter extends ThinCardBaseListAdapter {

    public PlacesAdapter(ArrayList arrayList) {
        list = arrayList;
    }

    @Override
    public void onBindViewHolder(final ThinCardBaseListAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.nameTextView.setText(((Place) list.get(position)).name());
        holder.typeTextView.setText(((Place) list.get(position)).type());

        holder.view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.PLACE_CARD_TAG, ((Place) getItem(holder.getAdapterPosition())).name);
                Intent intent = new Intent(v.getContext().getApplicationContext(), PlaceDetailActivity.class);
                intent.putExtra(DETAIL_EXTRA, (Serializable) getItem(holder.getAdapterPosition()));
                holder.view.getContext().startActivity(intent);
            }
        });

        if (((Place) list.get(position)).lat != 0 && ((Place) list.get(position)).lng != 0)
            holder.locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Location " + ((Place) getItem(holder.getAdapterPosition())).name);
                    holder.view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)",
                                    ((Place) list.get(holder.getAdapterPosition())).lat,
                                    ((Place) list.get(holder.getAdapterPosition())).lng,
                                    ((Place) list.get(holder.getAdapterPosition())).lat,
                                    ((Place) list.get(holder.getAdapterPosition())).lng,
                                    ((Place) list.get(holder.getAdapterPosition())).name()))));

                }
            });
        else
            holder.locationButton.setVisibility(View.GONE);

        if (((Place) list.get(position)).phone_number != null)
            holder.callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Call " + ((Place) getItem(holder.getAdapterPosition())).name);
                    final ArrayList<Object> phone_numbers = ((Place) list.get(holder.getAdapterPosition())).phone_number;

                    if (phone_numbers.size() > 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(R.string.choose_number_to_call);
                        ArrayAdapter<Object> adapter = new ArrayAdapter<>(v.getContext(),
                                android.R.layout.simple_selectable_list_item, phone_numbers);
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String phone = ((String) phone_numbers.get(which)).split(" ")[0];
                                holder.nameTextView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else {
                        String phone = (String) phone_numbers.get(0);
                        phone = phone.split(" ")[0];
                        holder.view.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));

                    }
                }
            });
        else
            holder.callButton.setVisibility(View.GONE);

        FirebaseStorageManager.init().getImageFromUrl(((Place) list.get(position)).img, holder.imageView);
    }
}
