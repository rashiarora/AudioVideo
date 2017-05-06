package com.rashi.audiovideo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView)findViewById(R.id.imageView);
        Intent rcvImage = getIntent();
        Uri rcvUri = rcvImage.getParcelableExtra("Image");
        imageView.setImageURI(rcvUri);

    }
}
