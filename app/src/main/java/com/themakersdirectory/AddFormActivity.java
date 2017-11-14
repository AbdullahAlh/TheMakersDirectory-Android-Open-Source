package com.themakersdirectory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseAuthManager;
import com.themakersdirectory.firebase.FirebaseDataManager;

public class AddFormActivity extends AppCompatActivity {

    private static final int REQUEST_PLACE_PICKER = 2143;
    private View mProgressView;
    private View mLayoutFormView;

    private Place place;
    private LatLng place_latLng;
    private String place_name;
    private String place_address;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private BroadcastReceiver signinBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgress(false);
            postEntry();

        }
    };

    private BroadcastReceiver failedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgress(false);
            ((EditText) findViewById(R.id.password)).setError(intent.getStringExtra(FirebaseAuthManager.ERROR_MSG));
            findViewById(R.id.password).requestFocus();
        }
    };

    private BroadcastReceiver successBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, getString(R.string.firebase_success_to_post), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private BroadcastReceiver failureBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgress(false);
            Snackbar.make(findViewById(R.id.submitButton), getString(R.string.firebase_failed_to_post), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form);

        mLayoutFormView = findViewById(R.id.scroll_layout);
        mProgressView = findViewById(R.id.post_progress);

        titleEditText = ((EditText) findViewById(R.id.title));
        descriptionEditText = ((EditText) findViewById(R.id.descriptionText));
        locationEditText = ((EditText) findViewById(R.id.location));

        if (!FirebaseAuthManager.init().isSignedIn()) {
            mEmailView = ((AutoCompleteTextView) findViewById(R.id.email));
            mPasswordView = ((EditText) findViewById(R.id.password));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuthManager.init().isSignedIn()) {
            findViewById(R.id.signin_required_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.signin_required_layout).setVisibility(View.VISIBLE);
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(signinBroadcastReceiver, new IntentFilter(FirebaseAuthManager.FIREBASE_USER_SIGNED_IN));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(failedBroadcastReceiver, new IntentFilter(FirebaseAuthManager.FIREBASE_USER_AUTH_FAILED));

        LocalBroadcastManager.getInstance(this).registerReceiver(successBroadcastReceiver, new IntentFilter(FirebaseDataManager.FIREBASE_ADD_FORM_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(failureBroadcastReceiver, new IntentFilter(FirebaseDataManager.FIREBASE_ADD_FORM_FAILURE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(signinBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(failedBroadcastReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(successBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(failureBroadcastReceiver);

    }

    public void submit(View view) {
        FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Submit New Entry Button");
        if (validateFields())
            if (!FirebaseAuthManager.init().isSignedIn()) {
                attemptLogin();
            } else {
                postEntry();
            }
    }

    // This method validates all form fields
    private boolean validateFields() {
        boolean valid = true;
        View focusView = null;

        titleEditText.setError(null);
        descriptionEditText.setError(null);
        locationEditText.setError(null);

        if (TextUtils.isEmpty(titleEditText.getText().toString())) {
            titleEditText.setError(getString(R.string.error_field_required));
            focusView = titleEditText;
            valid = false;

        }

        if (TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            descriptionEditText.setError(getString(R.string.error_field_required));
            focusView = descriptionEditText;
            valid = false;

        }

        if (TextUtils.isEmpty(locationEditText.getText().toString()) && !((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString().equals(getResources().getString(R.string.Projects))) {
            locationEditText.setError(getString(R.string.error_field_required));
            focusView = locationEditText;
            valid = false;

        }

        if (!FirebaseAuthManager.init().isSignedIn()) {
            mEmailView.setError(null);
            mPasswordView.setError(null);

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                valid = false;
            } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                valid = false;
            }

            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                valid = false;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                valid = false;
            }
        }

        if (!valid)
            focusView.requestFocus();

        return valid;
    }

    private void postEntry() {
        showProgress(true);

        if (place != null) {
            FirebaseDataManager.init().postFormEntry(((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString(),
                    titleEditText.getText().toString(), descriptionEditText.getText().toString(),
                    FirebaseAuthManager.init().getCurrentUser().getEmail(), place_name, place_address, place_latLng.toString());
        } else {
            FirebaseDataManager.init().postFormEntry(((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString(),
                    titleEditText.getText().toString(), descriptionEditText.getText().toString(),
                    FirebaseAuthManager.init().getCurrentUser().getEmail(), null, null, null);
        }

    }

    private void attemptLogin() {
        showProgress(true);
        FirebaseAuthManager.init().signInWith(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLayoutFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayoutFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayoutFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLayoutFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void openPlacePicker(View view) {
        FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.ACTION_BUTTON_TAG, "Open Place Picker Button");
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {
            place = PlacePicker.getPlace(this, data);
            place_name = place.getName().toString();
            place_address = place.getAddress().toString();
            place_latLng = place.getLatLng();

            ((EditText) findViewById(R.id.location)).setText(String.format("%s, %s", place_name, place_address));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
