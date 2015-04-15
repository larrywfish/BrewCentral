package net.caughtinthefish.brewcentral;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 */
public class MashCalcFragment extends Fragment implements TextWatcher {
    TextView waterGrainRatioView = null;
    TextView gristTemperatureView = null;
    TextView restTemperatureView = null;
    TextView grainWeightView = null;

    public MashCalcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mash_calc,
                container, false);

        SharedPreferences preferences = this.getActivity().getPreferences(
                Context.MODE_PRIVATE);

        Float tmpFloat = new Float(0.0);

        waterGrainRatioView = (TextView) rootView
                .findViewById(R.id.waterGrainRatioField);
        tmpFloat = preferences.getFloat("mashCalculator.waterGrainRatio",
                (float) 1.25);
        waterGrainRatioView.setText(tmpFloat.toString());
        waterGrainRatioView.addTextChangedListener(this);

        gristTemperatureView = (TextView) rootView
                .findViewById(R.id.gristTemperatureField);
        tmpFloat = preferences.getFloat("mashCalculator.gristTemp",
                (float) 68.0);
        gristTemperatureView.setText(tmpFloat.toString());
        gristTemperatureView.addTextChangedListener(this);

        restTemperatureView = (TextView) rootView
                .findViewById(R.id.restTemperatureField);
        tmpFloat = preferences.getFloat("mashCalculator.restTemp",
                (float) 152.0);
        restTemperatureView.setText(tmpFloat.toString());
        restTemperatureView.addTextChangedListener(this);

        grainWeightView = (TextView) rootView
                .findViewById(R.id.grainWeightField);
        tmpFloat = preferences.getFloat("mashCalculator.grainWeight",
                (float) 10.0);
        grainWeightView.setText(tmpFloat.toString());
        grainWeightView.addTextChangedListener(this);

        ((EditText) rootView.findViewById(R.id.strikeTempResult))
                .setEnabled(false);
        ((EditText) rootView.findViewById(R.id.strikeWaterResult))
                .setEnabled(false);

        computeResult(rootView);

        return rootView;
    }

    private void computeResult(View view) {

        View useView;
        if (view == null) {
            useView = getView();
        } else {
            useView = view;
        }
        EditText waterTempField = (EditText) useView
                .findViewById(R.id.strikeTempResult);
        EditText waterVolumeField = (EditText) useView
                .findViewById(R.id.strikeWaterResult);

        try {
            EditText field;
            field = (EditText) useView.findViewById(R.id.waterGrainRatioField);
            // float r = (new Float(field.getText().toString())).floatValue();
            float r = Float.valueOf(field.getText().toString());

            field = (EditText) useView.findViewById(R.id.gristTemperatureField);
            // float T1 = (new Float(field.getText().toString())).floatValue();
            float T1 = Float.valueOf(field.getText().toString());

            field = (EditText) useView.findViewById(R.id.restTemperatureField);
            // float T2 = (new Float(field.getText().toString())).floatValue();
            float T2 = Float.valueOf(field.getText().toString());

            field = (EditText) useView.findViewById(R.id.grainWeightField);
            // float w = (new Float(field.getText().toString())).floatValue();
            float w = Float.valueOf(field.getText().toString());

            DecimalFormat df = new DecimalFormat("#.00");

            if (r > 0.0 && T1 > 0.0 && T2 > 0.0) {
                Float Tw = new Float(((.2 / r) * (T2 - T1)) + T2);
                waterTempField.setText(df.format(Tw.doubleValue()));

                Float Wm = new Float(r * w);
                waterVolumeField.setText(df.format(Wm.doubleValue()));
            } else {
                waterTempField.setText("");
                waterVolumeField.setText("");
            }
        } catch (Exception ex) {
            waterTempField.setText("");
            waterVolumeField.setText("");
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences preferences = getActivity().getPreferences(
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        EditText field;

        field = (EditText) getView().findViewById(R.id.waterGrainRatioField);
        editor.putFloat("mashCalculator.waterGrainRatio",
                Float.parseFloat(field.getText().toString()));
        field = (EditText) getView().findViewById(R.id.gristTemperatureField);
        editor.putFloat("mashCalculator.gristTemp",
                Float.parseFloat(field.getText().toString()));
        field = (EditText) getView().findViewById(R.id.restTemperatureField);
        editor.putFloat("mashCalculator.restTemp",
                Float.parseFloat(field.getText().toString()));
        field = (EditText) getView().findViewById(R.id.grainWeightField);
        editor.putFloat("mashCalculator.grainWeight",
                Float.parseFloat(field.getText().toString()));

        editor.commit();
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        computeResult(null);
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
            int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
