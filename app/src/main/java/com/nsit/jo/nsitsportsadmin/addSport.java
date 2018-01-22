package com.nsit.jo.nsitsportsadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addSport extends AppCompatActivity {

    ArrayList<String> sports;
    SportListAdapter sportListAdapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sport);

        sports=new ArrayList<>();
        sportListAdapter = new SportListAdapter(addSport.this,sports);
        lv=(ListView)findViewById(R.id.addSport);
        lv.setAdapter(sportListAdapter);

        DatabaseReference db= FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariables.sportListDB);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    sports.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        sports.add(ds.getValue().toString());
                    }
                    sportListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addSport(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addSport.this);
        builder.setMessage("Enter the name of the sport to be added.")
                .setTitle("Add sport");

// Set up the input
        final EditText input = new EditText(addSport.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
// Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

                FirebaseDatabase.getInstance().getReference()
                        .child(GlobalVariables.sportListDB)
                        .child(m_Text).setValue(m_Text);
                dialog.dismiss();

            }
        });


        builder.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(addSport.this,chooseCriteria.class));
    }
}
