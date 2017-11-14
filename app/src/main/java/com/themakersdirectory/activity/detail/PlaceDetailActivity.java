package com.themakersdirectory.activity.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.themakersdirectory.MainActivity;
import com.themakersdirectory.R;
import com.themakersdirectory.adapter.BranchesAdapter;
import com.themakersdirectory.entity.Place;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseDataManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

/**
 * Created by xlethal on 3/30/16.
 */
public class PlaceDetailActivity extends MainDetailActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    public void setupDetails() {
        setContentView(R.layout.activity_place_detail);
    }

    @Override
    public void loadContent() {
        final Place place = (Place) object;
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(place.name());

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_img);
        FirebaseStorageManager.init().getImageFromUrl(place.img, imageView);

        if (place.lat == 0 && place.lng == 0) {
            findViewById(R.id.map_card_view).setVisibility(View.GONE);

        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        if (place.url == null) {
            findViewById(R.id.websiteButton).setVisibility(View.GONE);

        } else {
            findViewById(R.id.websiteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Website " + place.name);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(((Place) object).url));
                    startActivity(intent);
                }
            });
        }

        if (place.branch != null) {
            findViewById(R.id.branches_details_view).setVisibility(View.VISIBLE);
            RecyclerView branches_recycler = ((RecyclerView) findViewById(R.id.branches_recycler_view));
            branches_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            branches_recycler.setAdapter(new BranchesAdapter(place.branch));

        }

        if (place.phone_number != null) {
            findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Call " + place.name);
                    if (place.phone_number.size() > 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(R.string.choose_number_to_call);
                        ArrayAdapter<Object> adapter = new ArrayAdapter<>(v.getContext(),
                                android.R.layout.simple_selectable_list_item, place.phone_number);
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String phone = ((String) place.phone_number.get(which)).split(" ")[0];
                                PlaceDetailActivity.this.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else {
                        String phone = (String) place.phone_number.get(0);
                        phone = phone.split(" ")[0];
                        PlaceDetailActivity.this.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                    }
                }
            });

        } else {
            findViewById(R.id.callButton).setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latlng = new LatLng(((Place) object).lat, ((Place) object).lng);
        mMap.addMarker(new MarkerOptions().position(latlng).title(((Place) object).name()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));


    }

    @Override
    public void onResult(@NonNull AppInviteInvitationResult result) {
        if (result.getStatus().isSuccess()) {
            Intent intent = result.getInvitationIntent();
            String deepLink = AppInviteReferral.getDeepLink(intent);

            FirebaseDataManager.init().loadPlaceFirebase(getObjectStringFromDeepLink(deepLink), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    object = dataSnapshot.getValue(Place.class);
                    if (object == null) {
                        startActivity(new Intent(PlaceDetailActivity.this, MainActivity.class));
                        finish();

                    } else {
                        ((Place) object).key = dataSnapshot.getKey();
                        loadContent();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Log.d("PLACE DEEP LINKING", "getInvitation: no deep link found.");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Place place = (Place) object;
        FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.FAB_BUTTON_TAG, "Share: Place " + place.name);
        Intent intent = new Intent(Intent.ACTION_SEND);
        String link = String.format(getResources().getString(R.string.dynamic_link_format), "place", place.key);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_format), place.name(), "", link));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.share)));
    }
}
