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
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.apppreferences.OnScrollObserver;
import com.fyp.n3015509.bookbuzzerapp.R;

import com.fyp.n3015509.bookbuzzerapp.other.SearchSuggestionsProvider;
import com.fyp.n3015509.bookbuzzerapp.other.SearchViewAdapter;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.db.DBUtil;

import java.util.ArrayList;

public class SearchActvity extends MainActivity {
    ArrayList<SearchResult> results = new ArrayList();
    SearchViewAdapter mAdapter;
    SearchViewAdapter mDownloadAdapter;

    private android.support.v7.widget.Toolbar toolbar;
    public ListView mainListView;
    private ListView downloadListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.navItemIndex = 7;
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.frame);
        getLayoutInflater().inflate(R.layout.activity_search_actvity, contentFrameLayout);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            results = getLocalResults(query);
            mainListView = (ListView) findViewById(R.id.search_list);

            if (results.size() != 0) {
                mAdapter = new SearchViewAdapter(this, results);
                mAdapter.setMode(Attributes.Mode.Single);
                mainListView.setAdapter(mAdapter);

                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //  ((SwipeLayout) (listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
                        ((SwipeLayout) (mainListView.getChildAt(Integer.parseInt((String) parent.getAdapter().getItem(position)) + 1))).open(true);
                    }
                });
            } else {
                mainListView.setVisibility(View.GONE);
                TextView text = (TextView) findViewById(R.id.search_list_text);
                text.setVisibility(View.GONE);
                View view = (View) findViewById(R.id.search_list_view);
                view.setVisibility(View.GONE);
            }


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
                if (apiResults.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            //   progress.dismiss();
            mHandler = new Handler();
            mDownloadAdapter = new SearchViewAdapter(mContext, apiResults);

            if (success) {
                downloadListView = (ListView) findViewById(R.id.download_search_list);
                downloadListView.setAdapter(mDownloadAdapter);
                downloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //  ((SwipeLayout) (listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
                        ((SwipeLayout) (mainListView.getChildAt(Integer.parseInt((String) parent.getAdapter().getItem(position)) + 1))).open(true);
                    }
                });
                //finish();
                //mDownloadAdapter.upDateEntries(apiResults);

            } else {
                downloadListView.setVisibility(View.GONE);
                TextView text = (TextView) findViewById(R.id.download_search_list_text);
                text.setVisibility(View.GONE);
                View view = (View) findViewById(R.id.download_search_list_view);
                view.setVisibility(View.GONE);
            }
        }
    }
}
