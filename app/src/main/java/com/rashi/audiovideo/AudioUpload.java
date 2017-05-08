package com.rashi.audiovideo;
import org.apache.commons.codec.binary.Base64;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioUpload extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
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
    OkHttpClient okHttpClient;
    File file;
    String encodedBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_upload);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        play = (Button) findViewById(R.id.buttonPlay1);
        stop = (Button) findViewById(R.id.buttonStop1);
        upload = (Button) findViewById(R.id.buttonAudioUpload1);
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
    void httpCode(){
        okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("url").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });



    }

    MultipartBody uploadRequestBody(String audioFormat, String title, File file){
        MediaType MEDIA_TYPE = MediaType.parse("audio/" + audioFormat); // e.g. "image/png"
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("format", "json")
                .addFormDataPart("filename", title + "." + audioFormat) //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", "...", RequestBody.create(MEDIA_TYPE, file))
                .build();
    }
    HttpUrl buildURL() {
        return new HttpUrl.Builder()
                .scheme("http") //http
                .host("rashiarora.esy.es")
                .addPathSegment("training/audio_upload.php")//adds "/pathSegment" at the end of hostname
                .addQueryParameter("audioName", "abc") //add query parameters to the URL
                .addEncodedQueryParameter("audioData",encodedBase64 )//add encoded query parameters to the URL
                .build();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonPlay) {

            mediaPlayer.start();

        } else if (v.getId() == R.id.buttonStop) {

            mediaPlayer.stop();

        } else if (v.getId() == R.id.buttonAudioUpload) {
            dialog.show();

            file= new File(rcvUri.getPath());

            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                encodedBase64 = new String(Base64.encodeBase64(bytes));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                POST(okHttpClient, buildURL(), uploadRequestBody( "png", "title", file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpCode();
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

    //GET network request
    public static String GET(OkHttpClient client, HttpUrl url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //POST network request
    public static String POST(OkHttpClient client, HttpUrl url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}
