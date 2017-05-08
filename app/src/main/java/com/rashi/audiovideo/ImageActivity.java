package com.rashi.audiovideo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView;
    Button btnImageUpload;
    Bitmap bitmap;

    //Uri filePath;

    String encodedImage;
    RequestQueue requestQueue;

    ProgressDialog progressDialog;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView)findViewById(R.id.imageView);
        Intent rcvImage = getIntent();
        Uri rcvUri = rcvImage.getParcelableExtra("Image");
        String path = rcvImage.getStringExtra("Path");

       //byte[] byteArray = rcvImage.getByteArrayExtra("Bitmap");
       //bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //bmp =  rcvImage.getParcelableExtra("Bitmap");
        imageView.setImageURI(rcvUri);
        btnImageUpload = (Button) findViewById(R.id.buttonImageUpload);
        btnImageUpload.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image..");
        progressDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonImageUpload){
            Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Log.i("Withoutimage",image.toString());
            encodedImage = getStringImage(image);
            Log.i("Encoded",encodedImage);

            sendImage();
        }

    }

    void sendImage() {

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.urlImageUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Toast.makeText(getApplication(), "Some Volley Error" + error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("imageName","Abc");
                map.put("imageData", encodedImage);
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encoded;
    }
}
