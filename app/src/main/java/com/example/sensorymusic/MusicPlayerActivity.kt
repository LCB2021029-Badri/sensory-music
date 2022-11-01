package com.example.sensorymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPalyer.INSTANCE.getInstance();
    int x =0;

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

        titleTv.setSelected(true);

        // here we get the song list passed from the adapter through the intent
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        //to play the current song using the following method
        setResourcesWithMusic();

        //observing changes in UI and reacting accordingly
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //we check each second from UI to activate the seekbar
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.icon_pause);
                        musicIcon.setRotation(x++);
                    } else {
                        pausePlay.setImageResource(R.drawable.icon_play);
                        musicIcon.setRotation(0);
                    }
                }
                //smoothen the seekbar
                new Handler().postDelayed(this, 100);
            }
        });

        // if the seekbar is changes by the user
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //resources for playing the current song
    void setResourcesWithMusic() {
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
    private void playMusic() {
        //we need a media player instance
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "there is an error", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextSong() {
        //if not last song
        MyMediaPalyer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        //if last song
        if (MyMediaPalyer.currentIndex == songsList.size() - 1) {
            return;
        }
    }

    private void playPreviousSong() {
        //if not first song
        MyMediaPalyer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        //if first song
        if (MyMediaPalyer.currentIndex == 0) {
            return;
        }
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    //for the millisecond format of the duration
    public static String convertToMMSS(String duration) {
        //first convert duration to milliseconds then to seconds and minutes
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}