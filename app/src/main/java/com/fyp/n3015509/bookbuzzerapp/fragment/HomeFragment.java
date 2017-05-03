package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.other.SearchViewAdapter;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.fyp.n3015509.bookbuzzerapp.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private Button scanBtn;
    ArrayList<Buzzlist> buzzlist = new ArrayList<>();
    ArrayList<GoodreadsBook> book = new ArrayList<>();
    ArrayList<GoodreadsAuthor> author = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);

        scanBtn = (Button)view.findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.scan_button){
                    IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                    scanIntegrator.initiateScan();
                }
            }
        });

       // buzzlist = DBUtil.GetBuzzlist(getActivity());
       // book = DBUtil.GetBooks(getActivity());
       // author = DBUtil.GetAuthors(getActivity());

        createBuzzlistList(view);
        createAuthorList(view);
        createBookList(view);

        return view;
    }

    private void createBookList(View view) {
        final ListView listv = (ListView) view.findViewById(R.id.book_list);
        if (book != null && book.size() > 0) {
            ArrayList<String> buzzlistNames = new ArrayList<String>();
            String[] values = new String[book.size()];
            int count = 0;
            for (GoodreadsBook buzz : book) {
                if (count < 4) {
                buzzlistNames.add(buzz.getTitle());
                count++;
                }
            }
            values = buzzlistNames.toArray(values);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);

            listv.setAdapter(adapter);
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GoodreadsBook buzz = book.get(position);

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
        }
        else
        {
            listv.setVisibility(View.GONE);
            TextView text = (TextView) view.findViewById(R.id.book_empty);
            text.setText("Opps! Looks like you don't have any watched books. Why don't you add one?");
        }
    }

    private void createBuzzlistList(View view) {
        if (buzzlist != null && buzzlist.size() > 0) {
            final ListView listv = (ListView) view.findViewById(R.id.buzz_list);

            ArrayList<String> buzzlistNames = new ArrayList<String>();
            String[] values = new String[buzzlist.size()];
            int count = 0;
            for (Buzzlist buzz : buzzlist) {
                if (count < 4) {
                    buzzlistNames.add(buzz.getName());
                    count++;
                }
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
        }
        else
        {
            TextView text = (TextView) view.findViewById(R.id.buzz_empty);
            text.setText("Opps! Looks like you don't have buzzlists. Why don't you create one?");
        }
    }

    private void createAuthorList(View view) {
        if (author != null && author.size() > 0) {
            final ListView listv = (ListView) view.findViewById(R.id.author_list);

            ArrayList<String> buzzlistNames = new ArrayList<String>();
            String[] values = new String[author.size()];
            int count = 0;
            for (GoodreadsAuthor buzz : author) {
                if (count < 4 && buzz.getName() != null) {
                buzzlistNames.add(buzz.getName());
                count++;
                 }
            }
            values = buzzlistNames.toArray(values);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);

            listv.setAdapter(adapter);
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GoodreadsAuthor buzz = author.get(position);

                    Fragment fragment = new ListFragment();

                    Bundle args = new Bundle();
                    args.putInt("authorId", buzz.getColumnId());
                    fragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment);
                    //fragmentTransaction.addToBackStack(null);

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            });
        }
        else
        {
            TextView text = (TextView) view.findViewById(R.id.author_empty);
            text.setText("Opps! Looks like you don't haven't found any authors yet. Why don't you search for one, using the search button at the top of the screen.");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve result of scanning - instantiate ZXing object
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //check we have a valid result
        if (scanningResult != null) {
            //get content from Intent Result
            String scanContent = scanningResult.getContents();
            //get format name of data scanned
            String scanFormat = scanningResult.getFormatName();
            if(scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")){
                DownloadBookTask saveTask = new DownloadBookTask(getContext(), scanContent);
                saveTask.execute((Void) null);
//book search
            }
            else{
                Toast toast = Toast.makeText(getContext(),
                        "Not a valid scan!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            //invalid scan data or scan canceled
            Toast toast = Toast.makeText(getContext(),
                    "No book scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    private class DownloadBookTask extends AsyncTask<Void, Void, Boolean> {

        private final String isbn;
        private final Context mContext;
        private ProgressDialog progress;
        private Handler mHandler;
        GoodreadsBook book;

        public DownloadBookTask(Context frag, String isbn) {
            this.isbn = isbn;
            this.mContext = frag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Opening book...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                GoodreadsAPI api = new GoodreadsAPI();
                ArrayList<SearchResult> apiResults = api.getSearch(mContext, isbn);

                GoodreadsAPI goodreadsApi = new GoodreadsAPI();
                book = goodreadsApi.DownloadBook(mContext, apiResults.get(0).getBookName(), apiResults.get(0).getAuthorName());
                BookBuzzerAPI bbAPI = new BookBuzzerAPI();
                ArrayList<PriceChecker> p = bbAPI.RunPriceChecker(mContext, book.getIsbn());

                if (p != null) {
                    for (PriceChecker price : p) {
                        switch (price.getType()) {
                            case KINDLE_EDITION:
                                book.setKindlePrice(price.getPrice());
                                break;
                            case HARDBACK:
                                book.setHardcoverPrice(price.getPrice());
                                break;
                            case PAPERBACK:
                                book.setPaperbackPrice(price.getPrice());
                                break;
                        }
                    }
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mHandler = new Handler();
            progress.dismiss();

            if (success) {
                Intent i = new Intent(mContext, MainActivity.class);
                MainActivity.data = book;

                i.putExtra("book", book.getTitle());
                mContext.startActivity(i);

            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error opening book, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
