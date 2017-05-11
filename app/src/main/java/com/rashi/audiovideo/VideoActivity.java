package com.rashi.audiovideo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener{
    VideoView videoView;
    Button videoUpload;
    Uri rcvUri;
    String rcvPath;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView)findViewById(R.id.videoView);
        videoUpload = (Button)findViewById(R.id.buttonVideoUpload);
        Intent rcvVideo = getIntent();
        rcvUri = rcvVideo.getParcelableExtra("VideoUri");
        rcvPath = rcvVideo.getStringExtra("Video");
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(rcvUri);
        videoView.requestFocus();
        videoView.start();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Video...");
        dialog.setCancelable(false);
        videoUpload.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.buttonVideoUpload){
            Toast.makeText(this,"Button Clicked",Toast.LENGTH_LONG).show();
            MyTask myTask = new MyTask();
            myTask.execute();
        }
    }

    class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            Toast.makeText(VideoActivity.this,"Upload started",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            //String existingFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypic.png";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 5 * 1024 * 1024;
            String responseFromServer = "";
            //String urlString = "http://mywebsite.com/directory/upload.php";

            try {

                //------------------ CLIENT REQUEST
                FileInputStream fileInputStream = new FileInputStream(new File(rcvPath));
                // open a URL connection to the Servlet
                URL url = new URL(Util.urlAudioUpload);
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + rcvPath + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams
                Log.e("Debug", "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }

            //------------------ read the SERVER RESPONSE
            try {

                inStream = new DataInputStream(conn.getInputStream());
                String str;

                while ((str = inStream.readLine()) != null) {

                    Log.e("Debug", "Server Response " + str);

                }

                inStream.close();
                //dialog.dismiss();

            } catch (IOException ioex) {
                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.dismiss();
            Toast.makeText(VideoActivity.this,"Upload Finished",Toast.LENGTH_LONG).show();
        }
    }
}
