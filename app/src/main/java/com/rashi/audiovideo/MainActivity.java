package com.rashi.audiovideo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnAudio, btnImage, btnVideo;
    String selectedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnImage =(Button)findViewById(R.id.buttonImage);
        btnAudio =(Button)findViewById(R.id.buttonAudio);
        btnVideo =(Button)findViewById(R.id.buttonVideo);
        btnImage.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonImage){
           openGalleryImage();
        }else if(v.getId()== R.id.buttonAudio){
            openGalleryAudio();
        }else if(v.getId()== R.id.buttonVideo){
            openGalleryVideo();
        }
    }

    public void openGalleryAudio(){

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Audio "),102);
    }

    public void openGalleryImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image "),101);
    }

    public void openGalleryVideo(){

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video "),103);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 101)
            {
                System.out.println("SELECT_IMAGE");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Intent i = new Intent(this, ImageActivity.class);
                i.putExtra("Image",selectedImageUri);
                startActivity(i);
            } else if(requestCode == 102){
                System.out.println("SELECT_AUDIO");
                Uri selectedAudioUri = data.getData();
                selectedPath = getPath(selectedAudioUri);
                Log.i("uri",selectedAudioUri.toString());
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Intent i = new Intent(this, AudioActivity.class);
                i.putExtra("Audio",selectedAudioUri);
                startActivity(i);
            }else if(requestCode== 103){
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                Log.i("uri",selectedVideoUri.toString());
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Intent i = new Intent(this, VideoActivity.class);
                i.putExtra("Video",selectedVideoUri);
                startActivity(i);
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
