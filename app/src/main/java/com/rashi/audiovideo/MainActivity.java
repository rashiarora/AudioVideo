package com.rashi.audiovideo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnAudio, btnImage, btnVideo;
    String selectedPath;
    Bitmap bitmap;
    byte[] byteArray;

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
                //filePath = data.getData();
               /*try {
                   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //byteArray = stream.toByteArray();

                    //Intent in1 = new Intent(this, ImageActivity.class);
                    //in1.putExtra("image",byteArray);
                   // imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                selectedPath = getImagePath(selectedImageUri);
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byteArray = stream.toByteArray();
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Intent i = new Intent(this, ImageActivity.class);
                i.putExtra("Path",selectedPath);
                i.putExtra("Image",selectedImageUri);
                Log.i("Path",selectedPath);
                //i.putExtra("Bitmap",bitmap);
                startActivity(i);
            } else if(requestCode == 102){
                System.out.println("SELECT_AUDIO");
                Uri selectedAudioUri = data.getData();
                selectedPath = getAudioPath(selectedAudioUri);
                Log.i("uri",selectedAudioUri.toString());
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Log.i("selected",selectedPath);
                //Intent i = new Intent(this, AudioActivity.class);
                Intent i = new Intent(this, AudioUpload.class);
                i.putExtra("Audio",selectedPath);
                i.putExtra("AudioUri",selectedAudioUri);
                startActivity(i);
            }else if(requestCode== 103){
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();
                selectedPath = getAudioPath(selectedVideoUri);
                Log.i("uri",selectedVideoUri.toString());
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                Intent i = new Intent(this, VideoActivity.class);
                i.putExtra("Video",selectedVideoUri);
                startActivity(i);
            }

        }
    }

    public String getAudioPath(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getImagePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
