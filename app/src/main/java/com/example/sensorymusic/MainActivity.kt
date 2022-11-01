package com.example.sensorymusic

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import android.widget.Toast
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var noMusicText: TextView
    var songsList = ArrayList<AudioModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        noMusicText = findViewById(R.id.no_songs)

        //permission
        if (checkPermission() == false) {
            requestPermission()
            return
        }

        //extracting all the songs from the database
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val selection =
            MediaStore.Audio.Media.IS_MUSIC + " != 0" // filtering music and adding not 0 condition

        // all the music sample data are stored in this cursor
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        //adding each songData to songList sing the cursor
        while (cursor!!.moveToNext()) {
            val songData = AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2))
            if (File(songData.path).exists()) { //check for the existence of songs (sometimes may not due to exceptions)
                songsList.add(songData)
            }
        }

        // confirm the songList size
        if (songsList.size == 0) {    // if there are no songs in the list
            noMusicText.visibility = View.INVISIBLE
        } else { // if the songs existS
            //showing songs in the recyclerview
            recyclerView.setLayoutManager(LinearLayoutManager(this))
            recyclerView.setAdapter(MusicListAdapter(songsList, applicationContext))
        }
    }

    //storage checking and request permissions
    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }

    fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) { //if the permission is denied
            Toast.makeText(this, "storage permission is required", Toast.LENGTH_SHORT).show()
        } else { //if the permission is accepted
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123321
            )
        }
    }

    // updates when we go back from MusicPlayerActivity to MainActivity
    override fun onResume() {
        super.onResume()
        if (recyclerView != null) {
            recyclerView!!.adapter = MusicListAdapter(songsList, applicationContext)
        }
    }
}