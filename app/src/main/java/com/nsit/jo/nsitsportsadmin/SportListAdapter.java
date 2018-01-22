package com.nsit.jo.nsitsportsadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by aditya on 22/1/18.
 */

public class SportListAdapter extends ArrayAdapter<String> {

public SportListAdapter(Context context, List<String> data) {
        super(context, R.layout.sport_entry_element, data);
        }

@NonNull
@Override
public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater lf = LayoutInflater.from(getContext());
    View customView = lf.inflate(R.layout.sport_entry_element, parent, false);


    TextView tv_name=(TextView)customView.findViewById(R.id.addSportEntry);
    tv_name.setText(getItem(position));


    ImageButton crossButton = (ImageButton)customView.findViewById(R.id.closeButton);
    crossButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to remove "+getItem(position)+"?")
                    .setTitle("Remove sport");

// Set up the input
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

// Set up the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("WS2018")
                            .child("1st Year")
                            .child(getItem(position)).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("WS2018")
                            .child("2nd Year")
                            .child(getItem(position)).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("WS2018")
                            .child("3rd Year")
                            .child(getItem(position)).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("WS2018")
                            .child("4th Year")
                            .child(getItem(position)).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("sportlist")
                            .child(getItem(position)).removeValue();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            builder.show();


        }
    });



    return customView;
    }
}
