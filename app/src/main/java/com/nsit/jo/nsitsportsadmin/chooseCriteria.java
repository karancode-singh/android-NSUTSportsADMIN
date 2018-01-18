package com.nsit.jo.nsitsportsadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

public class chooseCriteria extends AppCompatActivity {

    Spinner spinnerYearCC;
    Spinner spinnerSportCC;

    static protected String selectedYear;
    static protected String selectedSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_criteria);

        spinnerYearCC = (Spinner) findViewById(R.id.yearSpinCC);
        spinnerSportCC = (Spinner) findViewById(R.id.sportSpinCC);

    }

    public void showList(View view) {
        selectedYear=String.valueOf(spinnerYearCC.getSelectedItem());
        selectedSport=String.valueOf(spinnerSportCC.getSelectedItem());
        Intent i=new Intent(chooseCriteria.this,sportList.class);
        startActivity(i);
    }
}
