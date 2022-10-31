package com.example.sensorymusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicText = findViewById(R.id.no_songs);

        //permission
        if (checkPermission() == false) {
            requestPermission();
            return;
        }

        //extracting all the songs from the database
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";   // filtering music and adding not 0 condition

        // all the music sample data are stored in this cursor
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

//        //yet to be filled
//        while(cursor.moveToNext()){
//            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
//            if(new File(songData.getPath()).exists())
//                songsList.add(songData);
//        }

        if(songsList.size()==0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }else{
            //recyclerview
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }

    }


    //storage checking and request permissions
    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {//if the permission is denied
            Toast.makeText(this, "storage permission is required", Toast.LENGTH_SHORT).show();
        } else { //if the permission is accepted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123321);

        }
    }

//    // yet to be filled
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (recyclerView != null) {
//            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
//        }
//    }


}
