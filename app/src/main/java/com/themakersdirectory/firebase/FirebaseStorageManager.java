package com.themakersdirectory.firebase;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.themakersdirectory.MyApplication;
import com.themakersdirectory.Networking.VolleyManager;

/**
 * Created by xlethal on 6/6/16.
 */

public class FirebaseStorageManager {
    static FirebaseStorageManager firebaseStorageManager;
    static StorageReference storageRef;

    public static FirebaseStorageManager init() {
        if (firebaseStorageManager == null) {
            firebaseStorageManager = new FirebaseStorageManager();
            storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://the-makers-directory.appspot.com");


        }

        return firebaseStorageManager;
    }

    public void getImageFromUrl(String img_url, NetworkImageView imageView) {
//        Picasso.with(MyApplication.getAppContext()).load(img_uri).into(imageView);
        imageView.setImageUrl(img_url, VolleyManager.getInstance(MyApplication.getAppContext()).getImageLoader());

    }
}
