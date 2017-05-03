package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.other.ArraySwipeAdapterSample;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BookListFragment extends ListFragment implements OnItemClickListener {
    View rootView;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private BookListFragment.OnFragmentInteractionListener mListener;
    ArrayList<Buzzlist> buzzlist = new ArrayList<>();

    public BookListFragment() {
    }

    public static BookListFragment newInstance(String param1, String param2) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);
        buzzlist = DBUtil.GetBuzzlist(getActivity());
        if (buzzlist != null || !buzzlist.isEmpty()) {
            final ListView listv = (ListView) rootView.findViewById(R.id.book_list);

            ArrayList<String> buzzlistNames = new ArrayList<String>();
            String[] values = new String[buzzlist.size()];

            for (Buzzlist buzz : buzzlist) {
                buzzlistNames.add(buzz.getName());
            }
            values = buzzlistNames.toArray(values);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
            listv.setAdapter(adapter);

            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Buzzlist buzz = buzzlist.get(position);

                    Fragment fragment = new ListFragment();

                    Bundle args = new Bundle();
                    args.putInt("listId", buzz.getId());
                    fragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment);
                    //fragmentTransaction.addToBackStack(null);

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            });
            listv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("ListView", "OnTouch");
                    return false;
                }
            });
            final String[] finalValues = values;
            listv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    buildChangeBuzzlistDialog(finalValues[position]);
                    return true;
                }
            });
            listv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Log.e("ListView", "onScrollStateChanged");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });


            // listv.setOnItemClickListener(this);
        }
        return rootView;
    }

    public void buildChangeBuzzlistDialog(final String originalName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        final EditText input = new EditText(getContext());
        input.setText(originalName);

        input.setSingleLine();
        FrameLayout container = new FrameLayout(this.getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setTextColor(getResources().getColor(R.color.white));
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
                boolean exist = DBUtil.CheckBuzzlistName(getContext(), newName);
                if (originalName.contentEquals(input.getText().toString())) {
                    input.setError("Oops! It looks like you've entered the same name, please press cancel if you don't want to change anything.");
                    input.requestFocus();
                } else if (!exist) {
                    new BuzzlistModify(getContext(), originalName, newName ).execute();
                    wantToCloseDialog = true;

                    // update the main content by replacing fragments
                    Fragment fragment = new BookListFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Buzzlist buzz = buzzlist.get(position);

        Fragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putInt("listId", buzz.getId());
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        //fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class BuzzlistCreate extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;

        BuzzlistCreate(Context context, String buzzName) {
            this.mContext = context;
            this.name = buzzName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DBUtil.SaveBuzzlist(mContext, name);
                BookBuzzerAPI.SaveBuzzlist(mContext, name);
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
}
