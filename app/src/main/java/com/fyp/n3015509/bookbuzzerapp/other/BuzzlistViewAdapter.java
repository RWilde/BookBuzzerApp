package com.fyp.n3015509.bookbuzzerapp.other;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookListFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ListFragment;
import com.fyp.n3015509.dao.BookAdapter;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.db.DBUtil;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by tomha on 05-May-17.
 */

public class BuzzlistViewAdapter  extends BaseSwipeAdapter implements Filterable {
    private ArrayList<Buzzlist> buzz;
    private ArrayList<Buzzlist> filteredBuzz = new ArrayList();
    private FragmentActivity mContext;

    private TextView text;
    private ImageView image;
    private TextView currentTime;

    private RemoveBuzzlistTask mRemoveTask;


    public BuzzlistViewAdapter(FragmentActivity activity, ArrayList<Buzzlist> buzz) {
        this.mContext = activity;
        this.buzz = buzz;
        this.filteredBuzz = buzz;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.book_view_item, null);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Buzzlist buzzlist = getItem(position);

                Fragment fragment = new ListFragment();

                Bundle args = new Bundle();
                args.putInt("listId", buzzlist.getId());
                fragment.setArguments(args);

                FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                //fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();

            }
        });

        swipeLayout.findViewById(R.id.edit2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildChangeBuzzlistDialog(getItem(position).getName());
            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveTask = new RemoveBuzzlistTask(buzz.get(position).getName(), mContext);
                mRemoveTask.execute((Void) null);
            }
        });

        return v;
    }

    public void buildChangeBuzzlistDialog(final String originalName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        final EditText input = new EditText(mContext);
        input.setText(originalName);

        input.setSingleLine();
        FrameLayout container = new FrameLayout(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setTextColor(mContext.getResources().getColor(R.color.white));
        input.setLayoutParams(params);
        container.addView(input);
        alert.setTitle("Enter the name of your new buzzlist");

        alert.setView(container);

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                String newName = input.getText().toString();
                boolean exist = DBUtil.CheckBuzzlistName(mContext, newName);
                if (originalName.contentEquals(input.getText().toString())) {
                    input.setError("Oops! It looks like you've entered the same name, please press cancel if you don't want to change anything.");
                    input.requestFocus();
                } else if (!exist) {
                    new BuzzlistModify(mContext, originalName, newName).execute();
                    wantToCloseDialog = true;

                    // update the main content by replacing fragments
                    Fragment fragment = new BookListFragment();
                    FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, "Buzzlist");
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    input.setError("Oops! It looks like you've already used this name, please try another one.");
                    input.requestFocus();
                }

                if (wantToCloseDialog)
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void fillValues(int position, View convertView) {
        text = (TextView) convertView.findViewById(R.id.buzzList);
        text.setText(getItem(position).getName());
    }

    @Override
    public int getCount() {
        return filteredBuzz.size();
    }

    @Override
    public Buzzlist getItem(int position) {
        return filteredBuzz.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
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
                    results.values = buzz;
                    results.count = buzz.size();
                } else {
                    ArrayList<Buzzlist> filterResultsData = new ArrayList<>();
                    try {
                        for (Buzzlist data : buzz) {
                            if (data.getName() != null) {
                                if (data.getName().toLowerCase().startsWith(s)) {
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
                    filteredBuzz = new ArrayList<Buzzlist>();
                } else {
                    filteredBuzz = (ArrayList<Buzzlist>) filterResults.values;
                }
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    private class BuzzlistModify extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        private final String newName;
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;

        BuzzlistModify(Context context, String buzzName, String newName) {
            this.mContext = context;
            this.name = buzzName;
            this.newName = newName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DBUtil.ModifyBuzzlist(mContext, name, newName);
                BookBuzzerAPI.ModifyBuzzlist(mContext, name, newName);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private ProgressDialog pdia;

        protected void onPostExecute(final Boolean success) {
            //showProgress(false);

        }
    }

    private class RemoveBuzzlistTask extends AsyncTask<Void, Void, Boolean> {

        private String mName;
        private ProgressDialog progress;
        private FragmentActivity frag;
        private Handler mHandler;

        public RemoveBuzzlistTask(String name, FragmentActivity frag) {
            this.mName = name;
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
            try {
                Boolean dbSuccess = DBUtil.RemoveBuzzlist(mContext, mName);
                Boolean apiSuccess = BookBuzzerAPI.RemoveBuzzlist(mContext, mName);
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
                        Fragment fragment = new BookListFragment();

                        FragmentTransaction fragmentTransaction = frag.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, "Buzzlists");
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
}
