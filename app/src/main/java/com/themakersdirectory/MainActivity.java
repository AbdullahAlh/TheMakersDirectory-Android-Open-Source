package com.themakersdirectory;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.themakersdirectory.firebase.FirebaseAnalyticsManager;
import com.themakersdirectory.firebase.FirebaseAuthManager;
import com.themakersdirectory.fragment.MainListFragment;
import com.themakersdirectory.fragment.MaterialsFragment;
import com.themakersdirectory.fragment.ProjectsFragment;
import com.themakersdirectory.fragment.ToolsFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    private ArrayList<MainListFragment> items = new ArrayList<>();

    private BroadcastReceiver signinBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
        }
    };

    private BroadcastReceiver signoutBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.initLanguage();

        setContentView(R.layout.activity_main_tabbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        items.add(new ProjectsFragment());
        items.add(new ToolsFragment());
        items.add(new MaterialsFragment());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.saveState();

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setupFab();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(signinBroadcastReceiver, new IntentFilter(FirebaseAuthManager.FIREBASE_USER_SIGNED_IN));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(signoutBroadcastReceiver, new IntentFilter(FirebaseAuthManager.FIREBASE_USER_SIGNED_OUT));

        if (searchView != null) {
            searchMenuItem.collapseActionView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(signinBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(signoutBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tabbar, menu);
        MenuItem signinMenuItem = menu.findItem(R.id.action_sign_in);
        MenuItem signoutMenuItem = menu.findItem(R.id.action_sign_out);

        if (FirebaseAuthManager.init().isSignedIn()) {
            signinMenuItem.setVisible(false);
            signoutMenuItem.setVisible(true);

        } else if (!signinMenuItem.isVisible()) {
            signinMenuItem.setVisible(true);
            signoutMenuItem.setVisible(false);
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.MENU_BUTTON_TAG, "Settings Button");
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_sign_in) {
            FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.MENU_BUTTON_TAG, "Sign in Button");
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else if (id == R.id.action_sign_out) {
            FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.MENU_BUTTON_TAG, "Sign out Button");
            Toast.makeText(this, String.format("%s %s", getString(R.string.signed_out_from), FirebaseAuthManager.init().getCurrentUser().getEmail()), Toast.LENGTH_SHORT).show();
            FirebaseAuthManager.init().signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAnalyticsManager.init().logEvent(getApplicationContext(), FirebaseAnalyticsManager.FAB_BUTTON_TAG, "Add New Entry Button");
                startActivity(new Intent(MainActivity.this, AddFormActivity.class));
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {


        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(items.get(position).pageTitle);
        }
    }
}
