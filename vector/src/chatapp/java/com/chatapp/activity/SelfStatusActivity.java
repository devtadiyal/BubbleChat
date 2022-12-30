package com.chatapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.chatapp.CR;
import com.chatapp.fragments.SeenFragment;
import com.chatapp.share.RecentModel;
import com.chatapp.status_module.StoriesProgressView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import im.vector.R;

public class SelfStatusActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, SeenFragment.ISeenDismissListener {

    private static int PROGRESS_COUNT = 6;

    private StoriesProgressView storiesProgressView;
    private ImageView image;
    private VideoView video;

    private int counter = 0;

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
    FloatingActionButton seen;
    ProgressDialog pDialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PROGRESS_COUNT = CR.resources.size();
        pDialog = new ProgressDialog(this); //Your Activity.this
        pDialog.setCancelable(false);
        model = getIntent().getParcelableExtra("model");
        isSelf = getIntent().getBooleanExtra("self", false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_self_status);


        seen = findViewById(R.id.seen_button);
        storiesProgressView = findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(30000L);
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
        counter = 0;
        storiesProgressView.startStories(counter);

        image = (ImageView) findViewById(R.id.image);
        video = (VideoView) findViewById(R.id.video);

        initialPlay(CR.resources.get(0));
//            Glide.with(this).load().into(image);
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
        SeenFragment.ISeenDismissListener l = this;
        seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    storiesProgressView.pause();
                    Bundle b = new Bundle();
                    b.putString("iurl", CR.resources.get(counter));
                    SeenFragment f = SeenFragment.Companion.getInstance(b);
                    f.setListener(l);
                    f.show(getSupportFragmentManager(), "seen fragment");
                } catch (Exception e) {

                }
            }
        });




    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initialPlay(String path) {
        if (path.contains(".mp4")) {
            image.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            storiesProgressView.pause();
            video.requestFocus();
            video.setVideoURI(Uri.parse(path));
            video.start();
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp.isPlaying()) {
                        storiesProgressView.resume();
                    }
                }
            });

            video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        pDialog.show();
                        storiesProgressView.pause();
                    }
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        pDialog.dismiss();
                        storiesProgressView.resume();
                    }
                    return false;
                }
            });
        } else {
            image.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
            Glide.with(this).load(path).into(image);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onNext() {
        try {
            if (CR.resources.size() - 1 == counter)
                finish();
            if (counter > CR.resources.size() - 1) {
                finish();
            }
            String path = CR.resources.get(++counter);
            if (path.contains(".mp4")) {
                image.setVisibility(View.GONE);
                video.setVisibility(View.VISIBLE);
                storiesProgressView.pause();
                video.requestFocus();
                video.setVideoURI(Uri.parse(path));
                video.start();
                video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp.isPlaying()) {
                            storiesProgressView.resume();
                        }
                    }
                });
                video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            pDialog.show();
                            storiesProgressView.pause();
                        }
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            pDialog.dismiss();
                            storiesProgressView.resume();
                        }
                        return false;
                    }
                });
            } else {
                image.setVisibility(View.VISIBLE);
                video.setVisibility(View.GONE);
                Glide.with(this).load(path).into(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrev() {
        try {
            if ((counter - 1) < 0) return;
            String path = CR.resources.get(--counter);
            if (path.contains(".mp4")) {
                image.setVisibility(View.GONE);
                video.setVisibility(View.VISIBLE);
                video.setVideoURI(Uri.parse(path));
                video.start();
            } else {
                image.setVisibility(View.VISIBLE);
                video.setVisibility(View.GONE);
                Glide.with(this).load(path).into(image);
            }
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

    @Override
    public void onDismissClick() {
        try {
            storiesProgressView.resume();
        } catch (Exception e) {
        }
    }
}
