package com.themakersdirectory.activity.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.themakersdirectory.R;
import com.themakersdirectory.adapter.CardBaseListAdapter;

public class MainDetailActivity extends AppCompatActivity implements ResultCallback<AppInviteInvitationResult>, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public Object object;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        object = getIntent().getSerializableExtra(CardBaseListAdapter.DETAIL_EXTRA);
        setupDetails();

        if (object != null)
            loadContent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupFab();

        setupDeepLinking();

    }

    public void setupDetails() {
        setContentView(R.layout.activity_main_detail);
        setTitle("");
    }

    public void loadContent() {

    }

    void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDeepLinking() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull AppInviteInvitationResult result) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    String getObjectStringFromDeepLink(String deepLink) {
        String[] split = deepLink.split("/");
        return split[split.length - 1];
    }

    @Override
    public void onClick(View v) {

    }
}
