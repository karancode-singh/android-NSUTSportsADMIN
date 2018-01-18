package com.nsit.jo.nsitsportsadmin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    private final String DB = "WS2018";

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private DatabaseReference mDatabase;
    protected String date = "";
    protected String time = "00:00";
    protected String batch = "1st Year";
    protected String team1 = "ICE-1";
    protected String team2 = "ICE-2";
    protected String sport = "Football";
    protected String score1 = "-1";
    protected String score2 = "-1";
    protected String tag = "";
    Date dateTime;
    long timeInMilisec;


    EditText timeHH_tv;
    EditText timeMM_tv;
    Spinner spinnerBranch1;
    Spinner spinnerBranch2;
    Spinner spinnerSection1;
    Spinner spinnerSection2;
    EditText score_tv1;
    EditText score_tv2;
    TextView dateSetter;
    TextView tv_tag;
    TextView textSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        timeHH_tv = (EditText) findViewById(R.id.tv_timeHH);
        timeMM_tv = (EditText) findViewById(R.id.tv_timeMM);
        spinnerBranch1 = (Spinner) findViewById(R.id.spinnerBranch1);
        spinnerSection1 = (Spinner) findViewById(R.id.spinnerSection1);
        spinnerBranch2 = (Spinner) findViewById(R.id.spinnerBranch2);
        spinnerSection2 = (Spinner) findViewById(R.id.spinnerSection2);
        score_tv1 = (EditText) findViewById(R.id.tv_score1);
        score_tv2 = (EditText) findViewById(R.id.tv_score2);
        dateSetter = (TextView) findViewById(R.id.button1);
        tv_tag = (EditText) findViewById(R.id.et_tagPush);
        textSport = (TextView) findViewById(R.id.textSportAdd);
        textSport.setText(chooseCriteria.selectedSport);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //showDate(arg1, arg2+1, arg3);
                    date = String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                    dateSetter.setText(date);
                }
            };


    public void goButton(View view) throws ParseException {
        if (date.equals("")) {
            Toast.makeText(MainActivity.this, "No date selected", Toast.LENGTH_SHORT).show();
            return;
        }
        team1 = String.valueOf(spinnerBranch1.getSelectedItem()) + "-" + String.valueOf(spinnerSection1.getSelectedItem());
        team2 = String.valueOf(spinnerBranch2.getSelectedItem()) + "-" + String.valueOf(spinnerSection2.getSelectedItem());
        if (team1.equals(team2)) {
            Toast.makeText(MainActivity.this, "Teams can't be same", Toast.LENGTH_SHORT).show();
            return;
        }
        assignValues();
        Entry newEntry = new Entry(date, time, timeInMilisec, team1, team2, score1, score2, tag);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DB).child(batch).child(sport);
        mDatabase.push().setValue(newEntry, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Toast.makeText(MainActivity.this, "Match entry could not be added. Contact developers if problem persists.", Toast.LENGTH_LONG).show();
                    Log.e("Firebase writing error", firebaseError.getMessage());
                } else {
                    Toast.makeText(MainActivity.this, "Match entry added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        finish();
    }

    private void assignValues() throws ParseException {
        batch = chooseCriteria.selectedYear;
        sport = chooseCriteria.selectedSport;

        if (timeHH_tv.getText().toString().equals("") && timeMM_tv.getText().toString().equals(""))
            time = "00:00";
        else if (timeHH_tv.getText().toString().equals("") && !timeMM_tv.getText().toString().equals(""))
            time = "00:" + timeMM_tv.getText().toString();
        else if (!timeHH_tv.getText().toString().equals("") && timeMM_tv.getText().toString().equals(""))
            time = timeHH_tv.getText().toString() + ":00";
        else
            time = timeHH_tv.getText().toString() + ":" + timeMM_tv.getText().toString();


        if (score_tv1.getText().toString().equals("") && !score_tv2.getText().toString().equals("")) {
            score1 = "0";
            score2 = score_tv2.getText().toString();
        } else if (!score_tv1.getText().toString().equals("") && score_tv2.getText().toString().equals("")) {
            score1 = score_tv1.getText().toString();
            score2 = "0";
        } else if (!score_tv1.getText().toString().equals("") && !score_tv2.getText().toString().equals("")) {
            score1 = score_tv1.getText().toString();
            score2 = score_tv2.getText().toString();
        }

        String dateTime = date + " " + time;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date dT = format.parse(dateTime);
        timeInMilisec = dT.getTime();
        tag = tv_tag.getText().toString();
//        String TAG = "log";
//        Log.d(TAG, time);

    }
}
