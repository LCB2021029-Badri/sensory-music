package com.example.sensorymusic

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sensorymusic.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder.MultiPermissionListener
import com.karumi.dexter.DexterBuilder.SinglePermissionListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var songsList = ArrayList<AudioModel>()

//    lateinit var recyclerView: RecyclerView
//    lateinit var noMusicText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fixing orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)


//        recyclerView = findViewById(R.id.recycler_view)
//        noMusicText = findViewById(R.id.no_songs)

        // permission is only asked if checked resuld is false
        if (checkPermission() == false) {
            showRotationalDialogForPermission()
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
            if (File(songData.path).exists() && (((cursor.getString(2)
                    .toFloat()) / 60000) % 60000) > 1.9
            ) { //check for the existence of songs (sometimes may not due to exceptions) and duration greater than 2 minutes
                songsList.add(songData)
            }
        }

        // confirm the songList size
        if (songsList.size == 0) {    // if there are no songs in the list
            binding.noSongs.visibility = View.VISIBLE
        } else { // if the songs existS
            //showing songs in the recyclerview
            binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
            binding.recyclerView.setAdapter(MusicListAdapter(songsList, applicationContext))
        }


    }

    // permission checking
    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            Toast.makeText(this,"grant permission in app settings",Toast.LENGTH_SHORT)
            return false
        }
    }

    // dialog box for permission
    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions " + "required for this feature. It can be made under app Settings")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    this.finish()
                    startActivity(this.intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this,"grant permission in app settings",Toast.LENGTH_SHORT)
                this.finish()
                this.startActivity(intent)
            }.show()
    }

    // updates when we go back from MusicPlayerActivity to MainActivity
    override fun onResume() {
        super.onResume()
        if (binding.recyclerView != null) {
            binding.recyclerView!!.adapter = MusicListAdapter(songsList, applicationContext)
        }
    }

}