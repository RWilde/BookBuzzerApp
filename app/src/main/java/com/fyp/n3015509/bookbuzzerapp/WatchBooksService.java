package com.fyp.n3015509.bookbuzzerapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.db.DBUtil;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tomha on 13-Apr-17.
 */

public class WatchBooksService extends GcmTaskService {

    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_RESULT = "extra_result";

    @Override
    public int onRunTask(TaskParams taskParams) {

        // Default result is success.
        int result = GcmNetworkManager.RESULT_SUCCESS;
        String tag = taskParams.getTag();

        // Choose method based on the tag.
//        if (MainActivity.TASK_TAG_WIFI.equals(tag)) {
//            result = doWifiTask();
//        } else if (MainActivity.TASK_TAG_CHARGING.equals(tag)) {
//            result = doChargingTask();
//        } else if (MainActivity.TASK_TAG_PERIODIC.equals(tag)) {
        try {
            result = checkForNewBooks();
            // checkForPrices();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  }

        // Create Intent to broadcast the task information.
        Intent intent = new Intent();
        intent.setAction(ACTION_DONE);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_RESULT, result);
//
//        // Send local broadcast, running Activities will be notified about the task.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(intent);

        // Return one of RESULT_SUCCESS, RESULT_FAILURE, or RESULT_RESCHEDULE
        return result;
    }

    private int checkForNewBooks() {
        DBUtil db = new DBUtil();
        String available = "";
        String preorder = "";
        Integer[] notifications = db.createNotifications(this);

        if (notifications != null) {
            String title = "BookBuzzer";
            if (notifications[0] > 0) {
                available = notifications[0] + " new books out ";
            }
            if (notifications[1] > 0) {
                if (available.contentEquals("")) {
                    preorder = " with ";
                }
                preorder += notifications[1] + "avaliable for preorder ";

            }
            if (!available.contentEquals("") || !preorder.contentEquals("")) {
                String subject = "You have new books to look at";
                String body = "There are " + available + preorder;

                setNotififcation(title, body, subject);
            }
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public int checkForPrices(Context ctx) {
        DBUtil db = new DBUtil();
        BookBuzzerAPI bb = new BookBuzzerAPI();

        String[] isbns = db.GetISBNFromWatch(ctx);
        ConcurrentHashMap<String, ArrayList<PriceChecker>> results = new ConcurrentHashMap<>();
        if (isbns != null) {
            for (String isbn : isbns) {
                ArrayList<PriceChecker> p =bb.RunPriceChecker(ctx, isbn);
                results.put(isbn, p);
            }
            db.CreatePriceCheckerNotifications(ctx, results);

            if (results != null) {
                if (results.size() > 0) {

                    String title = "BookBuzzer";

                    String subject = "Books you are watching are cheaper!";
                    String body = "";
                    if (results.size() == 1) {
                        body = "There is 1 book you are watching that is cheaper!";
                    } else {
                        body = "There are " + results.size() + " books you are watching that are cheaper!";
                    }

                   // setNotififcation(title, body, subject);
                }
            }
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public void setNotififcation(String title, String body, String subject)
    {
        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder
                (getApplicationContext()).setContentTitle(title).setContentText(body).
                setContentTitle(subject).setSmallIcon(R.drawable.blue_logo_small).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);
    }
}
