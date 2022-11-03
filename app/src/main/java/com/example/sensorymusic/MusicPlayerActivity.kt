package com.example.sensorymusic

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.sensorymusic.MyMediaPalyer.instance
import com.example.sensorymusic.MyMediaPalyer.currentIndex
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.SeekBar
import com.example.sensorymusic.AudioModel
import android.media.MediaPlayer
import com.example.sensorymusic.MyMediaPalyer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import com.example.sensorymusic.R
import com.example.sensorymusic.MusicPlayerActivity
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.ArrayList
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MusicPlayerActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var titleTv: TextView
    private lateinit var currentTimeTv: TextView
    private lateinit var totalTimeTv: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var pausePlay: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var previousBtn: ImageView
    private lateinit var musicIcon: ImageView
    private lateinit var songsList: ArrayList<AudioModel>
    private lateinit var currentSong: AudioModel

    //imlpementing tsensors to my app
    private lateinit var sensorManager: SensorManager

    private var mediaPlayer = instance
    var x = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        titleTv = findViewById(R.id.song_title)
        currentTimeTv = findViewById(R.id.current_time)
        totalTimeTv = findViewById(R.id.total_time)
        seekBar = findViewById(R.id.seek_bar)
        pausePlay = findViewById(R.id.pause_play)
        nextBtn = findViewById(R.id.next)
        previousBtn = findViewById(R.id.previous)
        musicIcon = findViewById(R.id.music_icon_big)

        titleTv.setSelected(true)

        // here we get the song list passed from the adapter through the intent
        songsList = (intent.getSerializableExtra("LIST") as ArrayList<AudioModel>?)!!

        //to play the current song using the following method
        setResourcesWithMusic()

        //observing changes in UI and reacting accordingly
        runOnUiThread(object : Runnable {
            override fun run() {
                //we check each second from UI to activate the seekbar
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer!!.currentPosition)
                    currentTimeTv.setText(convertToMMSS(mediaPlayer!!.currentPosition.toString() + ""))

                    //image rotation
                    if (mediaPlayer!!.isPlaying) {
                        pausePlay.setImageResource(R.drawable.icon_pause)
                        musicIcon.setRotation(x++.toFloat())
                    } else {
                        pausePlay.setImageResource(R.drawable.icon_play)
                        musicIcon.setRotation(0f)
                    }
                }

//                // if the song is completed
//                if(mediaPlayer != null && mediaPlayer!!.currentPosition == mediaPlayer!!.duration){
//                    mediaPlayer!!.start()
//                }


                //smoothen the seekbar
                Handler().postDelayed(this, 100)
            }
        })

        // if the seekbar is changes by the user
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        //implementing sensor
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpSensor()

        // if the current song is completed
        mediaPlayer!!.setOnCompletionListener {
            playNextSong()
        }

    }

    //resources for playing the current song
    fun setResourcesWithMusic() {
        currentSong = songsList!![currentIndex]
        titleTv!!.text = currentSong!!.title
        totalTimeTv!!.text = convertToMMSS(currentSong!!.duration)
        pausePlay!!.setOnClickListener { v: View? -> pausePlay() }
        nextBtn!!.setOnClickListener { v: View? -> playNextSong() }
        previousBtn!!.setOnClickListener { v: View? -> playPreviousSong() }

        //when the song is clicked form the recycler list
        playMusic()
    }

    //methods to play song-----------
    private fun playMusic() {
        //we need a media player instance
        mediaPlayer!!.reset()

        try {
            mediaPlayer!!.setDataSource(currentSong!!.path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            seekBar!!.progress = 0
            seekBar!!.max = mediaPlayer!!.duration
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "there is an error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playNextSong() {
        //if last song
        if (currentIndex == songsList!!.size - 1) {
            currentIndex = songsList!!.size - 1
            mediaPlayer!!.reset()
            setResourcesWithMusic()
        }else{
            //if not last song
            currentIndex += 1
            mediaPlayer!!.reset()
            setResourcesWithMusic()
        }
    }

    private fun playPreviousSong() {
        //if first song
        if (currentIndex == 0) {
            currentIndex = 0
            mediaPlayer!!.reset()
            setResourcesWithMusic()
        }else{
            //if not first song
            currentIndex -= 1
            mediaPlayer!!.reset()
            setResourcesWithMusic()
        }
    }

    private fun pausePlay() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        } else {
            mediaPlayer!!.start()
        }
    }

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
//            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_FASTEST,SensorManager.SENSOR_DELAY_FASTEST)
            sensorManager!!.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
//            sensorManager!!.registerListener(this, it,1000000,1000000)
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {  // we retrtieve information if it is this type of sensor
            val sides = p0.values[0]
            val upDown = p0.values[1]

            //control using sensor using following if statements
//            Timer().schedule(1000){}
                if (sides.toFloat() > 8 && sides.toFloat() < 9) {
                    // we call previous method
                    playPreviousSong()
                }
                if(sides.toFloat() < -8 && sides.toFloat() > -9){
                    // we call next function
                    playNextSong()
                }


//            square.apply {
//                rotationX = upDown *3f
//                rotationY = sides *3f
//                rotation = -sides
//                translationX = sides * -10
//                translationY = upDown * 10
//            }
//
//            val color = if(upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
//            square.setBackgroundColor(color)
//            square.text = "U/D - ${upDown.toInt()}\n L/R - ${sides.toInt()}"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }


//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        super.onSaveInstanceState(outState, outPersistentState)
//
//        val audioSaved : AudioModel = currentSong
//        outState.get("savedSong", currentSong)
//
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        val usingSongIndex : Int = savedInstanceState.getInt("savedSong", currentIndex)
//        currentIndex = usingSongIndex
//        currentSong = songsList[currentIndex]
//    }




    companion object {
        //for the millisecond format of the duration
        fun convertToMMSS(duration: String): String {
            //first convert duration to milliseconds then to seconds and minutes
            val millis = duration.toLong()
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            )
        }
        //
    }
}