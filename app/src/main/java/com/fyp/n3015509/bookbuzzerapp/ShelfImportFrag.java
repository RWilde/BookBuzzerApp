package com.fyp.n3015509.bookbuzzerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fyp.n3015509.Util.GoodreadsUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.goodreads.GoodreadsShelf;
import com.fyp.n3015509.goodreadsapi.GoodreadsShelves;

import java.util.ArrayList;
import java.util.List;

public class ShelfImportFrag extends DialogFragment {


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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<GoodreadsShelf> shelves = GoodreadsUtil.getShelves(getContext());
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
                        Boolean result = GoodreadsUtil.RetrieveSelectedShelves(getActivity(), options);
                        SaveSharedPreference.setImported(getActivity(), result);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }


}
