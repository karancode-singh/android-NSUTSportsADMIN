package com.nsit.jo.nsitsportsadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chooseCriteria extends AppCompatActivity {

    Spinner spinnerYearCC;
    Spinner spinnerSportCC;

    static protected String selectedYear;
    static protected String selectedSport;
    ArrayAdapter spinnerArrayAdapter;

    ArrayList<String> spinnerArray = new ArrayList<String>();

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_criteria);

        spinnerYearCC = (Spinner) findViewById(R.id.yearSpinCC);
        spinnerSportCC = (Spinner) findViewById(R.id.sportSpinCC);

        dialog=new ProgressDialog(chooseCriteria.this);
        dialog.show();

        spinnerArray=new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);
        spinnerSportCC.setAdapter(spinnerArrayAdapter);

        DatabaseReference db= FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariables.sportListDB);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        spinnerArray.add(ds.getValue().toString());
                    }
                    spinnerArrayAdapter.notifyDataSetChanged();
                    dialog.dismiss();

                }else{
                    dialog.dismiss();
                    Toast.makeText(chooseCriteria.this,"Something wrong",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showList(View view) {
        selectedYear=String.valueOf(spinnerYearCC.getSelectedItem());
        selectedSport="";
        try{
            selectedSport=String.valueOf(spinnerSportCC.getSelectedItem());
        }catch (Exception e){
            Toast.makeText(chooseCriteria.this,"Something wrong",Toast.LENGTH_LONG).show();
        }
        if(!selectedSport.equals("")){
            Intent i=new Intent(chooseCriteria.this,sportList.class);
            startActivity(i);
        }
    }

    public void changeSportList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(chooseCriteria.this);
        builder.setMessage("Enter the SUPER ADMIN PASSWORD to change the sportlist.")
                .setTitle("Change sportlist");

// Set up the input
        final EditText input = new EditText(chooseCriteria.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
// Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if(m_Text.equals("acdc")){
                    //change to add sport activity
                    startActivity(new Intent(chooseCriteria.this,addSport.class));
                    finish();
                }else{
                    Toast.makeText(chooseCriteria.this,"Wrong password.",Toast.LENGTH_LONG).show();
                }
            }
        });


        builder.show();


    }

    @Override
    public void onBackPressed() {

    }
}
