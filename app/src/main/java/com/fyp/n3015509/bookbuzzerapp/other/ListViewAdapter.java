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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.Util.APIUtil;
import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ListFragment;

import org.json.JSONObject;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private FragmentActivity activity;

    private TextView text;
    private ImageView image;
    private TextView authorText;

    private int listId;
    private String listName;
    private String[] names;
    private Bitmap[] images;
    private String[] authors;
    private Integer[] ids;

    private RemoveBookTask deleteTask;
    private WatchBookTask watchTask;

    public ListViewAdapter(Context ctx)
    {
        this.mContext = ctx;
    }

    public ListViewAdapter(FragmentActivity activity, String[] values, Bitmap[] images, String[] authors, int listId, Integer[] ids, String listName) {
        this.activity = activity;
        this.mContext = activity;
        this.names = values;
        this.images = images;
        this.authors = authors;
        this.listId = listId;
        this.ids = ids;
        this.listName = listName;
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
                ViewBook(position);
            }
        });

        swipeLayout.findViewById(R.id.star2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchTask = new WatchBookTask(ids[position], listId);
                watchTask.execute((Void) null);
            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask = new RemoveBookTask(ids[position], listId, ListViewAdapter.this.activity);
                deleteTask.execute((Void) null);
            }
        });

        swipeLayout.findViewById(R.id.magnifier2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBook(position);
            }
        });

        return v;
    }

    public void ViewBook(int position) {
        Fragment fragment = new BookFragment();

        Bundle args = new Bundle();
        args.putInt("bookId", ids[position]);

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void fillValues(int position, View convertView) {
        image = (ImageView) convertView.findViewById(R.id.imageView);
        image.setImageBitmap(images[position]);

        text = (TextView) convertView.findViewById(R.id.book_name);
        text.setText(names[position]);

        authorText = (TextView) convertView.findViewById(R.id.book_author);
        authorText.setText(authors[position]);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
                Boolean apiSuccess = APIUtil.RemoveBookFromBuzzlist(mContext, mBook, listName);
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
                APIUtil api = new APIUtil();
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
}
