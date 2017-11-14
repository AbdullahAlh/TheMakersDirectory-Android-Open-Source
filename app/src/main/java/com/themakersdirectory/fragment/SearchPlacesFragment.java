package com.themakersdirectory.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.themakersdirectory.R;
import com.themakersdirectory.algolia.AlgoliaSearchManager;

/**
 * Created by xlethal on 4/2/16.
 */
public class SearchPlacesFragment extends SearchListFragment {

    public SearchPlacesFragment() {
        pageTitle = R.string.Places;
        searchIndex = AlgoliaSearchManager.INDEX_PLACES;
    }

}
