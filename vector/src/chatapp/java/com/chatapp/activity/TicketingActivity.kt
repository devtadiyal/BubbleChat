package com.chatapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chatapp.C
import im.vector.Matrix
import im.vector.R
import im.vector.util.VectorUtils
import kotlinx.android.synthetic.chatapp.activity_qr.*
import kotlinx.android.synthetic.chatapp.activity_ticketing.*
import kotlinx.android.synthetic.chatapp.activity_ticketing.settings_avatar
import kotlinx.android.synthetic.chatapp.activity_ticketing.toolbar
import org.matrix.androidsdk.MXSession

class TicketingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticketing)
        train.setOnClickListener(this)
        taxi.setOnClickListener(this)
//        smartcab.setOnClickListener(this)
        bus.setOnClickListener(this)
        fligt.setOnClickListener(this)
        hotels.setOnClickListener(this)
//        bike.setOnClickListener(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Ticketing"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        var mSession: MXSession? = Matrix.getInstance(this).defaultSession
        VectorUtils.loadUserAvatar(this, mSession, settings_avatar, mSession!!.myUser)

    }

    override fun onClick(v: View?) {
        C.showErr()
    }

}