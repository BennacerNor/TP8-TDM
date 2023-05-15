package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MonActivity2  extends AppCompatActivity {

ImageView im1;
ImageView im2;
ImageView im3;
ImageView im4;
ImageView im5;
    private MediaPlayer mediaPlayer;
    private List<String> songList;
    private int songIndex = 0;
    private int songIndexx;


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

                Toast.makeText(MonActivity2.this,"favoris",Toast.LENGTH_LONG).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int songIndexx = getIntent().getIntExtra("songIndexx", -1);
//
//
//            // do something with the song index
//
//            Toast.makeText(MonActivity2.this, "songIndexx", Toast.LENGTH_SHORT).show();
//


        setContentView(R.layout.activity_mon2);

        im1 = findViewById(R.id.image1);
        im2 = findViewById(R.id.image2);
        im3 = findViewById(R.id.image3);
        im4 = findViewById(R.id.image4);
        im5 = findViewById(R.id.image5);

        im4.setVisibility(View.INVISIBLE);
        im5.setVisibility(View.INVISIBLE);

        // Get the list of songs from the media store
        songList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(0);
                songList.add(filePath);
            }
            cursor.close();
        }

        // Create a new media player and start playing the first song
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Move to the next song when the current song completes
                songIndex++;
                if (songIndex >= songList.size()) {
                    songIndex = 0;
                }
                String songPath = songList.get(songIndex);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songPath));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Log.e("MonActivity2", "Failed to play song", e);
                }
            }
        });
        String firstSongPath = songList.get(songIndex);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(firstSongPath));
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.e("MonActivity2", "Failed to play song", e);
        }

        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(),MyService.class));
                im3.setVisibility(View.VISIBLE);
                im2.setVisibility(View.INVISIBLE);
                im4.setVisibility(View.VISIBLE);
                im5.setVisibility(View.VISIBLE);



            }
        });


        im5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                songIndex++;
                if (songIndex >= songList.size()) {

                    songIndex = 0;
                }
                String songPath = songList.get(songIndex);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songPath));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Log.e("MonActivity2", "Failed to play song", e);
                }
            }
           }
            );
        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(),MyService.class));
                im2.setVisibility(View.VISIBLE);
                im3.setVisibility(View.INVISIBLE);
                im4.setVisibility(View.INVISIBLE);
                im5.setVisibility(View.INVISIBLE);
            }
        });

        im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songIndex++;
                if (songIndex >= songList.size()) {
                    songIndex = 0;
                }
                String songPath = songList.get(songIndex);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songPath));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Log.e("MonActivity2", "Failed to play song", e);
                }
            }
        });

    }

}

