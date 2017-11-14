package com.themakersdirectory.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by xlethal on 9/22/16.
 */

public class FirebaseAnalyticsManager {
    public static String MENU_BUTTON_TAG = "menu_button";
    public static String FAB_BUTTON_TAG = "fab_button";
    public static String ACTION_BUTTON_TAG = "action_button";
    public static String THING_CARD_TAG = "thing_card";
    public static String PROJECT_CARD_TAG = "project_card";
    public static String PLACE_CARD_TAG = "place_card";

    private static FirebaseAnalyticsManager firebaseAnalyticsManager;

    public static FirebaseAnalyticsManager init() {
        if (firebaseAnalyticsManager == null)
            firebaseAnalyticsManager = new FirebaseAnalyticsManager();

        return firebaseAnalyticsManager;
    }

    public void logEvent(Context context, String id, String name) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }
}
