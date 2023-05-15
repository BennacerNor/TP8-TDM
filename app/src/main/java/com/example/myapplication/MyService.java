package com.example.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.service.quicksettings.Tile;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.BreakIterator;
import java.util.ArrayList;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import com.example.myapplication.MonActivity2;

public class MyService extends Service {
    //private static final String TAG = "MyService";

    private ArrayList<String> songTitles;
    private MediaPlayer mediaPlayer;
    private MyReceiver recv;
    private Context mContext;
    private int currentSongIndex = 0;
    private MediaSessionCompat mediaSession;
    String Title;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public MyService( ) {

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate() {

        recv = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter("PlayPause");
        registerReceiver(recv, intentFilter);
        super.onCreate();

    //    Intent intent = new Intent(this, MonActivity2.class);
//        intent.putExtra("songIndexx", currentSongIndex);
//        startActivity(intent);

        NextReceiver nextReceiver = new NextReceiver();
        registerReceiver(nextReceiver, new IntentFilter("Next"));

        PrevReceiver prevReceiver = new PrevReceiver();
        registerReceiver(prevReceiver, new IntentFilter("Prev"));

        mContext = getApplicationContext();
        mediaPlayer = new MediaPlayer();
        songTitles = new ArrayList<>();
        verifyStoragePermissions(this);


        // Start a handler thread to perform asynchronous operations
        HandlerThread handlerThread = new HandlerThread("MusicThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        // Access music files asynchronously
        handler.post(new Runnable() {
            @Override
            public void run() {

               // Toast.makeText(MyService.this, "Hello, world!3333", Toast.LENGTH_SHORT).show();

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Audio.AudioColumns.DATA,MediaStore.Audio.AudioColumns.TITLE };
                Cursor cursor = mContext.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Download%"}, null);

                ContentResolver contentResolver = getContentResolver();

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                       // Toast.makeText(MyService.this, "Hello, world2!", Toast.LENGTH_SHORT).show();
                       // String songTitle = songTitles.get(currentSongIndex);

                        songTitles.add(title);
                        //Title = title;
                        //Toast.makeText(MyService.this, title, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MyService.this, currentSongIndex, Toast.LENGTH_SHORT).show();

                    }
                    cursor.close();
                }

                // Start playing the first song

                if (!songTitles.isEmpty()) {
                   // Toast.makeText(MyService.this, currentSongIndex, Toast.LENGTH_SHORT).show();
                    playSong(0);
                }
            }
        });
    }

    private void verifyStoragePermissions(Context context) {
        // Check if we have read permission
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        // We don't have permission so prompt the user
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {

        mediaSession = new MediaSessionCompat(this, "MyService");
        //ce qu’on va faire si user clique sur la notif
        Intent notificationIntent = new Intent(this, MonActivity2.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        FLAG_UPDATE_CURRENT);
        // ce qu’on va faire si user clique sur le bouton de la notif
        PendingIntent pPPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("PlayPause"),
                        FLAG_UPDATE_CURRENT);
        PendingIntent pPrevPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("Prev"),
                        FLAG_UPDATE_CURRENT);
        PendingIntent pNextPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("Next"),
                        FLAG_UPDATE_CURRENT);

  mediaPlayer.start();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (songTitles != null && !songTitles.isEmpty() && currentSongIndex >= 0 && currentSongIndex < songTitles.size()) {
            //String Title = songTitles.get(currentSongIndex);
           // if (Title != null) {
                Notification  notification  =
                        new NotificationCompat.Builder(this, "channel_id")
                                .setContentTitle("Lecture en cours")
                                .setContentText(Title)

                                .setSmallIcon(R.drawable.play)
                                .addAction(R.drawable.pause, "Play/Pause", pPPendingIntent)
                                .addAction(R.drawable.backward, "Previous", pPrevPendingIntent)
                                .addAction(R.drawable.forward, "Next", pNextPendingIntent)
                                .setContentIntent(pendingIntent)
                                .setPriority(Notification.PRIORITY_MAX)
                                .build();
                startForeground(110, notification);
            //}
        }
        ////
        if (!songTitles.isEmpty()) {
            playSong(0);
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the service is destroyed
        mediaPlayer.stop();
        unregisterReceiver(recv);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service does not support binding
        return null;
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("PlayPause")) {
                //Toast.makeText(MyService.this, currentSongIndex, Toast.LENGTH_SHORT).show();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        }
    }

    private class NextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            currentSongIndex++;
            if (currentSongIndex >= songTitles.size()) {
                currentSongIndex = 0;

            }
            playSong(currentSongIndex);


            // Update the title of the notification(Does not work )
            Title = songTitles.get(currentSongIndex);
            //notificationBuilder.setContentTitle(Title);
            //notificationManager.notify(1, notificationBuilder.build());
        }

    }


    public class PrevReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentSongIndex--;
            if (currentSongIndex <= songTitles.size()) {
                currentSongIndex = 0;
            }
            playSong(currentSongIndex);
        }
    }


    @SuppressLint("Range")
    private void playSong(int index) {
        try {
            mediaPlayer.reset();
            String songTitle = songTitles.get(index);

            Title = songTitles.get(currentSongIndex);

            //that works It updates the title on the Toast but not on the notification!

            Toast.makeText(MyService.this, "songTitle:  "+ Title, Toast.LENGTH_SHORT).show();


            // Retrieve the song file path using the song title
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.Media.DATA};
            String selection = MediaStore.Audio.Media.TITLE + "=?";
            String[] selectionArgs = {songTitle};
            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
            String filePath = null;
            if (cursor != null && cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                cursor.close();
            }

            // Get the Uri for the song file path
            Uri songUri = Uri.parse("file://" + filePath);

            mediaPlayer.setDataSource(getApplicationContext(), songUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Play the next song when this one finishes
                    int nextIndex = (index + 1) % songTitles.size();
                    playSong(nextIndex);
                }
            });
        } catch (Exception e) {
            Log.e("Error playing song", String.valueOf(e));
        }
    }
}





