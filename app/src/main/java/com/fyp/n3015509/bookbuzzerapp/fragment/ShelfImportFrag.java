package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;

import com.fyp.n3015509.Util.AppUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import java.util.ArrayList;
import java.util.List;

public class ShelfImportFrag extends DialogFragment {

    GoodreadsAPI util = new GoodreadsAPI();
    private OnFragmentInteractionListener mListener;

    public ShelfImportFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment ShelfImportFrag.
     */
    public static ShelfImportFrag newInstance() {
        ShelfImportFrag frag = new ShelfImportFrag();
        Bundle args = new Bundle();
        return frag;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){

        }

    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        new UserShelves().execute();
        return null;
    }


    private class UserShelves extends AsyncTask<Void, Void, ArrayList<GoodreadsShelf>> {

        UserShelves() {

        }

        @Override
        protected ArrayList<GoodreadsShelf> doInBackground(Void... params) {
            return util.getShelves(getContext());
        }

        protected void onPostExecute(final ArrayList<GoodreadsShelf> shelves) {
            //showProgress(false);
            List<String> listOfShelves = null;
            final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items

            // Set the dialog title
            for (GoodreadsShelf shelf : shelves) {
                String shelfInfo = shelf.getShelfName() + " (" + shelf.getBookNum() + " books)";
                listOfShelves.add(shelfInfo);
            }
            boolean[] itemChecked = new boolean[listOfShelves.size()];

            CharSequence[] cs = listOfShelves.toArray(new CharSequence[listOfShelves.size()]);
            itemChecked.equals(true);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title

            builder.setTitle("Select shelves to import")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(cs, itemChecked,
                            new OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        mSelectedItems.add(which);
                                    } else if (mSelectedItems.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog
                            ArrayList<GoodreadsShelf> options = new ArrayList<GoodreadsShelf>();
                            for (Object shelfName : mSelectedItems )
                            {
                                for (GoodreadsShelf shelf : shelves)
                                {
                                    if (shelfName.toString().contentEquals(shelf.getShelfName()))
                                    {
                                        options.add(shelf);
                                    }
                                }
                            }
                            AppUtil appuUtil = new AppUtil();
                            Boolean result = appuUtil.SaveShelves(getContext(), util, options);
                            SaveSharedPreference.setImported(getActivity(), true);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}

