package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {
    private static final String TAG = "MainActivity3";
    private EditText urlEditText;
    private Button downloadButton;
    private ArrayList<String> mp3List = new ArrayList<>();

//    private ListView listView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.téléchager:

                Intent intent = new Intent(this, MainActivity3.class);
                startActivity(intent);
                return true;

            case R.id.favoris:

                Toast.makeText(MainActivity3.this,"favoris",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(this, Favoris.class);
                startActivity(intent1);

                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

//        listView = findViewById(R.id.listView);

        // Appeler la méthode de lecture de données pour afficher les informations stockées dans la base de données


        urlEditText = findViewById(R.id.ed1);
        downloadButton = findViewById(R.id.b1);
        File dir = new File(Environment.getExternalStorageDirectory() + "/mydir");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Failed to create directory");
            }
        }

        verifyStoragePermissions(this);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(urlEditText.getText().toString());
            }
        });


    }


    private void downloadFile(String url) {
        // Créer une demande de téléchargement à partir d'une URL
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Téléchargement de fichier MP3");
        request.setDescription("Téléchargement en cours...");

        // Obtenir l'emplacement de stockage du fichier téléchargé
        String filePath = Environment.getExternalStorageDirectory().toString() + "/mydir/myfile.mp3";
        request.setDestinationUri(Uri.fromFile(new File(filePath)));


        // Obtenir le gestionnaire de téléchargement
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Ajouter la demande de téléchargement à la file d'attente
        long downloadId = manager.enqueue(request);

        // Attendre que le téléchargement soit terminé
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = manager.query(query);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                // Le téléchargement est terminé avec succès
                // Ajouter les informations sur le fichier MP3 à la base de données
                SQLiteDatabase db = new MyDatabaseHelper(MainActivity3.this).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", "myfile.mp3");
                values.put("path", filePath);
                values.put("duration", 120);
                db.insert("mytable", null, values);
                db.close();
                Toast.makeText(MainActivity3.this, "Fichier téléchargé et ajouté à la base de données", Toast.LENGTH_SHORT).show();
//            displayData();
        }
        cursor.close();
    }

//    private void displayData() {
//        // Get a reference to the database
//        SQLiteDatabase db = new MyDatabaseHelper(MainActivity3.this).getReadableDatabase();
//
//        // Define the columns to retrieve
//        String[] projection = {
//                "name",
//                "path",
//                "duration"
//        };
//
//        // Query the database
//        Cursor cursor = db.query(
//                "mytable",
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        // Create an ArrayList to hold the data
//        ArrayList<String> data = new ArrayList<>();
//
//        // Loop through the cursor and add each row to the ArrayList
//        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//            String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
//            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
//            data.add(name + " - " + path + " - " + duration);
//        }
//
//        // Close the cursor and the database
//        cursor.close();
//        db.close();
//
//        // Create an ArrayAdapter to display the data in the ListView
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                MainActivity3.this,
//                R.layout.item,
//                data
//        );
//
//        // Set the adapter for the ListView
//        listView.setAdapter(adapter);
//    }



}