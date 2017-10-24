package com.example.stevennguyen.countdownmobile;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    // initialize databases
    DataBaseHelper myDb;

    // initialize arraylist, listview, adapter to connect to listview
    ArrayList<String> countdownList;
    ArrayAdapter<String> adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create new database
        myDb = new DataBaseHelper(this);

        // connect lv to myList
        lv = (ListView) findViewById(R.id.myList);

        // create new arraylist and adapter
        countdownList = new ArrayList<>();
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, countdownList);

        populateListView();

        if(!countdownList.isEmpty()) {
            // check if last updated was over a day ago
            myDb.checkForUpdate();
            populateListView();
        }

        // Long click lister for DELETING from list
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String countdownPos = countdownList.get(position);
                String[] splitByColon = countdownPos.split(":");
                String dbPosition = splitByColon[splitByColon.length-1];
                countdownList.remove(position);
                myDb.deleteData(dbPosition);
                adapter.notifyDataSetChanged();
                return false;
            }

        });

        lv.setAdapter(adapter);

    }

    // When user taps add button
    public void addCountdown(View view) {
        Intent intent = new Intent(this, addCountdownActivity.class);
        startActivityForResult(intent, 1);
    }

    // repopulate list upon adding new countdown
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                populateListView();
            }
        }
    }

    // populate the list view with database data
    private void populateListView() {
        Cursor cursor = myDb.getAllData();
        countdownList.clear();
        while(cursor.moveToNext()) {
            countdownList.add(cursor.getInt(2) + " days - " + cursor.getString(1)
                    + "\nid:" + cursor.getString(0) + "");
        }
        adapter.notifyDataSetChanged();
    }

}