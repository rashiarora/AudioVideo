package com.rashi.audiovideo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    VideoView videoView;
    Button videoUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView)findViewById(R.id.videoView);
        videoUpload = (Button)findViewById(R.id.buttonVideoUpload);
        Intent rcvImage = getIntent();
        Uri rcvUri = rcvImage.getParcelableExtra("Video");
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(rcvUri);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
