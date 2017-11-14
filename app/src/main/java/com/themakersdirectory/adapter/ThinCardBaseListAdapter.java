package com.themakersdirectory.adapter;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.themakersdirectory.R;

import java.util.ArrayList;

/**
 * Created by xlethal on 3/29/16.
 */
public class ThinCardBaseListAdapter extends RecyclerView.Adapter<ThinCardBaseListAdapter.ViewHolder> {
    public ArrayList list;
    public static String DETAIL_EXTRA = "DETAIL_EXTRA";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView nameTextView;
        public NetworkImageView imageView;
        public AppCompatButton callButton;
        public AppCompatButton locationButton;
        public TextView typeTextView;

        public ViewHolder(View v) {
            super(v);
            view = v;
            nameTextView = (TextView) v.findViewById(R.id.card_title);
            typeTextView = (TextView) v.findViewById(R.id.type_text);
            imageView = (NetworkImageView) v.findViewById(R.id.card_img);
            callButton = (AppCompatButton) v.findViewById(R.id.callButton);
            locationButton = (AppCompatButton) v.findViewById(R.id.locationButton);
        }
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public void addItem(Object item) {
        list.add(item);
    }

    public void addItems(ArrayList<?> items) {
        for (int i = 0; i < items.size(); i++)
            list.add(items.get(i));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ThinCardBaseListAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
