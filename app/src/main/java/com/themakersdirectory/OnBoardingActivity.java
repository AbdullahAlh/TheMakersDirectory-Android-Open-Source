package com.themakersdirectory;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.themakersdirectory.firebase.FirebaseAnalyticsManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OnBoardingActivity extends AppCompatActivity {

    public static final String OPEN_APP_FIRST_TIME_KEY = "OPEN_APP_FIRST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FullscreenTheme);
        if (!isFirstTime()) {
            startMainActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.onBoardingLayout), "alpha", 0.0f, 1.0f);
        objectAnimator.setDuration(300);
        objectAnimator.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void startMaking(View view) {
        saveFirstTime(false);
        FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Let's Start Making Button");
        startMainActivity();
    }

    private void startMainActivity() {
        Intent i = new Intent(OnBoardingActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(0, android.R.anim.fade_in);
    }

    private boolean isFirstTime() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(OPEN_APP_FIRST_TIME_KEY, true);

    }

    private void saveFirstTime(boolean value) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(OPEN_APP_FIRST_TIME_KEY, value);
        editor.apply();

    }
}
