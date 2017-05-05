package com.fyp.n3015509.bookbuzzerapp.tasks;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.Util.AppUtil;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.enums.NotificationTypes;
import com.fyp.n3015509.db.DBUtil;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tomha on 04-May-17.
 */

public class SyncService extends GcmTaskService {
    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_RESULT = "extra_result";

    @Override
    public int onRunTask(TaskParams taskParams) {
        int result = GcmNetworkManager.RESULT_SUCCESS;
        String tag = taskParams.getTag();

        try {
            result = syncWithAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_DONE);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_RESULT, result);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(intent);

        // Return one of RESULT_SUCCESS, RESULT_FAILURE, or RESULT_RESCHEDULE
        return result;
    }

    private int syncWithAPI() {
        DBUtil db = new DBUtil();
        BookBuzzerAPI api = new BookBuzzerAPI();

        JSONObject o = api.Sync(getApplicationContext());

        if (o != null) {
            DBUtil.InsertFromAPI(o, getApplicationContext());
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
