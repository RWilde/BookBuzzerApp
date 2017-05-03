package com.fyp.n3015509.bookbuzzerapp.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.dao.BookAdapter;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ListFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewAdapter extends BaseSwipeAdapter implements Filterable {

    private ArrayList<BookAdapter> bookList = new ArrayList();
    private ArrayList<BookAdapter> filteredBookList = new ArrayList();

    private String[] isbns;
    private Context mContext;
    private FragmentActivity activity;

    private TextView text;
    private ImageView image;
    private TextView authorText;

    private int listId;
    private String listName;

    private RemoveBookTask deleteTask;
    private WatchBookTask watchTask;
    private PriceChecker priceTask;

    public ListViewAdapter(Context ctx) {
        this.mContext = ctx;
    }

    public ListViewAdapter(FragmentActivity activity, ArrayList<BookAdapter> adapter, int listId, String listName) {
        this.activity = activity;
        this.mContext = activity;
        this.listId = listId;
        this.listName = listName;
        this.bookList = adapter;
        this.filteredBookList = adapter;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceTask = new PriceChecker(mContext, getItem(position).getBuzzlistIsbns());
                priceTask.execute((Void) null);
                ViewBook(position);
            }
        });

        swipeLayout.findViewById(R.id.star2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = (ImageView) v.findViewById(R.id.star2);
                image.setImageResource(R.drawable.bg_circle);

                if (setStar(v, position) == true) {
                    //removeFromWatchList
                    DBUtil.RemoveFromWatched(mContext, getItem(position).getBuzzlistIds());
                    String listName = DBUtil.findListForBook(mContext, getItem(position).getBuzzlistIds());
                    BookBuzzerAPI.RemoveFromWatched(mContext, getItem(position).getBuzzlistIds());
                    setNotWatched(v);
                    Toast.makeText(mContext, "Removed from watch list", Toast.LENGTH_SHORT).show();
                } else {
                    setWatched(v);
                    watchTask = new WatchBookTask(getItem(position).getBuzzlistIds(), listId);
                    watchTask.execute((Void) null);
                }
            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listName.contentEquals("")) {
                    //from watch list
                    Toast.makeText(mContext, "Removed from watch list", Toast.LENGTH_SHORT).show();
                } else {
                    //from buzzlist
                    deleteTask = new RemoveBookTask(getItem(position).getBuzzlistIds(), listId, ListViewAdapter.this.activity);
                    deleteTask.execute((Void) null);
                }
            }
        });

        swipeLayout.findViewById(R.id.magnifier2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceTask = new PriceChecker(mContext, getItem(position).getBuzzlistIsbns());
                priceTask.execute((Void) null);
                ViewBook(position);
            }
        });

        return v;
    }

    public void ViewBook(int position) {
        Fragment fragment = new BookFragment();

        Bundle args = new Bundle();
        args.putInt("bookId", getItem(position).getBuzzlistIds());

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void fillValues(int position, View convertView) {
        image = (ImageView) convertView.findViewById(R.id.imageView);
        image.setImageBitmap(getItem(position).getBuzzlistImages());

        text = (TextView) convertView.findViewById(R.id.book_name);
        text.setText(getItem(position).getBuzzlistNames());

        authorText = (TextView) convertView.findViewById(R.id.book_author);
        authorText.setText(getItem(position).getBuzzlistAuthors());

        setStar(convertView, position);
    }

    public boolean setStar(View view, int position) {
        DBUtil db = new DBUtil();
        boolean watched = db.checkIfWatched(mContext, getItem(position).getBuzzlistIds());

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
    public int getCount() {
        return filteredBookList.size();
    }

    @Override
    public BookAdapter getItem(int position) {
        return filteredBookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String s = charSequence.toString().toLowerCase();
                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = bookList;
                    results.count = bookList.size();
                } else {
                    ArrayList<BookAdapter> filterResultsData = new ArrayList<BookAdapter>();
                    try {
                        for (BookAdapter data : bookList) {
                            if (data.getBuzzlistNames() != null) {
                                if (data.getBuzzlistNames().toLowerCase().startsWith(s)) {
                                    data.setAdded(true);
                                    filterResultsData.add(data);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.values == null) {
                    filteredBookList = new ArrayList<BookAdapter>();
                } else {
                    filteredBookList = (ArrayList<BookAdapter>) filterResults.values;
                }
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    private class RemoveBookTask extends AsyncTask<Void, Void, Boolean> {

        private final int mBook;
        private final int mListId;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public RemoveBookTask(Integer id, int listId, FragmentActivity frag) {
            mBook = id;
            mListId = listId;
            this.frag = frag;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Removing book...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                Boolean dbSuccess = DBUtil.RemoveBookFromBuzzList(mContext, mBook, mListId);
                Boolean apiSuccess = BookBuzzerAPI.RemoveBookFromBuzzlist(mContext, mBook, listName);
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
            mHandler = new Handler();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book successfully removed", Toast.LENGTH_SHORT).show();
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = new ListFragment();

                        Bundle args = new Bundle();
                        args.putInt("listId", listId);
                        fragment.setArguments(args);

                        FragmentTransaction fragmentTransaction = frag.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, "Buzzlist");
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                };

                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                }

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with removing book from buzzlist, please try again", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class WatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private final int mBook;
        private final int mListId;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;

        WatchBookTask(int book, int listId) {
            mBook = book;
            mListId = listId;
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

    private class PriceChecker extends AsyncTask<Void, Void, Boolean> {
        private final Context mContext;
        private final String mIsbn;

        PriceChecker(Context context, String isbn) {
            this.mContext = context;
            this.mIsbn = isbn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            BookBuzzerAPI util = new BookBuzzerAPI();
            util.RunPriceChecker(mContext, mIsbn);
            return true;
        }


        protected void onPostExecute() {
            //showProgress(false);

        }
    }
}
