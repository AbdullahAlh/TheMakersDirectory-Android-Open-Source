package com.themakersdirectory.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.themakersdirectory.R;
import com.themakersdirectory.SearchableActivity;
import com.themakersdirectory.UI.InteractiveRecyclerView;
import com.themakersdirectory.adapter.PlacesAdapter;
import com.themakersdirectory.adapter.ThingsAdapter;
import com.themakersdirectory.algolia.AlgoliaSearchManager;
import com.themakersdirectory.entity.Place;
import com.themakersdirectory.entity.Thing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xlethal on 4/2/16.
 */
public class SearchListFragment extends Fragment implements CompletionHandler, InteractiveRecyclerView.OnBottomReachedListener {
    public int pageTitle;
    public int nbPages;
    public int page = 0;
    public String query;
    public String searchIndex;
    public int number_of_hits;

    private InteractiveRecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search, container, false);

        mRecyclerView = (InteractiveRecyclerView) rootView.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setOnBottomReachedListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        page = 0;
        AlgoliaSearchManager.init().search(searchIndex, query, page, this);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void search(String query) {
        page = 0;
        number_of_hits = 0;
        setQuery(query);
        if (getView() != null) {
            getView().findViewById(R.id.msg_text).setVisibility(View.GONE);
            getView().findViewById(R.id.progress_bar_layout).setVisibility(View.VISIBLE);

            mRecyclerView.setAdapter(null);
        }

        AlgoliaSearchManager.init().search(searchIndex, query, page, this);
    }

    @Override
    public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
        if (getView() != null) {
            JSONArray hits;

            try {
                hits = (JSONArray) jsonObject.get("hits");
                number_of_hits = hits.length();

                if (number_of_hits == 0) {
                    getView().findViewById(R.id.msg_text).setVisibility(View.VISIBLE);

                } else {
                    if (searchIndex.equals(AlgoliaSearchManager.INDEX_PLACES)) {
                        placesUpdateAdapter(hits);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(SearchableActivity.SEARCH_HITS));

                    } else if (searchIndex.equals(AlgoliaSearchManager.INDEX_DIRECTORY))
                        toolsAndMaterialsUpdateAdapter(hits);

                }
                if (getView().findViewById(R.id.progress_bar_layout).getVisibility() == View.VISIBLE)
                    getView().findViewById(R.id.progress_bar_layout).setVisibility(View.GONE);

                if (getView().findViewById(R.id.progress_bar_recycler_view).getVisibility() == View.VISIBLE)
                    getView().findViewById(R.id.progress_bar_recycler_view).setVisibility(View.GONE);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                nbPages = jsonObject.getInt("nbPages");

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void placesUpdateAdapter(JSONArray hits) throws JSONException {
        ArrayList<Place> arrayList = new ArrayList<>();
        for (int i = 0; i < hits.length(); i++) {
            arrayList.add(Place.fromAlgoliaJson(hits.getJSONObject(i)));

        }

        if (mRecyclerView.getAdapter() != null) {
            ((PlacesAdapter) mRecyclerView.getAdapter()).addItems(arrayList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else
            mRecyclerView.setAdapter(new PlacesAdapter(arrayList));
    }

    private void toolsAndMaterialsUpdateAdapter(JSONArray hits) throws JSONException {
        ArrayList<Thing> arrayList = new ArrayList<>();
        for (int i = 0; i < hits.length(); i++) {
            arrayList.add(Thing.fromAlgoliaJson(hits.getJSONObject(i)));

        }

        if (mRecyclerView.getAdapter() != null) {
            ((ThingsAdapter) mRecyclerView.getAdapter()).addItems(arrayList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else
            mRecyclerView.setAdapter(new ThingsAdapter(arrayList));
    }

    @Override
    public void onBottomReached() {
        page++;

        if (page < nbPages) {
            getView().findViewById(R.id.progress_bar_recycler_view).setVisibility(View.VISIBLE);
            AlgoliaSearchManager.init().search(searchIndex, query, page, this);

        }
    }
}
