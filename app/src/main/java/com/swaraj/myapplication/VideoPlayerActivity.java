package com.swaraj.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        VideoView videoView = findViewById(R.id.videoView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        intent = getIntent();
        if (intent != null) {
            String videoUrl = intent.getStringExtra("videoLink");
            Uri uri = Uri.parse(videoUrl);
            videoView.setVideoURI(uri);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener(){

            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        videoView.setOnCompletionListener(completionListener);
    }
}