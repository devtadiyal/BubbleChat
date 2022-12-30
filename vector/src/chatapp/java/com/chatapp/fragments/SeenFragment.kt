package com.chatapp.fragments

import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.chatapp.C
import com.chatapp.SeenPeopleModel
import com.chatapp.Settings
import com.chatapp.adapters.SeenPeopleAdapter
import com.chatapp.network.VolleyApi
import com.chatapp.share.ShareFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import im.vector.R
import kotlinx.android.synthetic.chatapp.fragment_seen.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SeenFragment : BottomSheetDialogFragment() {

    public lateinit var l: ISeenDismissListener

    companion object {
        fun getInstance(b: Bundle): SeenFragment {
            val f = SeenFragment()
            f.arguments = b
            return f
        }

    }

    fun setListener(l: ISeenDismissListener) {
        this.l = l
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(activity)
        getList()
    }

    private fun getList() {
        val url = ""
        showPg()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { s ->
            hidePg()
            try {
                setData(JSONObject(s))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            hidePg()
            dismiss()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = C.getUserName()
                params["image_id"] = arguments?.getString("iurl").toString()
                return params
            }
        }
        VolleyApi(context).requestQueue.add(stringRequest)
    }

    private fun hidePg() {
        try {
            pg.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    private fun showPg() {
        try {
            pg.visibility = View.VISIBLE
        } catch (e: Exception) {
        }
    }

    private fun setData(s: JSONObject) {
        try {
            val l = ArrayList<SeenPeopleModel>()
            val d = s.getJSONArray("Users Viewed: ")
            val e = s.getJSONArray("Time viewed:")
            for (i in 0..d.length() - 1) {
                val sp = SeenPeopleModel()
                sp.no = d.get(i).toString()
                sp.time = e.get(i).toString()
                sp.name = getFullName(sp.no)
                l.add(sp)
            }
            val a = SeenPeopleAdapter(l)
            rv.adapter = a

        } catch (e: Exception) {
            dismiss()
            callResume()
            Toast.makeText(activity, "No seen available", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFullName(no: String): String {
        try {
            for (item in ShareFragment.localContactItemList) {
                if (no.equals(item.Phone.replace(" ", ""), ignoreCase = true)) {
                    return GetContactsName(item.ContactID)
                }
            }
        } catch (e: java.lang.Exception) {
            return ""
        }
        return ""
    }

    fun callResume() {
        try {
            l.onDismissClick()
        } catch (e: Exception) {

        }
    }

    interface ISeenDismissListener {
        fun onDismissClick();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        callResume()
    }

    private fun GetContactsName(ContactID: String): String {
        var displayName = ""
        val lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, Uri.encode(ContactID))
        val c = activity!!.contentResolver.query(lookupUri, arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null)
        try {
            c?.moveToFirst()
            displayName = c?.getString(0)?:""
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            c?.close()
        }
        return displayName
    }
}