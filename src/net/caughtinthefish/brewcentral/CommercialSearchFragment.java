package net.caughtinthefish.brewcentral;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Fragment for the Commercial Beer Search tool.
 */
public class CommercialSearchFragment extends Fragment implements
        OnClickListener, OnEditorActionListener {

    private static final String ARG_NAME = "name";
    private EditText searchText;
    private ListView resultsView;
    private String mResults = null;
    private String mName = null;
    private DrawerFragmentListener mListener;
    private ResultsAdapter mResultsAdapter;

    /**
     * Create a new instance of this fragment using the specified title.
     * 
     * @param name Title of the Fragment, seen in the action bar.
     * @return A new instance of fragment CommercialSearchFragment.
     */
    public static CommercialSearchFragment newInstance(String name) {
        CommercialSearchFragment fragment = new CommercialSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public CommercialSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mName = new String(getArguments().getString(ARG_NAME));
            mListener.onFragmentNameSet(mName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_commercial_search,
                container, false);

        Button searchButton = (Button) rootView
                .findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        resultsView = (ListView) rootView.findViewById(R.id.results_list);
        searchText = (EditText) rootView.findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(this);

        mResultsAdapter = new ResultsAdapter();
        resultsView.setAdapter(mResultsAdapter);

        if (savedInstanceState != null) {
            mResults = savedInstanceState.getString("searchResults");
            if (mResults != null && !mResults.isEmpty()) {
                displayResults();
            } 
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DrawerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent arg2) {
        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        performSearch();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResults != null) {
            outState.putString("searchResults", mResults);
        }
    }

    public void displayResults() {
        JSONObject parser;
        try {
            String toastStr = new String();

            parser = new JSONObject(mResults);
            JSONArray beerList = parser.getJSONArray("data");
            /*
            int count = parser.getInt("totalResults");
            toastStr += "Total Beers Found = ";
            toastStr += count;

            toastStr += "\nBeers in first page = ";
            toastStr += beerList.length();
            toastStr += " (";
            toastStr += mResults.length() / 1024;
            toastStr += "K bytes)";

            Toast.makeText(getActivity(), toastStr, Toast.LENGTH_LONG).show();
            */

            mResultsAdapter.clear();
            for (int ii = 0; ii < beerList.length(); ii++) {
                JSONObject obj = beerList.getJSONObject(ii);
                if (ii != 0)
                    toastStr += "\n";

                mResultsAdapter.add(obj.getString("name"),
                        obj.getJSONArray("breweries").getJSONObject(0)
                                .getString("name"));

            }

            // resultsView.invalidate();
            mResultsAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    private void performSearch() {
        if (searchText.getText().length() == 0) {
            return;
        }
        getActivity().setProgressBarIndeterminateVisibility(true);
        AsyncTask<Integer, String, Integer> task = new AsyncTask<Integer, String, Integer>() {

            @Override
            protected Integer doInBackground(Integer... arg0) {
                String url = new String(
                        "http://api.brewerydb.com/v2/search?key=0847b4c603224e9f5a063961ce54a664&type=beer");
                try {
                    url += "&q="
                            + URLEncoder.encode(
                                    searchText.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    return null;
                }
                url += "&withBreweries=Y";
                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    mResults = Client.execute(httpget, responseHandler);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Integer unused) {
                if (mResults != null && !mResults.isEmpty()) {
                    displayResults();
                }
            }

        };
        task.execute();
    }

    private class ResultsAdapter extends BaseAdapter {

        private List<String> mBeers = new ArrayList<String>();
        private List<String> mBreweries = new ArrayList<String>();

        public void clear() {
            mBeers.clear();
            mBreweries.clear();
        }

        public void add(String beer, String description) {
            mBeers.add(beer);
            mBreweries.add(description);
        }

        @Override
        public int getCount() {
            return mBeers.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mBeers.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getLayoutInflater();
                view = inflater
                        .inflate(R.layout.commercial_search_result, null);
            } else {

            }

            TextView tv = (TextView) view.findViewById(R.id.tvTitle);
            tv.setText(mBeers.get(position));

            TextView tv2 = (TextView) view.findViewById(R.id.tvDesc);
            tv2.setText(mBreweries.get(position));

            return view;

        }

    }

}
