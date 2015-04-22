package net.caughtinthefish.brewcentral;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class CommercialBeerDetailFragment extends Fragment {

    private JSONObject mBeer;
    private DrawerFragmentListener mListener;

    public CommercialBeerDetailFragment(JSONObject beer) {
        mBeer = beer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_commercial_beer_detail,
                                          container, false);
        
        TextView detailTextView = (TextView)rootView.findViewById(R.id.detailTextView);
        
        detailTextView.setText(parseBeer());
        
        return rootView;
    }
    
    private Spanned parseBeer() {
        StringBuilder builder = new StringBuilder();

        try {
            builder.append("<b>Description:</b> ");
            builder.append(mBeer.getString("description"));
            builder.append("<br><br>\n");
            
            builder.append("<b>Style:</b> ");
            builder.append(mBeer.getJSONObject("style").getString("name"));
            builder.append("<br>\n");
            
            builder.append("<b>Style Description:</b> ");
            builder.append(mBeer.getJSONObject("style").getString("description"));
            builder.append("<br>\n");
            
        } catch (JSONException e) {
            e.printStackTrace();
            return new SpannableString(e.getMessage());
        }
        
        return Html.fromHtml(builder.toString());
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener.onFragmentNameSet(mBeer.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
