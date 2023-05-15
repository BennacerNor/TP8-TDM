package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Favoris extends AppCompatActivity {
    private ListView listView;
    private Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        listView = findViewById(R.id.listView);

        displayData();

    }

    private void displayData() {
        // Get a reference to the database
        SQLiteDatabase db = new MyDatabaseHelper(this).getReadableDatabase();

        // Define the columns to retrieve
        String[] projection = {
                "name",
                "path",
                "duration"
        };

        // Query the database
        Cursor cursor = db.query(
                "mytable",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Create an ArrayList to hold the data
        ArrayList<String> data = new ArrayList<>();

        // Loop through the cursor and add each row to the ArrayList
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            data.add(name + " - " + path + " - " + duration);
        }

        // Close the cursor and the database
        cursor.close();
        db.close();

        // Create an ArrayAdapter to display the data in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Favoris.this,
                R.layout.item,
                data
        );

        // Set the adapter for the ListView
        listView.setAdapter(adapter);
    }

}