package com.fyp.n3015509.bookbuzzerapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.AuthorListFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.BookListFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.DownloadBookFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.HomeFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.NotificationsFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.SettingsFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ShelfImportFrag;
import com.fyp.n3015509.bookbuzzerapp.fragment.WatchListFragment;
import com.fyp.n3015509.bookbuzzerapp.other.CircleTransform;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;
import com.fyp.n3015509.bookbuzzerapp.tasks.WatchBooksService;
import com.fyp.n3015509.db.DBUtil;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static GoodreadsBook data;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    //private FloatingActionButton fab;

    private View mProgressView;
    private View mLoginFormView;

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static int bookIdFromBookFrag = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BUZZLIST = "buzzlist";
    private static final String TAG_BEELIST = "beelist";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SUGGESTIONS = "suggestions";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_WATCHED = "watched";

    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private GcmNetworkManager mGcmNetworkManager;
    private BroadcastReceiver mReceiver;
    public static final String TASK_TAG_PERIODIC = "periodic_task";
    long periodSecs = 86400L; // the task should be executed every 30 seconds
    long flexSecs = 100L; // the task can run as early as -15 seconds from the scheduled time

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String s = getIntent().getStringExtra("book");
        mHandler = new Handler();
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);

        PeriodicTask task = new PeriodicTask.Builder()
                .setService(WatchBooksService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(30L)
                .setFlex(flexSecs)
                .build();

        mGcmNetworkManager.schedule(task);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WatchBooksService.ACTION_DONE)) {
                    String tag = intent.getStringExtra(WatchBooksService.EXTRA_TAG);
                    int result = intent.getIntExtra(WatchBooksService.EXTRA_RESULT, -1);

                    String msg = String.format("DONE: %s (%d)", tag, result);
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (s != null) {
            GoodreadsBook book = data;
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = new DownloadBookFragment();

                    Bundle b = new Bundle();
                    fragment.setArguments(b);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, "");
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
        } else {

            //task.startBookWatchTask();

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            //fab = (FloatingActionButton) findViewById(R.id.fab);

            // Navigation view header
            navHeader = navigationView.getHeaderView(0);
            txtName = (TextView) navHeader.findViewById(R.id.name);
            txtWebsite = (TextView) navHeader.findViewById(R.id.website);
            imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
            imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

            // load toolbar titles from string resources
            activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

            // load nav menu header data
            loadNavHeader();

            // initializing navigation menu
            setUpNavigationView();

            if (navItemIndex != 7) {
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

            }
        }
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

        // Loading profile image
        Glide.with(this).load(" ")
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        int notificationCount = DBUtil.GetUnopenedNotifications(getApplicationContext());
        if (notificationCount > 0) {
            navigationView.getMenu().getItem(4).setActionView(R.layout.menu_dot);
        }
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
            //toggleFab();
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
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.frame);
        contentFrameLayout.removeAllViews();
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                BookListFragment bListFragment = new BookListFragment();
                return bListFragment;
            case 2:
                // movies fragment
                AuthorListFragment listFragment = new AuthorListFragment();
                return listFragment;
            case 3:
                // settings fragment
                WatchListFragment watchFragment = new WatchListFragment();
                return watchFragment;
            case 4:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 5:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            default:
                return new HomeFragment();
        }
    }

    public void switchContent(Fragment mfragment, int bookId) {
        Fragment fragment = mfragment;
        Bundle args = new Bundle();
        args.putInt("bookId", bookId);

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void switchToDownloadBook(Fragment mfragment, String book) {
        Fragment fragment = mfragment;
        Bundle args = new Bundle();
        args.putString("book", book);

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
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
                    case R.id.nav_watched:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_WATCHED;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_suggestions:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SUGGESTIONS;
                        break;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        new UserLogout(getApplicationContext()).execute();
                        return true;
                    case R.id.nav_settings:
                        //navItemIndex = 5;
                        //CURRENT_TAG = TAG_SETTINGS;
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
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        if(navItemIndex == 1)
        {
            getMenuInflater().inflate(R.menu.create_buzzlist, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 4) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }

        // Add SearchWidget.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.options_menu_main_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        return super.onCreateOptionsMenu(menu);


        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new UserLogout(getApplicationContext()).execute();
        }

        if (id == R.id.create_buzzlist) {
            buildNewBuzzlistDialog();
        }

        if (id == R.id.star_book) {
            new WatchBookTask(getApplicationContext()).execute();
        }

        if (id == R.id.unstar_book) {
            new StopWatchBookTask(getApplicationContext()).execute();
        }

//        if (id == R.id.download_book) {
//            new DownloadBookTask(getApplicationContext()).execute();
//        }

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

    public void buildNewBuzzlistDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(getApplicationContext());

        input.setSingleLine();
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                String value = input.getText().toString();
                boolean exist = DBUtil.CheckBuzzlistName(getApplicationContext(), value);
                if (!exist){
                    new MainActivity.BuzzlistCreate(getApplicationContext(), value).execute();
                    wantToCloseDialog = true;
                    Runnable mPendingRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // update the main content by replacing fragments
                            Fragment fragment = new BookListFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                            fragmentTransaction.commitAllowingStateLoss();
                        }
                    };

                    // If mPendingRunnable is not null, then add to the message queue
                    if (mPendingRunnable != null) {
                        mHandler.post(mPendingRunnable);
                    }
                }
                else
                {
                    input.setError("Oops! It looks like you've already used this name, please try another one.");
                    input.requestFocus();
                }

                if(wantToCloseDialog)
                    dialog.dismiss();
            }
        });
    }

    private void populateBookShelves() {
        new UserLists(getApplicationContext()).execute();
    }

    private void ShowAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShelfImportFrag alertDialog = ShelfImportFrag.newInstance();
        alertDialog.show(fm, "import");
    }

    private class SaveBookTask extends AsyncTask<Void, Void, Boolean> {

        private final GoodreadsBook mBook;
        private final String listName;
        private final Context mContext;
        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        boolean exist;

        SaveBookTask(GoodreadsBook book, String listName, Context cxt, Boolean listExist) {
            this.mBook = book;
            this.listName = listName;
            this.mContext = cxt;
            this.exist = listExist;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Adding to watch list...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                if (!exist) {
                    BookBuzzerAPI api = new BookBuzzerAPI();
                    Boolean dbSuccess = DBUtil.CreateBookAndList(mContext, mBook, listName);
                    Boolean apiSuccess = api.CreateBookAndList(mContext, mBook, listName);

//                    if (dbSuccess == false || apiSuccess == false) {
//                        return false;
//                    }
                }
                else
                {
                    BookBuzzerAPI api = new BookBuzzerAPI();
                    Boolean dbSuccess = DBUtil.AddBookToList(mContext, mBook, listName);
                    Boolean apiSuccess = api.AddBookToList(mContext, mBook, listName);

//                    if (dbSuccess == false || apiSuccess == false) {
//                        return false;
//                    }
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

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book downloaded and add to list", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class StopWatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        Context mContext;

        StopWatchBookTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Adding to watch list...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                BookBuzzerAPI api = new BookBuzzerAPI();
                Boolean dbSuccess = DBUtil.RemoveFromWatched(mContext, bookIdFromBookFrag);
               // String listName = DBUtil.findListForBook(mContext, bookIdFromBookFrag);
                Boolean apiSuccess = api.RemoveFromWatched(mContext, bookIdFromBookFrag);

//                if (dbSuccess == false || apiSuccess == false) {
//                    return false;
//                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book watched", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book to watch list, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class WatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        Context mContext;

        WatchBookTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress = new ProgressDialog(mContext);
//            progress.setMessage("Adding to watch list...");
//            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                BookBuzzerAPI api = new BookBuzzerAPI();
                Boolean dbSuccess = DBUtil.WatchBook(mContext, bookIdFromBookFrag);
                String listName = DBUtil.findListForBook(mContext, bookIdFromBookFrag);
                Boolean apiSuccess = api.WatchBook(mContext, bookIdFromBookFrag, listName);

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
            //progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book watched", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book to watch list, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UserShelves extends AsyncTask<Void, Void, ArrayList<GoodreadsShelf>> {
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;

        UserShelves(Context context) {
            this.mContext = context;
        }

        @Override
        protected ArrayList<GoodreadsShelf> doInBackground(Void... params) {
            ArrayList<GoodreadsShelf> shelves = util.getShelves(getApplicationContext());
            return shelves;
        }

        private ProgressDialog pdia;

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
            try
            {
                DBUtil.SaveBuzzlist(mContext, name);
                BookBuzzerAPI.SaveBuzzlist(mContext, name);
                SaveSharedPreference.setImported(getApplicationContext(), true);
            }catch (Exception e)
            {
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

    private class UserLogout extends AsyncTask<Void, Void, ArrayList<GoodreadsShelf>> {
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;

        UserLogout(Context context) {
            this.mContext = context;
        }

        @Override
        protected ArrayList<GoodreadsShelf> doInBackground(Void... params) {
            ArrayList<GoodreadsShelf> shelves = util.getShelves(getApplicationContext());
            return shelves;
        }

        private ProgressDialog pdia;

        protected void onPostExecute(final ArrayList<GoodreadsShelf> shelves) {
            //showProgress(false);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you wish to logout?")
                    .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            SaveSharedPreference.clearToken(getApplicationContext());
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            drawer.closeDrawers();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private class UserLists extends AsyncTask<Void, Void, ArrayList<GoodreadsBook>> {
        GoodreadsShelves util = new GoodreadsShelves();
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
            progress = new ProgressDialog(MainActivity.this);
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

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }

            if (aBoolean) {
                SaveSharedPreference.setImported(mContext, true);
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
