package com.themakersdirectory.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.themakersdirectory.R;
import com.themakersdirectory.adapter.ProjectsAdapter;
import com.themakersdirectory.firebase.FirebaseDataManager;

import java.util.ArrayList;

/**
 * Created by xlethal on 4/2/16.
 */
public class ProjectsFragment extends MainListFragment {

    public ProjectsFragment() {
        pageTitle = R.string.Projects;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setAdapter(new ProjectsAdapter((ArrayList) intent.getSerializableExtra(FirebaseDataManager.PROJECT_KEY)));
            }
        };

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseDataManager.init().loadProjectsFirebase();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(FirebaseDataManager.PROJECT_KEY));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);

    }

}
