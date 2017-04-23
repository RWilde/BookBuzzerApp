package com.fyp.n3015509.bookbuzzerapp.other;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by tomha on 23-Apr-17.
 */

public class SearchSuggestionsProvider  extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.fyp.n3015509.SearchSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
