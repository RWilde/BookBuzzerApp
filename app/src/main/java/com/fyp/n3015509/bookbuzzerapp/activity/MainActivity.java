package com.fyp.n3015509.bookbuzzerapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fyp.n3015509.Util.GoodreadsUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookListFragmentFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.HomeFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ListFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.NotificationsFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.SettingsFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ShelfImportFrag;
import com.fyp.n3015509.bookbuzzerapp.other.CircleTransform;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private View mProgressView;
    private View mLoginFormView;

    // urls to load navigation header background image
    // and profile image
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BUZZLIST = "buzzlist";
    private static final String TAG_BEELIST = "beelist";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SUGGESTIONS = "suggestions";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {

            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }


        Boolean imported = SaveSharedPreference.getImported(getApplicationContext());
        String userId = SaveSharedPreference.getGoodreadsId(getApplicationContext());
        if (imported == false && !userId.contentEquals("")) {
            new UserShelves(getApplicationContext()).execute();
        }

       // populateBookShelves();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Hello " + SaveSharedPreference.getUserName(getApplicationContext()));
        txtWebsite.setText("Welcome to BookBuzzer");

        // loading header background image
//        Glide.with(this).load(urlNavHeaderBg)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(" ")
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
           // mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                BookListFragmentFragment bListFragment = new BookListFragmentFragment();
                return bListFragment;
            case 2:
                // movies fragment
                ListFragment listFragment = new ListFragment();
                return listFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_buzzlists:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_BUZZLIST;
                        break;
                    case R.id.nav_beelists:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_BEELIST;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_suggestions:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SUGGESTIONS;
                        break;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        SaveSharedPreference.clearToken(getApplicationContext());
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_settings:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                        // launch new intent instead of loading fragment

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();

        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }


    private void populateBookShelves() {
        new UserLists(getApplicationContext()).execute();
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
            final ArrayList<String> listOfShelves = new ArrayList<String>();
            final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items

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
                            ArrayList<GoodreadsShelf> options = new ArrayList<GoodreadsShelf>();
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

                            UserGoodreadsShelves mGoodreadsShelfTask = new UserGoodreadsShelves(getApplicationContext(), options);
                            mGoodreadsShelfTask.execute((Void) null);
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

    private class UserLists extends AsyncTask<Void, Void, ArrayList<GoodreadsBook>> {
        GoodreadsUtil util = new GoodreadsUtil();
        private Context mContext;

        UserLists(Context context) {
            this.mContext = context;
        }

        @Override
        protected ArrayList<GoodreadsBook> doInBackground(Void... params) {
            ArrayList<GoodreadsBook> shelves = util.getBooks(getApplicationContext());
            return shelves;
        }
    }

    private class UserGoodreadsShelves extends AsyncTask<Void, Void, Boolean>
    {
        private Context mContext;
        GoodreadsUtil util = new GoodreadsUtil();
        ArrayList<GoodreadsShelf> options = new ArrayList<GoodreadsShelf>();

        UserGoodreadsShelves(Context context, ArrayList<GoodreadsShelf> options)
        {
            this.mContext = context;
            this.options = options;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = util.RetrieveSelectedShelves(mContext, options);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
           // showProgress(false);

            if (aBoolean)
            {//SaveSharedPreference.setImported(mContext, true);
                 }

        }


        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

}
