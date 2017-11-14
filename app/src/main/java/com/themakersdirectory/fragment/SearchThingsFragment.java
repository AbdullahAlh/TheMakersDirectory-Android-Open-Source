package com.themakersdirectory.fragment;

import com.themakersdirectory.R;
import com.themakersdirectory.algolia.AlgoliaSearchManager;

/**
 * Created by xlethal on 4/2/16.
 */
public class SearchThingsFragment extends SearchListFragment {

    public SearchThingsFragment() {
        pageTitle = R.string.tools_and_materials;
        searchIndex = AlgoliaSearchManager.INDEX_DIRECTORY;
    }

}
