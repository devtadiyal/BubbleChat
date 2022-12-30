package com.chatapp.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chatapp.Settings
import com.chatapp.adapters.RatesAdapter
import im.vector.R
import kotlinx.android.synthetic.chatapp.activity_call_rates.*
import org.json.JSONObject
import kotlin.error

class CallRatesActivity : AppCompatActivity() {
   var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_rates)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = "Call Rates"
        } catch (e: java.lang.Exception) {
        }
        getCallRates();
    }

    private fun getCallRates() {
        try {
            val pDialog: ProgressDialog
            pDialog = ProgressDialog(this)
            pDialog.setMessage("Please wait...")
            pDialog.setIndeterminate(false)
            pDialog.setCancelable(false)
            pDialog?.show()
            val settings = PreferenceManager.getDefaultSharedPreferences(this)
            val cust_id = Settings.asHex(Settings.encrypt(settings.getString("Username", ""), Settings.ENC_KEY).toByteArray())
            val cust_pass = Settings.asHex(Settings.encrypt(settings.getString("Password", ""), Settings.ENC_KEY).toByteArray())
            val url = Settings.GET_CALL_RATES
            val queue = Volley.newRequestQueue(this)
            val sr: StringRequest = object : StringRequest(Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String) {
                    var response = response
                    try {
                        pDialog?.dismiss()
                        response = response.trim { it <= ' ' }
                        val json = JSONObject(response)
                        if (json.getString("result").equals("success", ignoreCase = true)) {
                            val arr = json.getJSONArray("rates")
                            val list = ArrayList<Rates>()
                            for (i in 0 until arr.length()) {
                                val item = arr.getJSONObject(i)
                                list.add(Rates(item.getString("rate"),
                                        item.getString("prefix"),
                                        item.getString("destination")))
                            }
                            if (list.size > 0) {
                                setListData(list)
                            } else {
                                error?.visibility = View.VISIBLE
                                error?.setText("No Rate Found")
                            }

                        } else {
                            if (this != null) {
                                this@CallRatesActivity.runOnUiThread(Runnable { Toast.makeText(this@CallRatesActivity, "An error, please try again later.", Toast.LENGTH_LONG).show() })
                            }
                        }
                    } catch (e: Exception) {
                        pDialog?.dismiss()
                        e.printStackTrace()
                        if (this@CallRatesActivity != null) {
                            this@CallRatesActivity.runOnUiThread(Runnable { Toast.makeText(this@CallRatesActivity, "An Internal error, please try again later.", Toast.LENGTH_LONG).show() })
                        }
                    }
                }
            }, Response.ErrorListener { error ->
                pDialog?.dismiss()
                if (this@CallRatesActivity != null) {
                    this@CallRatesActivity.runOnUiThread(Runnable { Toast.makeText(this@CallRatesActivity, error.message, Toast.LENGTH_LONG).show() })
                }
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    return params
                }

                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/x-www-form-urlencoded"
                    return params
                }
            }
            queue.add(sr)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setListData(list: ArrayList<Rates>) {
        list_view.setVisibility(View.VISIBLE)
        val adapter = RatesAdapter(this, list)
        list_view.adapter = adapter
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}