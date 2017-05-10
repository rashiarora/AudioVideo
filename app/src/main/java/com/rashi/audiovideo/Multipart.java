package com.rashi.audiovideo;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by Admin on 10-05-2017.
 */

public class Multipart extends Request<NetworkResponse> {
    public Multipart(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {

    }

}
