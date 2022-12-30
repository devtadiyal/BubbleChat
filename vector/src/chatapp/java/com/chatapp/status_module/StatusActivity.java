package com.chatapp.status_module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.chatapp.C;
import com.chatapp.CR;
import com.chatapp.Settings;
import com.chatapp.network.VolleyApi;
import com.chatapp.share.RecentModel;
import com.chatapp.sip.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.vector.R;

public class StatusActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private static int PROGRESS_COUNT = 6;
String u="";
    private StoriesProgressView storiesProgressView;
    private ImageView image;

    private int counter = 0;

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };

    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };
    RecentModel model = new RecentModel();
    boolean isSelf = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        u = settings.getString("Username", "");
        PROGRESS_COUNT = CR.resources.size();
        model = getIntent().getParcelableExtra("model");
        isSelf = getIntent().getBooleanExtra("self", false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_status);


        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        counter = 0;
        storiesProgressView.startStories(counter);

        image = (ImageView) findViewById(R.id.image);

        try {
            updateStatus();
            Glide.with(this).load(CR.resources.get(0)).into(image);
        } catch (Exception e) {

        }
        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
        try {
            if (CR.resources.size() - 1 == counter)
                finish();
            int a=++counter;
            updateStatus();
            Glide.with(this).load(CR.resources.get(a)).into(image);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrev() {
        try {
            if ((counter - 1) < 0) return;
            Glide.with(this).load(CR.resources.get(--counter)).into(image);
        } catch (Exception e) {

        }
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void updateStatus() {
        String url = Settings.UPDATE_STATUS;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("s", "S");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", u);
                params.put("time", getTime());
                params.put("imageID", CR.resources.get(counter));
                return params;
            }

        };
        new VolleyApi(this).getRequestQueue().add(stringRequest);
    }

    private String getTime() {
        Date date = Calendar.getInstance().getTime();
        String s = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);
        s.replace(" ", "/");
        return s;

    }
}
