package net.caughtinthefish.brewcentral;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 */
public class CalculatorsFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private String mName = null;
    private DrawerFragmentListener mListener;

    /**
     * Create a new instance of this fragment using the specified title.
     * 
     * @param name
     *            Title of the Fragment, seen in the action bar.
     * @return A new instance of fragment CommercialSearchFragment.
     */
    public static CalculatorsFragment newInstance(String name) {
        CalculatorsFragment fragment = new CalculatorsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public CalculatorsFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_calculators,
                container, false);

        Button mashCalcButton = (Button) rootView
                .findViewById(R.id.mash_calculator_button);
        mashCalcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mashCalculator();
            }
        });

        Button percentAlcButton = (Button) rootView
                .findViewById(R.id.percent_alcohol_button);
        percentAlcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                percentAlcCalculator();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DrawerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DrawerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void mashCalculator() {
        Fragment fragment = new MashCalcFragment();
        FragmentManager fragmentMgr = getActivity().getFragmentManager();
        fragmentMgr.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack(fragment.getTag()).commit();
    }

    private void percentAlcCalculator() {

    }
}
