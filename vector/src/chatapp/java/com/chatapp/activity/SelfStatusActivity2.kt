package com.chatapp.activity

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bolaware.viewstimerstory.Momentz
import com.bolaware.viewstimerstory.MomentzCallback
import com.bolaware.viewstimerstory.MomentzView
import com.chatapp.CR
import com.chatapp.fragments.SeenFragment
import com.chatapp.fragments.SeenFragment.Companion.getInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import im.vector.R
import kotlinx.android.synthetic.chatapp.activity_self_status2.*
import java.net.URLConnection

class SelfStatusActivity2 : AppCompatActivity(), MomentzCallback, SeenFragment.ISeenDismissListener {

    var seen: FloatingActionButton? = null
    val counter: Int = 0
    lateinit var iSeenListener: SeenFragment.ISeenDismissListener
    lateinit var currentMomentz: Momentz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_status2)
        seen = findViewById(R.id.seen_button)
        val internetLoadedImageView = ImageView(this)
        val internetLoadedVideo = VideoView(this)
        val l = ArrayList<MomentzView>()
        for (path in CR.resources) {
            val mimeType = URLConnection.guessContentTypeFromName(path)
            val isVideo = mimeType != null && mimeType.startsWith("video")
//            if (path.contains(".mp4", ignoreCase = true)
//                    || path.contains(".avi", ignoreCase = true)
//                    || path.contains(".flv", ignoreCase = true)
//                    || path.contains(".flv", ignoreCase = true)
//            )
            if(isVideo)
            {
                l.add(MomentzView(internetLoadedVideo, 60))
            } else {
                l.add(MomentzView(internetLoadedImageView, 10))
            }
        }
        Momentz(this, l, container, this).start()
        iSeenListener = this
        seen?.setOnClickListener(View.OnClickListener {
            try {
                currentMomentz.pause(false)
                val b = Bundle()
                b.putString("iurl", CR.resources[counter])
                val f = getInstance(b)
                f.setListener(iSeenListener)
                f.show(supportFragmentManager, "seen fragment")
            } catch (e: Exception) {
            }
        })
    }

    override fun done() {
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onNextCalled(view: View, momentz: Momentz, index: Int) {
        currentMomentz = momentz
        if (view is VideoView) {
            momentz.pause(true)
            playVideo(view, index, momentz)
        } else if ((view is ImageView)) {
            momentz.pause(true)
            val url = CR.resources.get(index)
            Picasso.with(this).invalidate(url);
            Picasso.with(this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(view, object : Callback {
                        override fun onSuccess() {
                            momentz.resume()
                        }
                        override fun onError() {
                            momentz.resume()
                        }
                    })
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun playVideo(videoView: VideoView, index: Int, momentz: Momentz) {
        val str = CR.resources.get(index)
        val uri = Uri.parse(str)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
        videoView.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    momentz.editDurationAndResume(index, (videoView.duration) / 1000)
                    return true
                }
                return false
            }
        })
    }

    override fun onDismissClick() {
        currentMomentz.resume()
    }

}