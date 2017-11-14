package com.themakersdirectory.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;

import com.themakersdirectory.R;
import com.themakersdirectory.entity.Branch;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by xlethal on 3/28/16.
 */
public class BranchesAdapter extends ThinCardNoImageBaseListAdapter {

    public BranchesAdapter(ArrayList arrayList) {
        list = arrayList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.nameTextView.setText(((Branch) list.get(position)).name());

        if (((Branch) list.get(position)).lat != 0 && ((Branch) list.get(position)).lng != 0) {
            holder.locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Location " + ((Branch) list.get(holder.getAdapterPosition())).name);
                    holder.view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)",
                                    ((Branch) list.get(holder.getAdapterPosition())).lat,
                                    ((Branch) list.get(holder.getAdapterPosition())).lng,
                                    ((Branch) list.get(holder.getAdapterPosition())).lat,
                                    ((Branch) list.get(holder.getAdapterPosition())).lng,
                                    ((Branch) list.get(holder.getAdapterPosition())).name()))));

                }
            });
        } else
            holder.locationButton.setVisibility(View.GONE);

        if (((Branch) list.get(position)).phone_number != null)
            holder.callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Call " + ((Branch) list.get(holder.getAdapterPosition())).name);
                    final ArrayList<Object> phone_numbers = ((Branch) list.get(holder.getAdapterPosition())).phone_number;
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
    }
}
