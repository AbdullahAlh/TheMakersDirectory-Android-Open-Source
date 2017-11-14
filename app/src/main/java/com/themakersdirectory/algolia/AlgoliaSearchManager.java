package com.themakersdirectory.algolia;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

/**
 * Created by xlethal on 7/21/16.
 */

public class AlgoliaSearchManager {
    public static final String INDEX_PLACES = "places";
    public static final String INDEX_DIRECTORY = "directory";
    private static AlgoliaSearchManager algoliaSearchManager;
    private Client client;

    private AlgoliaSearchManager() {
        client = new Client("APP_ID", "API_KEY");

    }

    public static AlgoliaSearchManager init() {
        if (algoliaSearchManager == null)
            algoliaSearchManager = new AlgoliaSearchManager();
        return algoliaSearchManager;
    }

    public void search(String index_str, String query, int page_number, CompletionHandler completionHandler) {
        Index index = client.initIndex(index_str);
        index.enableSearchCache();
        index.searchAsync(new Query(query).setPage(page_number), completionHandler);

    }
}
