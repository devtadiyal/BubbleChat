package com.chatapp.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyListener {
    void onResponse(JSONObject jsonObject, String tag);
    void onError(VolleyError error, String tag);

}
