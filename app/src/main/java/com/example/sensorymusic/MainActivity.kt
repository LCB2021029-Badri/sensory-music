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

        //permission
        if (checkPermission() == false) {
            requestPermission()
            return
        }

//        checkPermissionNew()

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
            binding.noSongs.visibility = View.INVISIBLE
        } else { // if the songs existS
            //showing songs in the recyclerview
            binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
            binding.recyclerView.setAdapter(MusicListAdapter(songsList, applicationContext))
        }


    }

    //    storage checking and request permissions
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) { //if the permission is denied
            showRotationalDialogForPermission()
            Toast.makeText(this, "storage permission is required", Toast.LENGTH_SHORT).show()
        } else { //if the permission is accepted
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                69
            )
        }
    }


    // dialog box
    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions " + "required for this feature. It can be made under App Settings!!!")
            .setPositiveButton("Go to Settings(Badri)") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel(Badri)") { dialog, _ ->
                dialog.dismiss()
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