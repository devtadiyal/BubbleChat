package com.chatapp.fragments

import android.app.Activity
import android.telecom.Call
import com.chatapp.C
import com.chatapp.C.Companion.BuyCredit
import com.chatapp.C.Companion.CallDetailsReport
import com.chatapp.C.Companion.CallRates
import com.chatapp.C.Companion.ContactBackup
import com.chatapp.C.Companion.Courier
import com.chatapp.C.Companion.DataBundle
import com.chatapp.C.Companion.Did
import com.chatapp.C.Companion.ELearning
import com.chatapp.C.Companion.Electric
import com.chatapp.C.Companion.Electricity
import com.chatapp.C.Companion.Flight_Booking
import com.chatapp.C.Companion.InviteFriends
import com.chatapp.C.Companion.Law
import com.chatapp.C.Companion.Logout
import com.chatapp.C.Companion.Medical
import com.chatapp.C.Companion.Meeting
import com.chatapp.C.Companion.MobileMoney
import com.chatapp.C.Companion.MobileTopup
import com.chatapp.C.Companion.MobileTransfer
import com.chatapp.C.Companion.MyBalance
import com.chatapp.C.Companion.MyNumber
import com.chatapp.C.Companion.Qr
import com.chatapp.C.Companion.Settings
import com.chatapp.C.Companion.SmartCityGuide
import com.chatapp.C.Companion.Status
import com.chatapp.C.Companion.TV
import com.chatapp.C.Companion.Ticketing
import com.chatapp.C.Companion.TransferCash
import com.chatapp.C.Companion.TrnasferHistory
import com.chatapp.C.Companion.UpdateProfile
import com.chatapp.C.Companion.VoucherRecharge
import com.chatapp.C.Companion.WhyUs
import com.chatapp.C.Companion.WhyWill
import com.chatapp.C.Companion.WillEducation
import com.chatapp.C.Companion.bookId
import com.chatapp.C.Companion.callerId
import com.chatapp.C.Companion.services
import com.chatapp.C.Companion.smartAgro
import im.vector.R
import im.vector.VectorApp
import java.util.*

class HomeModel {
    var name: String = ""
    var colorCode = ""
    var icon = 0


    companion object {

        val firstHomeList: ArrayList<HomeModel>
            get() {
                val list = ArrayList<HomeModel>()
                val iconList = arrayOf(
                    R.drawable.invite_friends,
                    R.drawable.mybalance,
                    R.drawable.buy_credit,
                    R.drawable.transfer_credit,
                    R.drawable.transfer_history,
                    R.drawable.voucher,
                    R.drawable.call_details_report,
                    R.drawable.call_rates,
                    R.drawable.mobiletopup
                )
                val titles = arrayOf(
                    InviteFriends,
                    MyBalance,
                    BuyCredit,
                    MobileTransfer,
                    TrnasferHistory,
                    VoucherRecharge,
                    CallDetailsReport,
                    CallRates,
                    MobileTopup
                )
                for (i in iconList.indices) {
                    val model = HomeModel()
                    model.name = titles[i]
                    model.icon = iconList[i]
                    list.add(model)
                }
                return list
            }

        val thirdHomeList: ArrayList<HomeModel>
            get() {
                val list = ArrayList<HomeModel>()
                val iconList = arrayOf(
                    R.drawable.e_learning,
                    R.drawable.book_ticket,
                    R.drawable.courier,
                    R.drawable.contact_backup,
                    R.drawable.tracking,
                    R.drawable.teletalk_tv,
                    R.drawable.teletalk_store,
                    R.drawable.setting
                )
                val titles = arrayOf(
                    ELearning,
                    C.Ticketing,
                    Courier,
                    ContactBackup,
                    C.Tracking,
                    C.TeletakTV,
                    C.TeletalkStore,
                    C.Settings
                )
                for (i in iconList.indices) {
                    val model = HomeModel()
                    model.name = titles[i]
                    model.icon = iconList[i]
                    list.add(model)
                }
                return list
            }


        fun getSecHomeList(): ArrayList<HomeModel> {
            val list = ArrayList<HomeModel>()
            val iconList = arrayOf(
                R.drawable.mobile_money,
                R.drawable.tv,
                R.drawable.databundel,
                R.drawable.electricity,
                R.drawable.did,
                R.drawable.flight,
                R.drawable.courier,
                R.drawable.why,
                R.drawable.logout

            )
            val titles = arrayOf(
                MobileMoney,
                TV,
                DataBundle,
                Electricity,
                Did,
                Flight_Booking,
                Courier,
                WhyUs,
                Logout
            )
            for (i in iconList.indices) {
                val model = HomeModel()
                model.name = titles[i]
                model.icon = iconList[i]
                list.add(model)
            }
            return list
        }
    }
}