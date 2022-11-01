package com.example.sensorymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        // here we get the song list passed from the adapter through the intent
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        //to play the current song using the following method
        setResourcesWithMusic();
    }

    //resources for playing the current song
    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPalyer.INSTANCE.getCurrentIndex());
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        //when the song is clicked form the recycler list
        playMusic();
    }



    //methods to play song-----------
    private void playMusic(){

    }
    private void playNextSong(){

    }
    private void playPreviousSong(){

    }
    private void pausePlay(){

    }

    //for the millisecond format of the duration
    public static String convertToMMSS(String duration){
        //first convert duration to milliseconds then to seconds and minutes
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toSeconds(1));
    }

}