package com.nsit.jo.nsitsportsadmin;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class sportList extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private final String DB = "WS2018";
    private ArrayList<Entry> entriesPending;
    private ArrayList<Entry> entriesCompleted;
    private ArrayList<String> pKeys;
    private ArrayList<String> cKeys;
    private DatabaseReference mDatabase;
    private ListView listView;
    private ListView listView2;
    private TextView textSport;
    static boolean calledAlready = false;
    private ProgressBar progressBar;
    private AdapterView.OnItemClickListener pItemClickListener;
    private AdapterView.OnItemClickListener cItemClickListener;
    Snackbar snackbar;
    private NetworkStateReceiver networkStateReceiver;

    @Override
    public void onNetworkAvailable() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
            Log.d("snackbar", "Hiding");
        }
    }

    @Override
    public void onNetworkUnavailable() {
        if (!snackbar.isShown()) {
            snackbar.show();
            Log.d("snackbar", "Showing");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_list);

        snackbar = Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinatorLayout), "Unable to connect to the Internet", Snackbar.LENGTH_INDEFINITE);
        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        progressBar = (ProgressBar) LayoutInflater.from(this).inflate(R.layout.progress_bar, null);

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DB).child(chooseCriteria.selectedYear).child(chooseCriteria.selectedSport);

        entriesPending = new ArrayList<>();
        entriesCompleted = new ArrayList<>();
        pKeys = new ArrayList<>();
        cKeys = new ArrayList<>();

        Query mySortingQuery = mDatabase.orderByChild("timeInMiliSec");

        listView = (ListView) findViewById(R.id.lv);
        listView2 = (ListView) findViewById(R.id.lv2);

        textSport = (TextView) findViewById(R.id.textSport);
        textSport.setText(chooseCriteria.selectedSport);

        final Padapter myAdapter = new Padapter(this, entriesPending);
        final Cadapter myAdapter2 = new Cadapter(this, entriesCompleted);

        listView.setAdapter(myAdapter);
        listView2.setAdapter(myAdapter2);
        listView2.addFooterView(progressBar);

        mySortingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView2.removeFooterView(progressBar);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mySortingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                entriesPending.clear();
                entriesCompleted.clear();
                myAdapter.notifyDataSetChanged();
                myAdapter2.notifyDataSetChanged();
                ListUtils.setDynamicHeight(listView);
                ListUtils.setDynamicHeight(listView2);
                pKeys.clear();
                cKeys.clear();

                Iterable<DataSnapshot> entries = dataSnapshot.getChildren();
                for (DataSnapshot entry : entries) {
                    Entry value = entry.getValue(Entry.class);
                    String key = entry.getKey();
                    if (value.score1.equals("-1") || value.score2.equals("-1")) {
                        pKeys.add(key);
                        entriesPending.add(value);
                        ListUtils.setDynamicHeight(listView);
                    } else {
                        cKeys.add(key);
                        entriesCompleted.add(value);
                        ListUtils.setDynamicHeight(listView2);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(sportList.this, ChangeOrRemove.class);
                intent.putExtra("team1", entriesPending.get(position).team1);
                intent.putExtra("team2", entriesPending.get(position).team2);
                intent.putExtra("score1", entriesPending.get(position).score1);
                intent.putExtra("score2", entriesPending.get(position).score2);
                intent.putExtra("date", entriesPending.get(position).date);
                intent.putExtra("time", entriesPending.get(position).time);
                intent.putExtra("tag", entriesPending.get(position).tag);
                intent.putExtra("key", pKeys.get(position));
                startActivity(intent);
            }
        };

        cItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(sportList.this, ChangeOrRemove.class);
                intent.putExtra("team1", entriesCompleted.get(position).team1);
                intent.putExtra("team2", entriesCompleted.get(position).team2);
                intent.putExtra("score1", entriesCompleted.get(position).score1);
                intent.putExtra("score2", entriesCompleted.get(position).score2);
                intent.putExtra("date", entriesCompleted.get(position).date);
                intent.putExtra("time", entriesCompleted.get(position).time);
                intent.putExtra("tag", entriesCompleted.get(position).tag);
                intent.putExtra("key", cKeys.get(position));
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(pItemClickListener);
        listView2.setOnItemClickListener(cItemClickListener);
//        mySortingQuery.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Entry value = dataSnapshot.getValue(Entry.class);
//                String key = dataSnapshot.getKey();
//                if (value.team1.equals(branchSection) || value.team1.equals(branchSection)) {
//                    if (value.score1.equals("-1")) {
//                        entriesPending.add(value);
//                        keysPending.add(key);
//                        myAdapter.notifyDataSetChanged();
//                        ListUtils.setDynamicHeight(listView);
//                    } else {
//                        entriesCompleted.add(value);
//                        keysCompleted.add(key);
//                        myAdapter2.notifyDataSetChanged();
//                        ListUtils.setDynamicHeight(listView2);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Entry value = dataSnapshot.getValue(Entry.class);
//                String key = dataSnapshot.getKey();
//
//                if (keysPending.contains(key)) {
//                    int index = keysPending.indexOf(key);
//                    if (value.score1.equals("-1")) {
//                        entriesPending.set(index, value);
//                        myAdapter.notifyDataSetChanged();
//                        ListUtils.setDynamicHeight(listView);
//                    } else {
//                        entriesPending.remove(index);
//                        entriesCompleted.add(value);
//                        Collections.sort(entriesCompleted, new Comparator<Entry>() {
//                            @Override
//                            public int compare(Entry entry1, Entry entry2) {
//                                return entry1.timeInMiliSec.compareTo(entry2.timeInMiliSec);
//                            }
//                        });
//                        myAdapter.notifyDataSetChanged();
//                        myAdapter2.notifyDataSetChanged();
//                        ListUtils.setDynamicHeight(listView);
//                        ListUtils.setDynamicHeight(listView2);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Entry value = dataSnapshot.getValue(Entry.class);
//                String key = dataSnapshot.getKey();
//
//                if (keysPending.contains(key)) {
//                    int index = keysPending.indexOf(key);
//                    entriesPending.remove(index);
//                    myAdapter.notifyDataSetChanged();
//                    ListUtils.setDynamicHeight(listView);
//                }
//                if (keysCompleted.contains(key)) {
//                    int index = keysCompleted.indexOf(key);
//                    entriesCompleted.remove(index);
//                    myAdapter2.notifyDataSetChanged();
//                    ListUtils.setDynamicHeight(listView);
//                }
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void addEntry(View view) {
        startActivity(new Intent(sportList.this, MainActivity.class));
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
