package com.nsit.jo.nsitsportsadmin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangeOrRemove extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private final String DB = GlobalVariables.DB;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    EditText etTimeHH;
    EditText etTimeMM;
    TextView tvTeam1;
    TextView tvTeam2;
    EditText etScore1;
    EditText etScore2;
    TextView buttonDate;
    Button buttonChange;
    Button buttonRemove;
    TextView tv_tag;
    TextView textSport;

    private String team1;
    private String team2;
    private String score1;
    private String score2;
    private String date;
    private String time;
    private String key;
    private DatabaseReference mDatabase;
    private long timeInMilisec;
    private String tag;
    private TextView time_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_or_remove);

        time_tv = (TextView) findViewById(R.id.timePicker);
        time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFrag();
                timePicker.show(getSupportFragmentManager(),"sfa");
            }
        });

        team1 = getIntent().getStringExtra("team1");
        team2 = getIntent().getStringExtra("team2");
        score1 = getIntent().getStringExtra("score1");
        score2 = getIntent().getStringExtra("score2");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        key = getIntent().getStringExtra("key");
        tag = getIntent().getStringExtra("tag");
        String[] timeArr = time.split(":");

        tvTeam1 = (TextView) findViewById(R.id.tvTeam1);
        tvTeam2 = (TextView) findViewById(R.id.tvTeam2);
        etScore1 = (EditText) findViewById(R.id.etScore1);
        etScore2 = (EditText) findViewById(R.id.etScore2);
        buttonDate = (TextView) findViewById(R.id.buttonDate);
        buttonChange = (Button) findViewById(R.id.buttonChange);
        buttonRemove = (Button) findViewById(R.id.buttonRemove);
        tv_tag = (EditText) findViewById(R.id.et_tagChange);

        textSport = (TextView) findViewById(R.id.textSportChange);
        textSport.setText(chooseCriteria.selectedSport);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        time_tv.setText(timeArr[0]+":"+timeArr[1]);
        tvTeam1.setText(team1);
        tvTeam2.setText(team2);
        if(!(score1.equals("-1")||score2.equals("-1"))) {
            etScore1.setText(score1);
            etScore2.setText(score2);
        }
        buttonDate.setText(date);
        tv_tag.setText(tag);


        mDatabase = FirebaseDatabase.getInstance().getReference().child(DB).child(chooseCriteria.selectedYear).child(chooseCriteria.selectedSport).child(key);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Boolean shouldUpdate = false;
                try {
                    shouldUpdate = assignValues();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(shouldUpdate) {
                    Entry entry = new Entry(date, time, timeInMilisec, team1, team2, score1, score2, tag);
                    mDatabase.setValue(entry, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(ChangeOrRemove.this, "Match entry could not be updated. Contact developers if problem persists.", Toast.LENGTH_LONG).show();
                                Log.e("Firebase updating error", firebaseError.getMessage());
                            } else {
                                Toast.makeText(ChangeOrRemove.this, "Match entry updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finish();
                }
                else
                    Toast.makeText(ChangeOrRemove.this, "Some entry is not correct. Check and confirm all entries are correct.", Toast.LENGTH_LONG).show();
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDatabase.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(ChangeOrRemove.this, "Match entry could not be removed. Contact developers if problem persists.", Toast.LENGTH_LONG).show();
                            Log.e("Firebase updating error", firebaseError.getMessage());
                        } else {
                            Toast.makeText(ChangeOrRemove.this, "Match entry removed successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                finish();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        String hh = ""+hour;
        String mm = ""+minute;
        if(hour<10)
            hh = "0"+hh;
        if(minute<10)
            mm = "0"+mm;
        time = hh+":"+mm;
        time_tv.setText(time);
    }

    @SuppressWarnings("deprecation")
    public void setDateAgain(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
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
                    buttonDate.setText(date);
                }
            };

    private Boolean assignValues() throws ParseException {
//        if (
//                !etTimeHH.getText().toString().matches("\\d+")||
//                !etTimeMM.getText().toString().matches("\\d+")||
//                !etScore1.getText().toString().matches("-?\\d+")||
//                !etScore2.getText().toString().matches("-?\\d+")
//        )
//            return false;

//        if (etTimeHH.getText().toString().equals("") && etTimeMM.getText().toString().equals(""))
//            time = "00:00";
//        else if (etTimeHH.getText().toString().equals("") && !etTimeMM.getText().toString().equals(""))
//            time = "00:" + etTimeMM.getText().toString();
//        else if (!etTimeHH.getText().toString().equals("") && etTimeMM.getText().toString().equals(""))
//            time = etTimeHH.getText().toString() + ":00";
//        else
//            time = etTimeHH.getText().toString() + ":" + etTimeMM.getText().toString();

        if (etScore1.getText().toString().equals("") && etScore2.getText().toString().equals("")) {
            score1 = "-1";
            score2 = "-1";
        }else if (etScore1.getText().toString().equals("") && !etScore2.getText().toString().equals("")) {
            score1 = "0";
            score2 = etScore2.getText().toString();
        } else if (!etScore1.getText().toString().equals("") && etScore2.getText().toString().equals("")) {
            score1 = etScore1.getText().toString();
            score2 = "0";
        } else if (!etScore1.getText().toString().equals("") && !etScore2.getText().toString().equals("")) {
            score1 = etScore1.getText().toString();
            score2 = etScore2.getText().toString();
        }

        String dateTime = date + " " + time;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date dT = format.parse(dateTime);
        timeInMilisec = dT.getTime();
        tag = tv_tag.getText().toString();
        return true;
    }
}
