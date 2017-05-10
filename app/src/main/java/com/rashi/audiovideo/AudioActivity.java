package com.rashi.audiovideo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    SeekBar seekBar;
    Button play, stop, upload;
    MediaPlayer mediaPlayer;
    Uri rcvUri;
    String recvPath;
    RequestQueue requestQueue;
    String encodedAudio;
    byte[] byteArray,soundBytes;
    ProgressDialog dialog;
    static final int BUFFER_SIZE = 4096;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        play = (Button) findViewById(R.id.buttonPlay);
        stop = (Button) findViewById(R.id.buttonStop);
        upload = (Button) findViewById(R.id.buttonAudioUpload);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        upload.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        Intent rcvImage = getIntent();
        rcvUri = rcvImage.getParcelableExtra("AudioUri");
        recvPath = rcvImage.getStringExtra("Audio");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, rcvUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Audio...");
        dialog.setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonPlay) {

            mediaPlayer.start();

        } else if (v.getId() == R.id.buttonStop) {

            mediaPlayer.stop();

        } else if (v.getId() == R.id.buttonAudioUpload) {
            dialog.show();
            //DataOutputStream dos = new DataOutputStream();
          /*  File file = new File(rcvUri.getPath());
            byte[] b = new byte[(int) file.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(b);
                for (int i = 0; i < b.length; i++) {
                    System.out.print((char) b[i]);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                Toast.makeText(this,"File not found"+e.toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e1) {
                System.out.println("Error Reading The File.");
                Toast.makeText(this,"Error Reading The File.",Toast.LENGTH_LONG).show();
                e1.printStackTrace();
            }
            encodedAudio = Base64.encodeToString(b, Base64.DEFAULT);

            Log.i("encodedAudio", encodedAudio); */
           /* try {
                InputStream inputStream =
                        getContentResolver().openInputStream(Uri.fromFile(new File(rcvUri.getPath())));

                soundBytes = new byte[inputStream.available()];
                soundBytes = toByteArray(inputStream);
                encodedAudio = Base64.encodeToString(soundBytes,Base64.DEFAULT);
                Log.i("encode", encodedAudio);
                Toast.makeText(this, encodedAudio, Toast.LENGTH_LONG).show();

                Toast.makeText(this, "Recordin Finished"+ " " + soundBytes, Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                e.printStackTrace();
            } */

            uploadOnCloud();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setMax(mediaPlayer.getDuration());
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


    void uploadOnCloud() {

      //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //byteArray = byteArrayOutputStream.toByteArray();

        //String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+rcvUri.getPath();

        //Toast.makeText(this, encodedAudio, Toast.LENGTH_LONG).show();
/*
       try {
          byteArray= convert(recvPath);
            encodedAudio = Base64.encodeToString(byteArray,Base64.DEFAULT);
            Log.i("encode", encodedAudio);
            Toast.makeText(this, encodedAudio, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //final File file=new File(recvPath);

        /*byte[] soundBytes;

        try {
            InputStream inputStream =
                    getContentResolver().openInputStream(Uri.fromFile(new File(recvPath)));

            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);
            encodedAudio = Base64.encodeToString(soundBytes,Base64.DEFAULT);
            Log.i("encode", encodedAudio);
            Toast.makeText(this, encodedAudio, Toast.LENGTH_LONG).show();

            Toast.makeText(this, "Recordin Finished"+ " " + soundBytes, Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            e.printStackTrace();
        }*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.urlAudioUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Message from server", response);
                dialog.dismiss();
                Toast.makeText(getApplication(), "Attachment Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Message from server", error.toString());
                dialog.dismiss();
                Toast.makeText(getApplication(), "Attachment Uploaded Unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("audioName", "abc");
                map.put("audioData", encodedAudio);
                return map;
            }
        };
        requestQueue.add(stringRequest);

    }
    byte[] convert(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();
//        encodedAudio = Base64.encodeToString(bytes, Base64.DEFAULT);
//        Log.i("audio", encodedAudio);
//        Toast.makeText(this, encodedAudio, Toast.LENGTH_LONG).show();

        return bytes;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

}


