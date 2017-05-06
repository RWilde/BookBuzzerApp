package com.fyp.n3015509.bookbuzzerapp.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.activity.SearchActvity;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.enums.SearchResultType;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.db.DBUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tomha on 22-Apr-17.
 */

public class SearchViewAdapter  extends BaseSwipeAdapter {
    ArrayList<SearchResult> result = new ArrayList<>();
    private Context mContext;

    private ImageView image;
    private WatchBookTask watchTask;
    SearchActvity act;

    GoodreadsAPI goodreadsAPI = new GoodreadsAPI();

    public SearchViewAdapter(Context mContext, ArrayList<SearchResult> results, SearchActvity act) {
        this.mContext = mContext;
        this.result = results;
        this.act = act;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.search_listview_item, null);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

        swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (result.get(position).getType() == SearchResultType.GOODREADSAPI)
                    {
                        //download book, save to db and watch
                        DownloadBookTask mDownloadTask = new DownloadBookTask(mContext, result.get(position).getBookName(),result.get(position).getAuthorName(), act);
                        mDownloadTask.execute((Void) null);
                    }
                    else if(result.get(position).getType() == SearchResultType.LOCAL)
                    {
                        //open local book
                        Intent i = new Intent(mContext, MainActivity.class);
                        MainActivity.bookId = result.get(position).getGoodreadsId();
                        MainActivity.download = false;
                        MainActivity.navItemIndex = 1;
                        MainActivity.title = result.get(position).getBookName();
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        act.finish();
                        mContext.startActivity(i);

                    }
            }
        });

//        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //if from api, download and watch
//                if (result.get(position).getType() == SearchResultType.GOODREADSAPI)
//                {
//                    //download book, save to db and watch
//                    DownloadBookTask mDownloadTask = new DownloadBookTask(mContext, result.get(position).getBookName(),result.get(position).getAuthorName());
//                    mDownloadTask.execute((Void) null);
//                }
//                else if(result.get(position).getType() == SearchResultType.LOCAL)
//                {
//                    //open local book
//                    MainActivity main = new MainActivity();
//                    BookFragment book = new BookFragment();
//                    main.switchContent(book, result.get(position).getGoodreadsId());
//                }
//            }
//        });

        swipeLayout.findViewById(R.id.star2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.get(position).getType() == SearchResultType.GOODREADSAPI)
                {
                    //download book, save to db and watch
                }
                else if(result.get(position).getType() == SearchResultType.LOCAL)
                {
                    //watch local book
                    if (setStar(v, position) == true) {
                        //removeFromWatchList
                        DBUtil.RemoveFromWatched(mContext, result.get(position).getGoodreadsId());
                        setNotWatched(v);
                        Toast.makeText(mContext, "Removed from watch list", Toast.LENGTH_SHORT).show();
                    } else {
                        setWatched(v);

                        watchTask = new WatchBookTask(result.get(position).getGoodreadsId(), result.get(position).getListName());
                        watchTask.execute((Void) null);
                    }
                }
//                mNotificationTask = new MarkResultAsWatchedTask(mContext, result.get(position));
//                mNotificationTask.execute((Void) null);
            }
        });

        return v;
    }

    public boolean setStar(View view, int position) {
        DBUtil db = new DBUtil();
        boolean watched = db.checkIfWatched(mContext, result.get(position).getGoodreadsId());

        if (watched == true) {
            setWatched(view);
        } else {
            setNotWatched(view);
        }
        return watched;
    }

    public void setWatched(View view) {
        image = (ImageView) view.findViewById(R.id.star2);

        image.setImageResource(R.drawable.star_filled);
    }

    public void setNotWatched(View view) {
        image = (ImageView) view.findViewById(R.id.star2);

        image.setImageResource(R.drawable.star);
    }


    @Override
    public void fillValues(int position, View convertView) {
        if (result.isEmpty())
        {
            View empty = convertView.findViewById(R.id.empty);
            ListView list = (ListView) convertView.findViewById(R.id.list);
            list.setEmptyView(empty);
        }
        else {
            TextView atext = (TextView) convertView.findViewById(R.id.book_name);
            TextView bText = (TextView) convertView.findViewById(R.id.book_author);

            if (result.get(position).getBookName() == null)
            {
                bText.setText(result.get(position).getAuthorName());
            }
            else
            {
                atext.setText(result.get(position).getBookName());
                bText.setText(result.get(position).getAuthorName());
            }

        }
        setStar(convertView, position);
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void upDateEntries(ArrayList<SearchResult> apiResults) {
        if (apiResults != null)
        {
            for(int i = 0; i < apiResults.size(); i++)
            {
               // result.add(apiResults.get(i));
            }
           // notifyDataSetChanged();
        }
    }

    private class DownloadBookTask extends AsyncTask<Void, Void, Boolean> {

        private final String mBook;
        private final String mAuthor;
        private final SearchActvity act;
        private ProgressDialog progress;
        private Context frag;
        private Handler mHandler;
        String bookJson;
        GoodreadsBook book;

        public DownloadBookTask(Context frag, String mBook, String mAuthor,  SearchActvity act) {
            this.mBook = mBook;
            this.mAuthor = mAuthor;
            this.frag = frag;
            this.act = act;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(act);
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
                MainActivity.download = true;
                MainActivity.navItemIndex =0;
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("book", book.getTitle());
                mContext.startActivity(i);
                act.finish();
                ;

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
//            progress = new ProgressDialog(mContext);
//            progress.setMessage("Adding to watch list...");
//            progress.show();
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
          //  progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book watched", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book to watch list, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
