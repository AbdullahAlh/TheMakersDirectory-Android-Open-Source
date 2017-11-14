package com.themakersdirectory.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.themakersdirectory.R;
import com.themakersdirectory.adapter.ThingsAdapter;
import com.themakersdirectory.firebase.FirebaseDataManager;

import java.util.ArrayList;

/**
 * Created by xlethal on 4/2/16.
 */
public class ToolsFragment extends MainListFragment {

    public ToolsFragment() {
        pageTitle = R.string.Tools;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setAdapter(new ThingsAdapter((ArrayList) intent.getSerializableExtra(FirebaseDataManager.TOOL_KEY)));

            }
        };

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseDataManager.init().loadToolsFirebase();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(FirebaseDataManager.TOOL_KEY));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);

    }
}
