package com.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chatapp.activity.CallDetailsHistoryActivity
import com.chatapp.activity.CallRatesActivity
import im.vector.R
import kotlinx.android.synthetic.chatapp.activity_settings_web.*
import java.lang.Exception

class SettingsWebActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_web)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            setTitle("Services")
        } catch (e: Exception) {
        }
        balance_view.setOnClickListener(this)
        credit_view.setOnClickListener(this)
        transfer_history.setOnClickListener(this)
        transfer_view.setOnClickListener(this)
        interswitchBuy.setOnClickListener(this)
        callDetailsReport.setOnClickListener(this)
        callRates.setOnClickListener(this)
    }

    fun setTitle(title: String?) {
        try {
            supportActionBar!!.title = title
        } catch (e: Exception) {
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.callRates -> {
                startActivity(Intent(this, CallRatesActivity::class.java))
            }
            R.id.callDetailsReport -> {
                startActivity(Intent(this, CallDetailsHistoryActivity::class.java))

            }
            else -> {
                C.showErr()

            }
        }
    }
}