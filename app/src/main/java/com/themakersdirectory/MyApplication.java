package com.themakersdirectory;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by xlethal on 3/27/16.
 */
public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MyApplication.context = getApplicationContext();
//        Picasso.with(MyApplication.getAppContext()).setIndicatorsEnabled(true);
    }

    public synchronized static Context getAppContext() {
        return MyApplication.context;
    }
}
