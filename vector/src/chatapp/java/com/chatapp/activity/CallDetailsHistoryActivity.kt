package com.chatapp.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chatapp.Settings
import com.chatapp.adapters.CallDetailsAdapter
import im.vector.R
import kotlinx.android.synthetic.chatapp.activity_call_details_history.*
import org.json.JSONObject
import kotlin.error

class CallDetailsHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_details_history)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = "Call Details Report"
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
            val url = Settings.GET_CALL_DETAILS
            val queue = Volley.newRequestQueue(this)
            val sr: StringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String> {
                override fun onResponse(response: String) {
                    var response = response
                    try {
                        pDialog?.dismiss()
                        response = response.trim { it <= ' ' }
                        val json = JSONObject(response)
                        if (json.getString("result").equals("success", ignoreCase = true)) {
                            val arr = json.getJSONArray("msg")
                            val list = ArrayList<CallDetails>()
                            for (i in 0 until arr.length()) {
                                val item = arr.getJSONObject(i)
                                list.add(CallDetails(
                                        item.optString("date") ?: "",
                                        item.optString("country"),
                                        item.optString("duration"),
                                        item.optString("cost")
                                ))
                            }
                            if (list.size > 0) {
                                setListData(list)
                            } else {
                                error.visibility = View.VISIBLE
                                error.setText("No Call Details Found")
                            }

                        } else {
                            if (this != null) {
                                this@CallDetailsHistoryActivity.runOnUiThread(Runnable { Toast.makeText(this@CallDetailsHistoryActivity, "An error, please try again later.", Toast.LENGTH_LONG).show() })
                            }
                        }
                    } catch (e: Exception) {
                        pDialog?.dismiss()
                        e.printStackTrace()
                        if (this@CallDetailsHistoryActivity != null) {
                            this@CallDetailsHistoryActivity.runOnUiThread(Runnable { Toast.makeText(this@CallDetailsHistoryActivity, "An Internal error, please try again later.", Toast.LENGTH_LONG).show() })
                        }
                    }
                }
            }, Response.ErrorListener { error ->
                pDialog?.dismiss()
                if (this@CallDetailsHistoryActivity != null) {
                    this@CallDetailsHistoryActivity.runOnUiThread(Runnable { Toast.makeText(this@CallDetailsHistoryActivity, error.message, Toast.LENGTH_LONG).show() })
                }
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params.put("cust_id", cust_id);
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

    private fun setListData(list: ArrayList<CallDetails>) {
        list_view.setVisibility(View.VISIBLE)
        val adapter = CallDetailsAdapter(this, list)
        list_view.adapter = adapter
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}