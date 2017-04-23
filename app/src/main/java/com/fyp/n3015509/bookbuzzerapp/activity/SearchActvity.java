package com.fyp.n3015509.bookbuzzerapp.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toolbar;

import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.bookbuzzerapp.R;

import com.fyp.n3015509.bookbuzzerapp.other.SearchSuggestionsProvider;
import com.fyp.n3015509.bookbuzzerapp.other.SearchViewAdapter;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.db.DBUtil;

import java.util.ArrayList;

public class SearchActvity extends AppCompatActivity {
    ArrayList<SearchResult> results = new ArrayList();
    SearchViewAdapter mAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    public ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_actvity);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            results = getLocalResults(query);

            mAdapter = new SearchViewAdapter(this, results);
            mainListView = (ListView) findViewById(R.id.search_list);
            mainListView.setAdapter(mAdapter);

            GetSearchTask saveTask = new GetSearchTask(query, getApplicationContext(), mAdapter);
            saveTask.execute((Void) null);
        }
    }

    private ArrayList<SearchResult> getLocalResults(String query) {
        DBUtil db = new DBUtil();
        return db.GetSearchResults(getApplicationContext(), query);
    }

    private class GetSearchTask extends AsyncTask<Void, Void, Boolean> {

        private final SearchViewAdapter mAdapter;
        private String mSearch;
        private ProgressDialog progress;
        private Context mContext;
        private Handler mHandler;
        ArrayList<SearchResult> apiResults = new ArrayList<>();

        public GetSearchTask(String query, Context applicationContext, SearchViewAdapter adapter) {
            this.mContext = applicationContext;
            this.mSearch = query;
            this.mAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         //   progress = new ProgressDialog(mContext);
        //    progress.setMessage("Searching for additional results...");
        //    progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                GoodreadsAPI api = new GoodreadsAPI();
                apiResults = api.getSearch(mContext, mSearch);

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
         //   progress.dismiss();
            mHandler = new Handler();

            if (success) {
                //finish();
                mAdapter.upDateEntries(apiResults);

            } else {
                //book wasnt deleted succesfully
            }
        }
    }
}
