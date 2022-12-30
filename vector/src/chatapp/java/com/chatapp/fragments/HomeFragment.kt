package com.chatapp.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chatapp.*
import com.chatapp.activity.*
import com.chatapp.adapters.HomeAdapter
import com.chatapp.home.HomeSecond
import com.chatapp.home.Home_first
import com.chatapp.share.UserStatusActivity
import im.vector.Matrix
import im.vector.R
import im.vector.activity.VectorSettingsActivity
import kotlinx.android.synthetic.chatapp.fragment_home2.*
import org.json.JSONObject
import org.matrix.androidsdk.MXSession
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), HomeAdapter.iHomClick, View.OnClickListener {

    private lateinit var mSession: MXSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vp.adapter = VPAdapter(childFragmentManager)
        prev_page.setOnClickListener(this)
        next_page.setOnClickListener(this)
        mSession = Matrix.getInstance(activity).defaultSession

    }

    override fun onHomeClick(title: String?) {
        when (title) {
            C.Status -> {
                startActivity(Intent(requireActivity(), UserStatusActivity::class.java))

            }
            C.InviteFriends -> {
                (activity as ChatMainActivity).invite()

            }
            C.Settings -> {
                startActivity(Intent(activity, VectorSettingsActivity::class.java))

            }
            C.MyBalance -> {
                (activity as ChatMainActivity).GetBalance()

            }
            C.BuyCredit -> {
                val myIntent = Intent(context, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "Credit")
                startActivity(myIntent)
            }
            C.Meeting -> {
                val myIntent = Intent(context, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "meeting")
                startActivity(myIntent)
            }
            C.VoucherRecharge -> {
                (activity as ChatMainActivity).voucherTransfer()

            }
            C.MobileTopup -> {
                val myIntent = Intent(context, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "TopupA")
                startActivity(myIntent)
            }
            C.MobileTransfer -> {
                (activity as ChatMainActivity).voucherRegcharge()
            }
            C.TrnasferHistory -> {
                startActivity(Intent(requireActivity(), TransferHistoryAcitivty::class.java))

            }
            C.ContactBackup -> {
                showErr()

            }
            C.Tracking -> {
                C.showErr()
//                startActivity(Intent(activity, TrackingActivity::class.java))
            }
            C.Did -> {
                showErr()

            }
            C.UpdateProfile -> {
                startActivity(Intent(activity, VectorSettingsActivity::class.java))

            }
            C.Qr -> {
                startActivity(Intent(activity, QrActivity::class.java))

            }
            C.Ticketing -> {
                startActivity(Intent(activity, TicketingActivity::class.java))

            }
            C.Courier -> {
                startActivity(Intent(activity, CourierActivity::class.java))

            }
            C.WillEducation -> {
                val myIntent = Intent(context, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "Education")
                startActivity(myIntent)
            }
            C.Medical -> {
                showErr()

            }
            C.Law -> {
                startActivity(Intent(activity, LawActivity::class.java))

            }
            C.smartAgro -> {
                showErr()
            }
            C.SmartCityGuide -> {
                showErr()
            }
            C.WhyWill -> {
                try {
                    val myIntent = Intent(activity, ExtendedWebview::class.java)
                    myIntent.putExtra("Bundle", "Why")
                    startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "No application can handle this request. Please install a webbrowser", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }

            C.Logout -> {
                (activity as ChatMainActivity).logout()
            }
            C.MyNumber -> {
                try {
                    val myIntent = Intent(activity, ExtendedWebview::class.java)
                    myIntent.putExtra("Bundle", "MyNumber")
                    startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "No application can handle this request. Please install a webbrowser", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showErr() {
        Toast.makeText(requireActivity(), "Available in Paid Version", Toast.LENGTH_LONG).show()

    }

    override fun onResume() {
        super.onResume()
        GetBalance()
        (activity as ChatMainActivity).hideItem();
        mSession = Matrix.getInstance(requireActivity()).defaultSession
//        VectorUtils.loadUserAvatar(activity, mSession, settings_avatar, mSession.myUser)
        name.setText(mSession?.getMyUser()?.displayname)
        var tmp: Array<String>? = mSession?.myUserId?.split("@".toRegex())?.toTypedArray()
        tmp?.let {
            tmp->
            val tmp1 = tmp[1].split(":".toRegex()).toTypedArray()
            contact.text = tmp1[0]
        }
    }

    public class VPAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {
        var list: ArrayList<Fragment> = ArrayList()
        lateinit var title: ArrayList<String>

        init {

            list.add(Home_first())
            list.add(HomeSecond())
//            list.add(HomeFragmentThird())
        }

        override fun getItem(position: Int): Fragment {
            return list.get(position)
        }

        override fun getCount(): Int {
            return list.size
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.prev_page -> {
                if (vp.currentItem == 1) {
                    vp.setCurrentItem(0, true)
                    prev_page.visibility = View.GONE
                    next_page.visibility = View.VISIBLE
                } else if (vp.currentItem == 2) {
                    vp.setCurrentItem(1, true)
                    prev_page.visibility = View.VISIBLE
                    next_page.visibility = View.VISIBLE
                }
            }
            R.id.next_page -> {
                if (vp.currentItem == 0) {
                    vp.setCurrentItem(1, true)
                    prev_page.visibility = View.VISIBLE
                    next_page.visibility = View.GONE
                } else if (vp.currentItem == 1) {
                    vp.setCurrentItem(2, true)
                    prev_page.visibility = View.VISIBLE
                    next_page.visibility = View.INVISIBLE
                }

            }
        }
    }

    private fun GetBalance() {
        try {
            val settings = PreferenceManager.getDefaultSharedPreferences(activity)
            val cust_id = Settings.asHex(Settings.encrypt(settings.getString("Username", ""), Settings.ENC_KEY).toByteArray())
            val cust_pass = Settings.asHex(Settings.encrypt(settings.getString("Password", ""), Settings.ENC_KEY).toByteArray())
            val url = Settings.BALANCE_API
            val queue = Volley.newRequestQueue(activity)
            val sr: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
                var response = response
                try {
//                    balancePg.setVisibility(View.GONE)
                    response = response.trim { it <= ' ' }
                    val json = JSONObject(response)
                    if (!json.isNull("credit")) {
                        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        var UserCurrency = json.getString("currency").substring(0, 3)
                        format.currency = Currency.getInstance(UserCurrency)
                        val balanceVal = format.format(json.getDouble("credit"))
                        activity?.runOnUiThread(Runnable { balance.text = balanceVal })
                    } else {

                    }
                } catch (e: Exception) {

                }
            }, Response.ErrorListener { error ->

            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["cust_id"] = cust_id
                    params["cust_pass"] = cust_pass
                    return params
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/x-www-form-urlencoded"
                    return params
                }
            }
            queue.add(sr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}