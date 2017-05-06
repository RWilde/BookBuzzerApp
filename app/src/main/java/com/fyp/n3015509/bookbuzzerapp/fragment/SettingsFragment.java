package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.APIs.LoginAPI;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.activity.LoginActivity;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.other.SearchSuggestionsProvider;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsLogin;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;
import com.fyp.n3015509.db.DBUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private SeekBar seekBar;
    private TextView textView;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        //clear history on clear search history button
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
        suggestions.clearHistory();

        final EditText newName = (EditText) view.findViewById(R.id.nameInput);
        Button changeNameBut = (Button) view.findViewById(R.id.okName);

        changeNameBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newName.getText() != null || newName.getText().toString().contentEquals("")){
                    new ChangeNameTask(getContext(), newName.getText().toString()).execute();
                }
            }
        });

        Button reimportButton = (Button) view.findViewById(R.id.reimport);

        reimportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserShelves(getContext()).execute();

            }
        });

        Button goodreadsAuth = (Button) view.findViewById(R.id.authorise);
        String id = SaveSharedPreference.getGoodreadsId(getContext());
        if (SaveSharedPreference.getGoodreadsId(getContext()) == null || id.contentEquals(""))
        {
            LinearLayout lay = (LinearLayout) view.findViewById(R.id.authGoodreadsSettings);
            lay.setVisibility(View.VISIBLE);
            goodreadsAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UserGoodreadsAuthenticateTask(getContext()).execute();
                }
            });
        }

        toggleButton(view);

        seekBar(view);

        return view;
    }

    private void toggleButton(View view) {
        Switch notificationSwitch = (Switch) view.findViewById(R.id.notSwitch);
        //set the switch to ON
        notificationSwitch.setChecked(true);
        //attach a listener to check for changes in state
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    SaveSharedPreference.setAllowNotifications(getContext(), true);
                }else{
                    SaveSharedPreference.setAllowNotifications(getContext(), false);
                }

            }
        });

    }

    private void seekBar(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
        textView = (TextView) view.findViewById(R.id.textView1);

        if (seekBar.getProgress() == 0) {
            textView.setText("Never ");
        } else if (seekBar.getProgress() == 1) {
            textView.setText(seekBar.getProgress() + " Day");
        } else {
            textView.setText(seekBar.getProgress() + " Days");
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                SaveSharedPreference.setPrefSyncFreq(getContext(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(progress + " Days");

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

    private void showUserSettings() {

        StringBuilder builder = new StringBuilder();

        builder.append("\n Username: "
                + SaveSharedPreference.getUserName(getContext()));

        builder.append("\n Send report:"
        );

        builder.append("\n Sync Frequency: "
                + SaveSharedPreference.getPrefSyncFreq(getContext()));

        // TextView settingsTextView = (TextView) view.findViewById(R.id.textUserSettings);
        //settingsTextView.setText(builder.toString());
    }

    private class UserShelves extends AsyncTask<Void, Void, Boolean>{
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;
        private ProgressDialog progress;
        ArrayList<GoodreadsShelf> shelves = new ArrayList<>();

        UserShelves(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Fetching...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            progress.dismiss();
            shelves = util.getShelves(getContext());
            if (shelves != null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }



        protected void onPostExecute(final Boolean success) {
            //showProgress(false);
            final ArrayList<String> listOfShelves = new ArrayList<>();
            final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
            if (success) {
                // Set the dialog title
                for (GoodreadsShelf shelf : shelves) {
                    if (shelf.getBookNum() != 0) {
                        String shelfInfo = shelf.getShelfName() + " (" + shelf.getBookNum() + " books)";
                        listOfShelves.add(shelfInfo);
                    }
                }
                final boolean[] itemChecked = new boolean[listOfShelves.size()];

                CharSequence[] cs = listOfShelves.toArray(new CharSequence[listOfShelves.size()]);
                itemChecked.equals(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                // Set the dialog title

                builder.setTitle("Select shelves to import")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(cs, itemChecked,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            // itemChecked[which] = isChecked;
                                            //String item = listOfShelves.get(which);
                                            mSelectedItems.add(listOfShelves.get(which));
                                        } else if (mSelectedItems.contains(listOfShelves.get(which))) {
                                            // Else, if the item is already in the array, remove it
                                            mSelectedItems.remove(listOfShelves.get(which));
                                        }
                                    }
                                })
                        // Set the action buttons
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                final ArrayList<GoodreadsShelf> options = new ArrayList<GoodreadsShelf>();
                                for (Object shelfName : mSelectedItems) {
                                    String selected = shelfName.toString();
                                    String[] parts = selected.split("\\(");
                                    String shelfPart = parts[0];
                                    shelfPart = shelfPart.replaceAll(" $", "");
                                    for (GoodreadsShelf shelf : shelves) {
                                        if (shelfPart.toString().contentEquals(shelf.getShelfName())) {
                                            options.add(shelf);
                                        }
                                    }
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                // Set the dialog title

                                AlertDialog.Builder warning = new AlertDialog.Builder(mContext);
                                warning.setTitle("To Continue...");
                                warning.setMessage("Please note, importing shelves may delete your existing Buzzlists. You can avoid this by changing the names of your Buzzlists.");
                                warning.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        UserGoodreadsShelves mGoodreadsShelfTask = new UserGoodreadsShelves(mContext, options);
                                        mGoodreadsShelfTask.execute((Void) null);
                                    }
                                });
                                warning.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog war = warning.create();
                                war.show();

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

    private class UserGoodreadsShelves extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        GoodreadsShelves util = new GoodreadsShelves();
        ArrayList<GoodreadsShelf> options = new ArrayList<GoodreadsShelf>();
        private ProgressDialog progress;

        UserGoodreadsShelves(Context context, ArrayList<GoodreadsShelf> options) {
            this.mContext = context;
            this.options = options;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Loading books from Goodreads, this may a couple of minutes...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<JSONObject> result = util.RetrieveSelectedShelves(mContext, options);
                boolean success = false;
                progress.dismiss();

                for (JSONObject shelf : result) {
                    success = BookBuzzerAPI.SaveShelf(shelf, mContext);
                }

                return success;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            // showProgress(false);

            if (aBoolean) {
                SaveSharedPreference.setImported(mContext, true);
            }

        }
    }

    private class ChangeNameTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final Context mContext;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        boolean exist;

        ChangeNameTask(Context cxt, String name) {
            this.mName = name;
            this.mContext = cxt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progress = new ProgressDialog(mContext);
            // progress.setMessage("Adding to watch list...");
            // progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                BookBuzzerAPI api = new BookBuzzerAPI();
                SaveSharedPreference.setUserName(mContext, mName);
                Boolean apiSuccess = api.ChangeUserName(mContext, mName);

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            progress.dismiss();

            if (success) {
                Toast.makeText(mContext, "Name changed", Toast.LENGTH_LONG).show();

                //finish();
            } else {
                Toast.makeText(mContext, "oops", Toast.LENGTH_LONG).show();


            }
        }
    }

    private class UserGoodreadsAuthenticateTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject login = new JSONObject();
        private ProgressDialog progress;
        private GoodreadsLogin gLogin = new GoodreadsLogin();
        Context mContext;

        UserGoodreadsAuthenticateTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Authenticating...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject login = new JSONObject();
                int goodreads_id = gLogin.GetGoodreadsAuthentication(getActivity());
                if (goodreads_id != 0) {
                    login.put("goodreads_id", goodreads_id);
                    SaveSharedPreference.setGoodreadsId(getContext(), Integer.toString(goodreads_id));
                    try {
                        BookBuzzerAPI.AddGoodreadsAccount(getContext(), login);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();
            if (success) {
                new UserShelves(getContext()).execute();
                SaveSharedPreference.setPrefGoodreadsAuth(getContext(), true);
                Toast.makeText(getContext(), "Goodreads account sync success", Toast.LENGTH_SHORT).show();

                SettingsFragment fragment = new SettingsFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

            } else {
                Toast.makeText(getContext(), "Error with connecting to your goodreads account", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            //  showProgress(false);
        }
    }

}
