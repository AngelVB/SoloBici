package com.valera.angel.solobiciangelvalera;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Angel on 11/12/14.
 */
public class OnNivelItemClicked implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View view, int pos, long id) {
        MainActivity.nivel=(int)parent.getItemIdAtPosition(pos)+1;
        Toast.makeText(parent.getContext(), "Clicked : " +
                MainActivity.nivel, Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }
}
