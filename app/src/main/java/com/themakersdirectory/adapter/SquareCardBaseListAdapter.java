package com.themakersdirectory.adapter;

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
public class SquareCardBaseListAdapter extends RecyclerView.Adapter<SquareCardBaseListAdapter.ViewHolder> {
    public ArrayList list;
    public static String DETAIL_EXTRA = "DETAIL_EXTRA";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView nameTextView;
        public NetworkImageView imageView;

        public ViewHolder(View v) {
            super(v);
            view = v;
            nameTextView = (TextView) v.findViewById(R.id.card_title);
            imageView = (NetworkImageView) v.findViewById(R.id.card_img);
        }
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public void addItem(Object item) {
        list.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_square_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SquareCardBaseListAdapter.ViewHolder holder, final int position) {

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
