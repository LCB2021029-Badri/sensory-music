package com.example.sensorymusic

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
import android.view.View
import android.widget.ImageView
import com.example.sensorymusic.R
import com.example.sensorymusic.MusicPlayerActivity
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : AppCompatActivity() {
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
                    if (mediaPlayer!!.isPlaying) {
                        pausePlay.setImageResource(R.drawable.icon_pause)
                        musicIcon.setRotation(x++.toFloat())
                    } else {
                        pausePlay.setImageResource(R.drawable.icon_play)
                        musicIcon.setRotation(0f)
                    }
                }
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
        //if not last song
        currentIndex += 1
        mediaPlayer!!.reset()
        setResourcesWithMusic()
        //if last song
        if (currentIndex == songsList!!.size - 1) {
            return
        }
    }

    private fun playPreviousSong() {
        //if not first song
        currentIndex -= 1
        mediaPlayer!!.reset()
        setResourcesWithMusic()
        //if first song
        if (currentIndex == 0) {
            return
        }
    }

    private fun pausePlay() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        } else {
            mediaPlayer!!.start()
        }
    }

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
    }
}