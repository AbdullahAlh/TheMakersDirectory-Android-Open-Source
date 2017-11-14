package com.themakersdirectory.activity.detail;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.themakersdirectory.adapter.ProjectThingsSquareAdapter;
import com.themakersdirectory.entity.Project;
import com.themakersdirectory.entity.Thing;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseDataManager;
import com.themakersdirectory.firebase.FirebaseStorageManager;

import java.util.ArrayList;

/**
 * Created by xlethal on 3/30/16.
 */
public class ProjectDetailActivity extends MainDetailActivity {

    @Override
    public void setupDetails() {
        setContentView(R.layout.activity_project_detail);

    }

    @Override
    public void loadContent() {
        final Project project = (Project) object;
        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(project.name());
        ((TextView) findViewById(R.id.description)).setText(project.description());

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_img);
        FirebaseStorageManager.init().getImageFromUrl(project.img, imageView);

        final RecyclerView materials_recycler = (RecyclerView) findViewById(R.id.materials_recycler_view);
        materials_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        final RecyclerView tools_recycler = (RecyclerView) findViewById(R.id.tools_recycler_view);
        tools_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        project.loadMaterials(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Thing object = snapshot.getValue(Thing.class);
                object.type = Thing.MATERIAL_TYPE;
                object.key = snapshot.getKey();

                if (materials_recycler.getAdapter() != null) {
                    ((ProjectThingsSquareAdapter) materials_recycler.getAdapter()).addItem(object);
                    materials_recycler.getAdapter().notifyDataSetChanged();
                } else {
                    ArrayList<Thing> arrayList = new ArrayList<>();
                    arrayList.add(object);
                    materials_recycler.setAdapter(new ProjectThingsSquareAdapter(arrayList));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        project.loadTools(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Thing object = snapshot.getValue(Thing.class);
                object.type = Thing.TOOL_TYPE;
                object.key = snapshot.getKey();

                if (tools_recycler.getAdapter() != null) {
                    ((ProjectThingsSquareAdapter) tools_recycler.getAdapter()).addItem(object);
                    tools_recycler.getAdapter().notifyDataSetChanged();
                } else {
                    ArrayList<Thing> arrayList = new ArrayList<>();
                    arrayList.add(object);
                    tools_recycler.setAdapter(new ProjectThingsSquareAdapter(arrayList));
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

            FirebaseDataManager.init().loadProjectFirebase(getObjectStringFromDeepLink(deepLink), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    object = dataSnapshot.getValue(Project.class);
                    if (object == null) {
                        startActivity(new Intent(ProjectDetailActivity.this, MainActivity.class));
                        finish();

                    } else {
                        ((Project) object).key = dataSnapshot.getKey();
                        loadContent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Log.d("PROJECT DEEP LINKING", "getInvitation: no deep link found.");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Project project = (Project) object;
        FirebaseAnalyticsManager.init().logEvent(v.getContext(), FirebaseAnalyticsManager.FAB_BUTTON_TAG, "Share: Project " + project.name);
        Intent intent = new Intent(Intent.ACTION_SEND);
        String link = String.format(getResources().getString(R.string.dynamic_link_format), "project", project.key);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_format), project.name(), project.description(), link));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.share)));
    }
}
