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
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.apppreferences.OnScrollObserver;
import com.fyp.n3015509.bookbuzzerapp.R;

import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.DownloadBookFragment;
import com.fyp.n3015509.bookbuzzerapp.other.SearchSuggestionsProvider;
import com.fyp.n3015509.bookbuzzerapp.other.SearchViewAdapter;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.enums.SearchResultType;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.db.DBUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActvity extends MainActivity {
    ArrayList<SearchResult> results = new ArrayList();
    Context mContext = null;

    private android.support.v7.widget.Toolbar toolbar;
    //public ListView mainListView;
    private ListView downloadListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.navItemIndex = 7;
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.frame);
        View v = getLayoutInflater().inflate(R.layout.activity_search_actvity, contentFrameLayout);
        mContext = getApplicationContext();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            results = getLocalResults(query);
            ListView mainListView = (ListView) findViewById(R.id.search_list);

            if (results.size() != 0) {
                SearchViewAdapter mAdapter = new SearchViewAdapter(getApplicationContext(), results, this);
                mainListView.setAdapter(mAdapter);
            } else {
                mainListView.setVisibility(View.GONE);
                TextView text = (TextView) findViewById(R.id.search_list_text);
                text.setVisibility(View.GONE);
                View view = (View) findViewById(R.id.search_list_view);
                view.setVisibility(View.GONE);
            }


            GetSearchTask saveTask = new GetSearchTask(query, getApplicationContext(), this);
            saveTask.execute((Void) null);


        }
    }

    private ArrayList<SearchResult> getLocalResults(String query) {
        DBUtil db = new DBUtil();
        return db.GetSearchResults(getApplicationContext(), query);
    }

    private class DownloadBookTask extends AsyncTask<Void, Void, Boolean> {

        private final String mBook;
        private final String mAuthor;
        private ProgressDialog progress;
        private Context frag;
        private Handler mHandler;
        String bookJson;
        GoodreadsBook book;
        GoodreadsAPI goodreadsAPI = new GoodreadsAPI();

        public DownloadBookTask(Context frag, String mBook, String mAuthor) {
            this.mBook = mBook;
            this.mAuthor = mAuthor;
            this.frag = frag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Opening book...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                book = goodreadsAPI.DownloadBook(mContext, mBook, mAuthor);
                BookBuzzerAPI api = new BookBuzzerAPI();
                ArrayList<PriceChecker> p = api.RunPriceChecker(mContext, book.getIsbn());

                if (p != null) {
                    for (PriceChecker price : p) {
                        switch (price.getType()) {
                            case KINDLE_EDITION:
                                book.setKindlePrice(price.getPrice());
                                break;
                            case HARDBACK:
                                book.setHardcoverPrice(price.getPrice());
                                break;
                            case PAPERBACK:
                                book.setPaperbackPrice(price.getPrice());
                                break;
                        }
                    }
                }


                //open book fragment

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mHandler = new Handler();
            progress.dismiss();

            if (success) {
                Intent i = new Intent(mContext, MainActivity.class);
                MainActivity.data = book;

                i.putExtra("book", book.getTitle());
                mContext.startActivity(i);

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error opening book, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class WatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private final int mBook;
        private final String listName;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;

        WatchBookTask(int book, String listId) {
            mBook = book;
            listName = listId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Adding to watch list...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                BookBuzzerAPI api = new BookBuzzerAPI();
                Boolean dbSuccess = DBUtil.WatchBook(mContext, mBook);
                Boolean apiSuccess = api.WatchBook(mContext, mBook, listName);

                if (dbSuccess == false || apiSuccess == false) {
                    return false;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book watched", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book to watch list, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetSearchTask extends AsyncTask<Void, Void, Boolean> {
        private final SearchActvity act;
        private String mSearch;
        private ProgressDialog progress;
        private Context mContext;
        private Handler mHandler;
        ArrayList<SearchResult> apiResults = new ArrayList<>();

        public GetSearchTask(String query, Context applicationContext, SearchActvity act) {
            this.mContext = applicationContext;
            this.mSearch = query;
            this.act = act;
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
            SearchViewAdapter mDownloadAdapter = new SearchViewAdapter(mContext, apiResults, act);
            if (success) {
                ListView downloadListView = (ListView) findViewById(R.id.download_search_list);
                downloadListView.setAdapter(mDownloadAdapter);
                downloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //  ((SwipeLayout) (listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
                    }
                });
                mDownloadAdapter.notifyDataSetChanged();
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
