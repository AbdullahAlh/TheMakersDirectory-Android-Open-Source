package com.themakersdirectory.firebase;

/**
 * Created by xlethal on 3/27/16.
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.themakersdirectory.MyApplication;
import com.themakersdirectory.entity.Place;
import com.themakersdirectory.entity.Project;
import com.themakersdirectory.entity.Thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDataManager {
    public static final String FIREBASE_ADD_FORM_FAILURE = "FIREBASE_ADD_FORM_FAILURE";
    public static final String FIREBASE_ADD_FORM_SUCCESS = "FIREBASE_ADD_FORM_SUCCESS";

    static FirebaseDataManager firebaseDataManager;
    static DatabaseReference firebaseRef;

    public static String LOCATION_KEY = "Location";
    public static String MATERIAL_KEY = "Material";
    public static String PROJECT_KEY = "Project";
    public static String TOOL_KEY = "Tool";

    public static FirebaseDataManager init() {
        if (firebaseDataManager == null) {
            firebaseDataManager = new FirebaseDataManager();
            firebaseRef = FirebaseDatabase.getInstance()
                    .getReference();
        }
        return firebaseDataManager;
    }

    public void loadProjectsFirebase() {
        firebaseRef.child(PROJECT_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Project> arrayList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Project object = itemSnapshot.getValue(Project.class);
                    object.key = itemSnapshot.getKey();
                    arrayList.add(object);
                }
                Intent intent = new Intent(PROJECT_KEY);
                intent.putExtra(PROJECT_KEY, arrayList);
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void loadToolsFirebase() {
        firebaseRef.child(TOOL_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Thing> arrayList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Thing object = itemSnapshot.getValue(Thing.class);
                    object.type = Thing.TOOL_TYPE;
                    object.key = itemSnapshot.getKey();

                    arrayList.add(object);
                }
                Intent intent = new Intent(TOOL_KEY);
                intent.putExtra(TOOL_KEY, arrayList);
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void loadMaterialsFirebase() {
        firebaseRef.child(MATERIAL_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Thing> arrayList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Thing object = itemSnapshot.getValue(Thing.class);
                    object.type = Thing.MATERIAL_TYPE;
                    object.key = itemSnapshot.getKey();

                    arrayList.add(object);
                }
                Intent intent = new Intent(MATERIAL_KEY);
                intent.putExtra(MATERIAL_KEY, arrayList);
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void loadPlacesFirebase() {
        firebaseRef.child(LOCATION_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Place> arrayList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Place object = itemSnapshot.getValue(Place.class);
                    object.key = itemSnapshot.getKey();
                    arrayList.add(object);
                }
                Intent intent = new Intent(LOCATION_KEY);
                intent.putExtra(LOCATION_KEY, arrayList);
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void loadMaterialFirebase(String material, ValueEventListener valueEventListener) {
        firebaseRef.child(MATERIAL_KEY).child(material).addValueEventListener(valueEventListener);
    }

    public void loadToolFirebase(String tool, ValueEventListener valueEventListener) {
        firebaseRef.child(TOOL_KEY).child(tool).addValueEventListener(valueEventListener);

    }

    public void loadPlaceFirebase(String place, ValueEventListener valueEventListener) {
        firebaseRef.child(LOCATION_KEY).child(place).addValueEventListener(valueEventListener);

    }

    public void loadProjectFirebase(String project, ValueEventListener valueEventListener) {
        firebaseRef.child(PROJECT_KEY).child(project).addValueEventListener(valueEventListener);

    }

    public void postFormEntry(String type, String name, String description, String user_email, String place_name, String place_address, String place_latLng) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("name", name);
        map.put("description", description);
        map.put("user", user_email);

        if (place_name != null)
            map.put("place_name", place_name);

        if (place_address != null)
            map.put("place_address", place_address);

        if (place_latLng != null)
            map.put("latlng", place_latLng);

        postFormEntry(map);

    }

    public void postFormEntry(Map<String, Object> entry) {
        String key = firebaseRef.child("add-form").push().getKey();

        firebaseRef.child("add-form").child(key).updateChildren(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(new Intent(FIREBASE_ADD_FORM_SUCCESS));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("POST FAILED", e.toString());
                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(new Intent(FIREBASE_ADD_FORM_FAILURE));
            }

        });
    }
}

