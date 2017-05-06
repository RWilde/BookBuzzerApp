package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.activity.SearchActvity;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DownloadBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DownloadBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadBookFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "book";
    private static final String ARG_PARAM2 = "param2";

    Gson gson = new Gson();

    // TODO: Rename and change types of parameters
    private String bookJson;
    GoodreadsBook mBook;
    private String mParam2;
    String spinner_item = null;
    SpinnerAdapter adapter;
    String[] buzzlistNames;

    private OnFragmentInteractionListener mListener;
    private ImageView image;
    private TextView title;

    public DownloadBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadBookFragment newInstance(String param1, String param2) {
        DownloadBookFragment fragment = new DownloadBookFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookJson = getArguments().getString(ARG_PARAM1);
            mBook = MainActivity.data;
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        image = (ImageView) view.findViewById(R.id.book_cover);
        image.setImageBitmap(mBook.getImage());

        title = (TextView) view.findViewById(R.id.title);
        title.setText(mBook.getTitle());

        String kindlePrice = "";
        String paperPrice = "";
        String hardPrice = "";
        if (mBook.getKindlePrice() != 0.0)
            kindlePrice = "£" + Double.toString(mBook.getKindlePrice());
        else
            kindlePrice = "Not available";
        if (mBook.getPaperbackPrice() != 0.0)
            paperPrice = "£" + Double.toString(mBook.getPaperbackPrice());
        else
            paperPrice = "Not available";
        if (mBook.getHardcoverPrice() != 0.0)
            hardPrice = "£" + Double.toString(mBook.getHardcoverPrice());
        else
            hardPrice = "Not available";


        TextView kindle = (TextView) view.findViewById(R.id.kindle_price);
        kindle.setText(kindlePrice);

        TextView paper = (TextView) view.findViewById(R.id.paper_price);
        paper.setText(paperPrice);

        TextView hard = (TextView) view.findViewById(R.id.hard_price);
        hard.setText(hardPrice);
        TextView author = (TextView) view.findViewById(R.id.author);
        StringBuilder b = new StringBuilder();
        for (GoodreadsAuthor a : mBook.getAuthors()) {
            if (b.length() != 0) {
                b.append(", ");
            }
            b.append(a.getName());
        }
        author.setText(b);

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(mBook.getDescription());

        TextView isbn = (TextView) view.findViewById(R.id.isbn);
        isbn.setText(mBook.getIsbn());

        TextView pages = (TextView) view.findViewById(R.id.pages);
        pages.setText(mBook.getNumPages() + "pages ");

        TextView edition = (TextView) view.findViewById(R.id.format);
        edition.setText("(" + mBook.getEditionInformation() + ")");

        TextView series = (TextView) view.findViewById(R.id.series);
        String[] seriesString = mBook.getTitle().split("\\(");
        String[] justSeries = seriesString[1].split("\\,");

        series.setText(justSeries[0]);

        TextView published = (TextView) view.findViewById(R.id.publishing_details);
        edition.setText(mBook.getPublisher() + ", " + mBook.getReleaseDate());

        TextView shelf = (TextView) view.findViewById(R.id.bookshelf);
        edition.setText("shelF TODO");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.download_book) {
            DBUtil util = new DBUtil();
            ArrayList<Buzzlist> buzz = util.GetBuzzlist(getContext());
            buzzlistNames = new String[buzz.size()];
            ArrayList<String> buzzListNameStrings = new ArrayList<>();
            for (Buzzlist b : buzz) {
                buzzListNameStrings.add(b.getName());
            }

            buzzlistNames = buzzListNameStrings.toArray(buzzlistNames);
            adapter=new SpinnerAdapter(getContext());

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.buzzlist_shelf_selector);
            dialog.setCancelable(true);

            // set the custom dialog components - text, image and button
            final Spinner spinner = (Spinner) dialog.findViewById(R.id.buzzlistSpinner);
            final EditText edittext = (EditText) dialog.findViewById(R.id.new_buzzlist);
            Button save = (Button) dialog.findViewById(R.id.save);
            Button create = (Button) dialog.findViewById(R.id.create);

            if (buzz.size() ==0)
            {
                TextView text = (TextView) dialog.findViewById(R.id.buzzlist) ;
                View header = (View) dialog.findViewById(R.id.header_div);

                header.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
            }



            spinner.setAdapter((android.widget.SpinnerAdapter) adapter);
            final String[] finalBuzzlistNames = buzzlistNames;

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    // TODO Auto-generated method stub
                    spinner_item = finalBuzzlistNames[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }
            });

            save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (spinner_item != null)
                    {
                        SaveBookTask saveTask = new SaveBookTask(mBook, spinner_item, getContext(), true);
                        saveTask.execute((Void) null);
                        dialog.dismiss();
                    }
                    else
                    {
                        //display warning tha tnothing selected
                        edittext.setError(getString(R.string.error_invalid_email));
                    }
                }
            });
            create.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (edittext.getText() != null)
                    {
                        SaveBookTask saveTask = new SaveBookTask(mBook, spinner_item, getContext(), false);
                        saveTask.execute((Void) null);
                        dialog.dismiss();
                    }
                    else
                    {
                        //display warning tha tnothing selected
                        edittext.setError(getString(R.string.error_invalid_email));
                    }
                }
            });
            dialog.show();
        }
        return true;
    }


    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        try {
            menu.findItem(R.id.download_book).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
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

    public class SpinnerAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;

        public SpinnerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return buzzlistNames.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                mInflater = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.buzzlist_shelf_textview, null);
                holder = new ListContent();
                holder.text = (TextView) v.findViewById(R.id.textView1);

                v.setTag(holder);
            } else {

                holder = (ListContent) v.getTag();
            }

            holder.text.setText(buzzlistNames[position]);

            return v;
        }
    }

    static class ListContent {

        TextView text;
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
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
                    String currentDate = df.format(c.getTime());

                    mBook.setReleaseDate(currentDate);
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
}
