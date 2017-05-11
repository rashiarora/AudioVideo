package com.rashi.audiovideo;
import org.apache.commons.codec.binary.Base64;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class AudioUpload extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    SeekBar seekBar;
    Button play, stop, upload;
    MediaPlayer mediaPlayer;
    Uri rcvUri;
    String recvPath;
    RequestQueue requestQueue;
    String encodedAudio;
    byte[] byteArray, soundBytes;
    ProgressDialog dialog;
    static final int BUFFER_SIZE = 4096;

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
        Log.i("path", rcvUri.toString());
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
        if (v.getId() == R.id.buttonPlay1) {

            mediaPlayer.start();
            Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();

        } else if (v.getId() == R.id.buttonStop1) {

            mediaPlayer.stop();
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();

        } else if (v.getId() == R.id.buttonAudioUpload1) {
            //dialog.show();
            //doFileUpload();
           /* file= new File(rcvUri.getPath());

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
*/
          MyTask myTask = new MyTask();
            myTask.execute();
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

    class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            Toast.makeText(AudioUpload.this,"Upload started",Toast.LENGTH_LONG).show();
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
                FileInputStream fileInputStream = new FileInputStream(new File(recvPath));
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
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + recvPath + "\"" + lineEnd);
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
            Toast.makeText(AudioUpload.this,"Upload Finished",Toast.LENGTH_LONG).show();
        }
    }
}





