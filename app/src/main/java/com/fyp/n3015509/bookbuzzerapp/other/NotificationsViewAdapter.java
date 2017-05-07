package com.fyp.n3015509.bookbuzzerapp.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.Calendar;
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
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.NotificationsFragment;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.enums.NotificationTypes;

import java.util.ArrayList;

/**
 * Created by tomha on 14-Apr-17.
 */

public class NotificationsViewAdapter extends BaseSwipeAdapter {
    ArrayList<BuzzNotification> notifications;
    private FragmentActivity mContext;

    private TextView text;
    private ImageView image;
    private TextView currentTime;

    private MarkNotificationAsReadTask mNotificationTask;
    private RemoveNotificationTask mRemoveTask;
    private PriceChecker priceTask;

    public NotificationsViewAdapter(FragmentActivity activity, ArrayList<BuzzNotification> notifications) {
        this.mContext = activity;
        this.notifications = notifications;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.notification_view_item, null);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

//        if (notifications.get(position).getRead() == true)
           v.setBackgroundResource(R.drawable.notification_false_selector);
//
//        else {
        //    v.setBackgroundResource(R.drawable.notification_true_selector);
   //     }

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isbn = DBUtil.getIsbnByGoodreadsId(mContext, notifications.get(position).getGoodreadsId());

                priceTask = new PriceChecker(mContext, isbn);
                priceTask.execute((Void) null);

                mNotificationTask = new MarkNotificationAsReadTask(notifications.get(position).getGoodreadsId(), mContext, notifications.get(position).getType(), notifications.get(position).getGoodreadsId());
                mNotificationTask.execute((Void) null);

            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveTask = new RemoveNotificationTask(notifications.get(position).getGoodreadsId(), mContext, notifications.get(position).getType());
                mRemoveTask.execute((Void) null);
            }
        });

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {

        image = (ImageView) convertView.findViewById(R.id.notImageView);
        image.setImageBitmap(notifications.get(position).getImage());

        text = (TextView) convertView.findViewById(R.id.notification);
        text.setText(notifications.get(position).getMessage());

        String date = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        }

        currentTime = (TextView) convertView.findViewById(R.id.date);
        currentTime.setText(date);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class RemoveNotificationTask extends AsyncTask<Void, Void, Boolean> {

        private int mBook;
        private NotificationTypes mType;
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public RemoveNotificationTask(Integer id, FragmentActivity frag, NotificationTypes type) {
            this.mBook = id;
            this.frag = frag;
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress = new ProgressDialog(mContext);
//            progress.setMessage("Removing notification...");
//            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Boolean dbSuccess = DBUtil.RemoveNotification(mContext, mBook);
                Boolean apiSuccess = BookBuzzerAPI.RemoveNotification(mContext, mBook, mType);

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
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = new NotificationsFragment();

                        FragmentTransaction fragmentTransaction = frag.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, "Notifications");
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                };

                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                }

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with removing notification, please try agasin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MarkNotificationAsReadTask extends AsyncTask<Void, Void, Boolean> {

        private final int mBook;
        private final NotificationTypes mType;
        private final Integer mGoodreadsId;
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public MarkNotificationAsReadTask(Integer id, FragmentActivity frag, NotificationTypes type, Integer goodreadsId) {
            this.mGoodreadsId = goodreadsId;
            this.mBook = id;
            this.frag = frag;
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Opening Book...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Boolean dbSuccess = DBUtil.MarkNotificationAsRead(mContext, mBook, mType);
                Boolean apiSuccess = BookBuzzerAPI.MarkNotificationAsRead(mContext, mGoodreadsId, mType);
                if (dbSuccess == false || apiSuccess == false) {
                    return false;
                }
                MainActivity.bookIdFromBookFrag = 7;

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
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ViewBook(mBook);
                    }
                };

                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                }

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with removing notification, please try again", Toast.LENGTH_SHORT).show();
            }
        }

        private void ViewBook(int bookId) {
            Fragment fragment = new BookFragment();

            Bundle args = new Bundle();
            args.putInt("bookId", bookId);

            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
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

            DBUtil.CheckAgainstDb(mContext, util.RunPriceChecker(mContext, mIsbn), mIsbn);
            return true;
        }


        protected void onPostExecute() {
            //showProgress(false);

        }
    }
}
