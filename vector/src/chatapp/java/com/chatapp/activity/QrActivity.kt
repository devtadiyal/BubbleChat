package com.chatapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chatapp.C
import im.vector.Matrix
import im.vector.R
import im.vector.util.VectorUtils
import kotlinx.android.synthetic.chatapp.activity_courier.*
import kotlinx.android.synthetic.chatapp.activity_qr.*
import kotlinx.android.synthetic.chatapp.activity_qr.settings_avatar
import org.matrix.androidsdk.MXSession

class QrActivity : AppCompatActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        btb.setOnClickListener(this)
        tws.setOnClickListener(this)
        loan.setOnClickListener(this)
        tmm.setOnClickListener(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "QR Code Trasaction"
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