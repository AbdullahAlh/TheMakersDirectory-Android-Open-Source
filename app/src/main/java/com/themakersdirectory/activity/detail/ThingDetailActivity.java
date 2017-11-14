package com.themakersdirectory.activity.detail;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.themakersdirectory.MainActivity;
import com.themakersdirectory.R;
import com.themakersdirectory.adapter.PlacesAdapter;
import com.themakersdirectory.entity.Place;
import com.themakersdirectory.entity.Thing;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseDataManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.util.ArrayList;

/**
 * Created by xlethal on 3/30/16.
 */
public class ThingDetailActivity extends MainDetailActivity {

    @Override
    public void loadContent() {
        Thing thing = (Thing) object;
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(thing.name());
        ((TextView) findViewById(R.id.description)).setText(thing.description());

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_img);
        FirebaseStorageManager.init().getImageFromUrl(thing.img, imageView);

        final RecyclerView places_recycler = (RecyclerView) findViewById(R.id.places_recycler_view);
        places_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        thing.loadPlaces(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Place object = snapshot.getValue(Place.class);
                object.key = snapshot.getKey();

                if (object.branch != null) {
                    object.loadBranches();
                }

                if (places_recycler.getAdapter() != null) {
                    ((PlacesAdapter) places_recycler.getAdapter()).addItem(object);
                    places_recycler.getAdapter().notifyDataSetChanged();
                } else {
                    ArrayList<Place> arrayList = new ArrayList<>();
                    arrayList.add(object);
                    places_recycler.setAdapter(new PlacesAdapter(arrayList));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onResult(@NonNull AppInviteInvitationResult result) {
        if (result.getStatus().isSuccess()) {
            Intent intent = result.getInvitationIntent();
            String deepLink = AppInviteReferral.getDeepLink(intent);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    object = dataSnapshot.getValue(Thing.class);

                    if (object == null) {
                        startActivity(new Intent(ThingDetailActivity.this, MainActivity.class));
                        finish();

                    } else {
                        ((Thing) object).key = dataSnapshot.getKey();
                        loadContent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            if (deepLink.contains("material"))
                FirebaseDataManager.init().loadMaterialFirebase(getObjectStringFromDeepLink(deepLink), valueEventListener);
            else if (deepLink.contains("tool"))
                FirebaseDataManager.init().loadToolFirebase(getObjectStringFromDeepLink(deepLink), valueEventListener);

        } else {
            Log.d("THING DEEP LINKING", "getInvitation: no deep link found.");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Thing thing = (Thing) object;
        FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.FAB_BUTTON_TAG, "Share: " + thing.type + " " + thing.name);
        Intent intent = new Intent(Intent.ACTION_SEND);
        String link = String.format(getResources().getString(R.string.dynamic_link_format), (thing.isTypeMaterial()) ? "material" : "tool", thing.key);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_format), thing.name(), thing.description(), link));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.share)));
    }
}
