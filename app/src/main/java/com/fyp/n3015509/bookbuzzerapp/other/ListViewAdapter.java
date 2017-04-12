package com.fyp.n3015509.bookbuzzerapp.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private FragmentActivity activity;
    private RemoveBookTask deleteTask;
    private TextView text;
    private ImageView image;
    private TextView authorText;
    String[] names;
    Bitmap[] images;
    String[] authors;
    Integer[] ids;
    int listId;

    public ListViewAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public ListViewAdapter(FragmentActivity activity, String[] values, Bitmap[] images, String[] authors, int listId, Integer[] ids) {
        this.activity = activity;
        this.mContext = activity;
        this.names = values;
        this.images = images;
        this.authors = authors;
        this.listId = listId;
        this.ids = ids;
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
                ViewBook();
            }
        });
        swipeLayout.findViewById(R.id.star2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "Added to watch list", Toast.LENGTH_SHORT).show();
            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask = new RemoveBookTask(ids[position], listId);
                deleteTask.execute((Void) null);
            }
        });

        swipeLayout.findViewById(R.id.magnifier2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBook();
                // Toast.makeText(mContext, "View Book", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    public void ViewBook() {
        Fragment fragment = new BookFragment();

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

        RemoveBookTask(int book, int listId) {
            mBook = book;
            mListId = listId;
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
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                Boolean dbSuccess = DBUtil.RemoveBookFromBuzzList(mContext, mBook, mListId);
                Boolean apiSuccess = APIUtil.RemoveBookFromBuzzlist(mContext, mBook, mListId, SaveSharedPreference.getToken(mContext));
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
                Toast.makeText(mContext, "Book successfully removed", Toast.LENGTH_SHORT).show();
                Fragment fragment = new ListFragment();

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with removing book from buzzlist, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class WatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private final String mBookName;
        private final int mListId;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;

        WatchBookTask(String book, int listId) {
            mBookName = book;
            mListId = listId;
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
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                Boolean dbSuccess = DBUtil.WatchBook(mContext, mBookName, mListId);

                deletedBook.put("name", mBookName);
                deletedBook.put("listId", mListId);
                deletedBook.put("token", SaveSharedPreference.getToken(mContext));

                Boolean apiSuccess = APIUtil.WatchBook(mContext, deletedBook);
                if (dbSuccess == false || apiSuccess == false) {
                    return false;
                }

//
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book successfully removed", Toast.LENGTH_SHORT).show();
                Fragment fragment = new ListFragment();

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with removing book from buzzlist, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
