package com.fyp.n3015509.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.ArrayList;

/**
 * Created by tomha on 13-Apr-17.
 */

public class WatchBooksService extends GcmTaskService {
    Context mContext;

    public WatchBooksService(Context ctx) {
        this.mContext = ctx;
    }


    @Override
    public int onRunTask(TaskParams taskParams) {

        // Default result is success.
        int result = GcmNetworkManager.RESULT_SUCCESS;

        // Choose method based on the tag.
//        if (MainActivity.TASK_TAG_WIFI.equals(tag)) {
//            result = doWifiTask();
//        } else if (MainActivity.TASK_TAG_CHARGING.equals(tag)) {
//            result = doChargingTask();
//        } else if (MainActivity.TASK_TAG_PERIODIC.equals(tag)) {
        result = doPeriodicTask();
        //  }

        // Create Intent to broadcast the task information.
//        Intent intent = new Intent();
//        intent.setAction(ACTION_DONE);
//        intent.putExtra(EXTRA_TAG, tag);
//        intent.putExtra(EXTRA_RESULT, result);
//
//        // Send local broadcast, running Activities will be notified about the task.
//        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
//        manager.sendBroadcast(intent);

        // Return one of RESULT_SUCCESS, RESULT_FAILURE, or RESULT_RESCHEDULE
        return result;
    }

    private int doPeriodicTask() {
        DBUtil db = new DBUtil();
        String available = null;
        String preorder = null;
        Integer[] notifications = db.createNotifications(mContext);

        if (notifications != null) {
            String title = "BookBuzzer";
            if (notifications[0] > 0) {
                available = notifications[0] + " new books out ";
            }
            if (notifications[1] > 0) {
                if (available != null) {
                    preorder = " with ";
                }
                preorder += notifications[1] + "avaliable for preorder ";

            }

            String subject = "You have new books to look at";
            String body = "There are " + available + preorder;

            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder
                    (getApplicationContext()).setContentTitle(title).setContentText(body).
                    setContentTitle(subject).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
