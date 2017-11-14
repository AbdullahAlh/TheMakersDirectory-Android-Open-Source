package com.themakersdirectory.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.themakersdirectory.MyApplication;
import com.themakersdirectory.R;

/**
 * Created by xlethal on 6/8/16.
 */

public class FirebaseAuthManager {
    public static final String FIREBASE_USER_SIGNED_IN = "FIREBASE_USER_SIGNED_IN";
    public static final String FIREBASE_USER_SIGNED_OUT = "FIREBASE_USER_SIGNED_OUT";
    public static final String FIREBASE_USER_AUTH_FAILED = "FIREBASE_USER_AUTH_FAILED";
    public static final String ERROR_MSG = "ERROR_MSG";

    private static FirebaseAuthManager firebaseAuthManager;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    public static FirebaseAuthManager init() {
        if (firebaseAuthManager == null) {
            firebaseAuthManager = new FirebaseAuthManager();
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
//                        Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                        LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(new Intent(FIREBASE_USER_SIGNED_IN));

                    } else {
                        // User is signed out
//                        Log.d("", "onAuthStateChanged:signed_out");
                        LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(new Intent(FIREBASE_USER_SIGNED_OUT));
                    }

                }
            };

            mAuth.addAuthStateListener(mAuthListener);
        }

        return firebaseAuthManager;
    }

    public void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInAnonymously:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("", "signInAnonymously", task.getException());

                        }

                    }
                });
    }

    public void signInWith(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("FIREBASE Sign In", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
//                            Log.w("FIREBASE Sign In", "signInWithEmail Failed to sign in", task.getException());

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                createUserWith(email, password);

                            } catch (Exception e) {
//                                FirebaseAuthInvalidCredentialsException
                                Intent intent = new Intent(FIREBASE_USER_AUTH_FAILED);
                                intent.putExtra(ERROR_MSG, task.getException().getLocalizedMessage());
                                LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);
                            }


                        }

                    }
                });
    }

    public void createUserWith(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("FIREBASE Create user", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
//                            Log.d("FIREBASE Create user", "createUserWithEmail:onComplete: FAILED", task.getException());
                            Intent intent = new Intent(FIREBASE_USER_AUTH_FAILED);
                            intent.putExtra(ERROR_MSG, task.getException().getLocalizedMessage());
                            LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);

                        }

                    }
                });

    }

    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.firebase_password_Reset_email), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void linkUserAccountWith(String email, String password) {
        if (mAuth.getCurrentUser() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("", "linkWithCredential:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Log.d("", "Authentication failed.");
                            }

                        }
                    });
        }
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isSignedIn() {
        FirebaseUser firebaseUser = getCurrentUser();
        return (firebaseUser != null);
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener mAuthListener) {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthStateListener(FirebaseAuth.AuthStateListener mAuthListener) {
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    public void signOut() {
        mAuth.signOut();
    }


}
