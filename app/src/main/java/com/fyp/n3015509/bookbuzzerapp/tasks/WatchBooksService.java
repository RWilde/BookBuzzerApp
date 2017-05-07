package com.fyp.n3015509.bookbuzzerapp.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.Util.AppUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.enums.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.db.DBUtil;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            checkForPrices();
            checkForNewInSeries(getApplicationContext());
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
        BookBuzzerAPI api = new BookBuzzerAPI();

        String available = "";
        String preorder = "";
        ArrayList<BuzzNotification> notifications = db.CreateWatchNotifications(this);
        api.SaveNotifications(getApplicationContext(), notifications);

        int availableCount = 0;
        int preorderCount = 0;

        for (BuzzNotification b : notifications) {
            if (b.getType() == NotificationTypes.AVALIABLE) {
                availableCount++;
            } else if (b.getType() == NotificationTypes.PREORDER) {
                preorderCount++;
            }
        }

        if (notifications != null) {
            String title = "BookBuzzer";
            if (availableCount > 0) {
                available = availableCount + " new books out ";
            }
            if (preorderCount > 0) {
                if (available.contentEquals("")) {
                    preorder = " with ";
                }
                preorder += preorderCount + "avaliable for preorder ";

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

    public int checkForPrices() {
        AppUtil app = new AppUtil();
        DBUtil db = new DBUtil();
        BookBuzzerAPI api = new BookBuzzerAPI();

        ConcurrentHashMap<String, ArrayList<PriceChecker>> results = app.GetLatestPrices(getApplicationContext());
        ArrayList<BuzzNotification> buzzList = db.CreatePriceCheckerNotifications(getApplicationContext(), results);

        api.SaveNotifications(getApplicationContext(), buzzList);
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

                setNotififcation(title, body, subject);
            }
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public int checkForNewInSeries(Context cxt) {
        GoodreadsAPI goodreadsAPI = new GoodreadsAPI();
        DBUtil db = new DBUtil();
        BookBuzzerAPI api = new BookBuzzerAPI();

        ArrayList<Integer> WatchedGoodreadIds = DBUtil.getGoodreadIdsFromWatchList(cxt);
        ArrayList<BuzzNotification> buzzList = new ArrayList<>();

        for (int i : WatchedGoodreadIds) {
            //turn goodreads id to work id
            // https://www.goodreads.com/book/id_to_work_id/17167166?key=8vvXeL81U1l6yxnUT1c9Q
            int workId = goodreadsAPI.getWorkId(i);

            //get series using returned work id
            //https://www.goodreads.com/work/21581860/series?format=xml&key=8vvXeL81U1l6yxnUT1c9Q
            //get series id
            if (workId != 0) {
                int seriesId = goodreadsAPI.getSeriesId(workId);

                //give id to bookbuzzer api which will download dom and find "expected publication"
                JSONObject o = api.GetExpectedPub(cxt, seriesId);
                String author = "";
                try {
                    author = o.getString("author");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (o.length() != 0) {
                    try {
                        JSONArray array = o.getJSONArray("books");
                        for(i = 0; i < array.length(); i++)
                        {
                            JSONObject book = array.getJSONObject(i);
                            String name = book.getString("name");
                            String pub = book.getString("pub");

                            if (pub.contains("expected publication")) {
                                GoodreadsBook gBook = null;

                                gBook = goodreadsAPI.DownloadBook(cxt, name, author);


                                int colId = DBUtil.SaveBook(cxt, gBook);
                                BuzzNotification buzz = new BuzzNotification();
                                buzz.setBookId(colId);
                                buzz.setAuthorName(author);
                                buzz.setGoodreadsId(gBook.getId());
                                buzz.setRead(false);
                                buzz.setType(NotificationTypes.FUTURE);
                                int year = Integer.parseInt(pub.replaceAll("[\\D]", ""));
                                buzz.setMessage("There's a new book avaliable in a series that you've read: " + name + " release: " + year);

                                buzzList.add(buzz);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        db.SaveExpectedNotifications(this, buzzList);
        api.SaveNotifications(getApplicationContext(), buzzList);
        if (buzzList != null) {
            if (buzzList.size() > 0) {
                String title = "BookBuzzer";
                String subject = "There are new books you might be interested in!";
                String body = "";
                if (buzzList.size() == 1) {
                    body = "There is 1 new book you might want to look at!";
                } else {
                    body = "There are " + buzzList.size() + " new books being released in the future you might like!";
                }

                setNotififcation(title, body, subject);
            }
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public void setNotififcation(String title, String body, String subject) {
        if (SaveSharedPreference.getAllowNotifications(getApplicationContext())) {
            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder
                    (getApplicationContext()).setContentTitle(title).setContentText(body).
                    setContentTitle(subject).setSmallIcon(R.drawable.blue_logo_small).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        }
    }
}
