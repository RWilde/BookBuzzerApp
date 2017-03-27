package com.fyp.n3015509.bookbuzzerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.fyp.n3015509.Util.GoodreadsUtil;
import com.fyp.n3015509.Util.LoginUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.goodreads.GoodreadsShelf;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button mLogoutButton = (Button) findViewById(R.id.logout_button);
        String token = SaveSharedPreference.getToken(getApplicationContext());
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtil.signout(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                setContentView(R.layout.activity_login);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Boolean imported = SaveSharedPreference.getImported(getApplicationContext());
        if (imported == false)
        {
            new UserShelves(getApplicationContext()).execute();
        }
    }

    private void ShowAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShelfImportFrag alertDialog = ShelfImportFrag.newInstance();
        alertDialog.show(fm, "import");
    }

    private class UserShelves extends AsyncTask<Void, Void, ArrayList<GoodreadsShelf>> {
        GoodreadsUtil util = new GoodreadsUtil();
        private final Context mContext;

        UserShelves(Context context) {
            this.mContext = context;
        }

        @Override
        protected ArrayList<GoodreadsShelf> doInBackground(Void... params) {
            ArrayList<GoodreadsShelf> shelves = util.getShelves(getApplicationContext());
            return shelves;
        }

        protected void onPostExecute(final ArrayList<GoodreadsShelf> shelves) {
            //showProgress(false);
            ArrayList<String> listOfShelves = new ArrayList<String>();
            final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items

            // Set the dialog title
            for (GoodreadsShelf shelf : shelves) {
                if(shelf.getBookNum() != 0) {
                    String shelfInfo = shelf.getShelfName() + " (" + shelf.getBookNum() + " books)";
                    listOfShelves.add(shelfInfo);
                }
            }
            boolean[] itemChecked = new boolean[listOfShelves.size()];

            CharSequence[] cs = listOfShelves.toArray(new CharSequence[listOfShelves.size()]);
            itemChecked.equals(true);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                            Boolean result = GoodreadsUtil.RetrieveSelectedShelves(mContext, options);
                            SaveSharedPreference.setImported(mContext, result);
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
