package com.example.searchvideo

import android.content.SearchRecentSuggestionsProvider

class MainRecentSearchSuggestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }
    companion object {
        const val AUTHORITY = "com.example.searchvideo.MainRecentSearchSuggestionsProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}