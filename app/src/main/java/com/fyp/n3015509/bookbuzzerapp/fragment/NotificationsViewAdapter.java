package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
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
import com.fyp.n3015509.Util.APIUtil;
import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.NotificationTypes;

/**
 * Created by tomha on 14-Apr-17.
 */

class NotificationsViewAdapter extends BaseSwipeAdapter {
    private FragmentActivity mContext;
    private String[] mBookName;
    private Boolean[] mNotified;
    private NotificationTypes[] mNotTypes;
    private Integer[] mBookIds;

    private TextView text;
    private ImageView image;
    private TextView currentTime;

    private MarkNotificationAsReadTask mNotificationTask;
    private RemoveNotificationTask mRemoveTask;

    public NotificationsViewAdapter(FragmentActivity activity, String[] bookNameArray, Boolean[] notifiedArray, NotificationTypes[] notTypesArray, Integer[] bookIdsArray) {
        this.mContext = activity;
        this.mBookIds = bookIdsArray;
        this.mBookName = bookNameArray;
        this.mNotified = notifiedArray;
        this.mNotTypes = notTypesArray;
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

        if (mNotified[position] == true)
            v.setBackgroundColor(Color.RED);

        else {
            v.setBackgroundColor(Color.BLUE);
        }

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationTask = new NotificationsViewAdapter.MarkNotificationAsReadTask(mBookIds[position], mContext);
                mNotificationTask.execute((Void) null);

            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveTask = new NotificationsViewAdapter.RemoveNotificationTask(mBookIds[position], mContext);
                mRemoveTask.execute((Void) null);
            }
        });

        return v;
    }


    @Override
    public void fillValues(int position, View convertView) {
//        image = (ImageView) convertView.findViewById(R.id.imageView);
//        image.setImageBitmap(images[position]);
        String notification = "";
        String date = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        }

        switch (mNotTypes[position]) {
            case AVALIABLE:
                notification = "The book " + mBookName[position] + " is now available for purchase";
                break;
            case PREORDER:
                notification = "The book " + mBookName[position] + " will be released next week, and is available for pre-order";
                break;
        }

        text = (TextView) convertView.findViewById(R.id.notification);
        text.setText(notification);

        currentTime = (TextView) convertView.findViewById(R.id.date);
        currentTime.setText(date);
    }

    @Override
    public int getCount() {
        return mBookIds.length;
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

        private final int mBook;
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public RemoveNotificationTask(Integer id, FragmentActivity frag) {
            this.mBook = id;
            this.frag = frag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Removing notification...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                Boolean dbSuccess = DBUtil.RemoveNotification(mContext, mBook);
                Boolean apiSuccess = APIUtil.RemoveNotification(mContext, mBook, SaveSharedPreference.getToken(mContext));
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
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public MarkNotificationAsReadTask(Integer id, FragmentActivity frag) {
            this.mBook = id;
            this.frag = frag;
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
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                Boolean dbSuccess = DBUtil.MarkNotificationAsRead(mContext, mBook);
                Boolean apiSuccess = APIUtil.MarkNotificationAsRead(mContext, mBook, SaveSharedPreference.getToken(mContext));
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
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        ViewBook(mBook);
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
}
