package com.chatapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Toast
import com.chatapp.activity.*
import com.chatapp.share.UserStatusActivity
import im.vector.R
import im.vector.VectorApp
import im.vector.activity.VectorSettingsActivity

class C {
    var c: Context? = null

    constructor (context: Activity) {
        c = context
    }

    companion object {
        var Status = "Status"
        var InviteFriends = "InviteFriends"
        var Settings = "Settings"
        var MyBalance = "MyBalance"
        var BuyCredit = "Credit"
        var VoucherRecharge = "VoucherRecharge"
        var MobileTopup = "MobileTopup"
        var MobileMoney = "MobileMoney"
        var MobileTransfer = "MobileTransfer"
        var TrnasferHistory = "TrnasferHistory"
        var ContactBackup = "ContactBackup"
        var Tracking = "Tracking"
        var Did = "Did"
        var Flight_Booking = "Flight Booking"
        var UpdateProfile = "UpdateProfile"
        var Qr = "Qr"
        var Ticketing = "Ticketing"
        var Courier = "Courier"
        var WillEducation = "Will Education"
        var Medical = "Medical"
        var Law = "Law"
        var bookId = "Book Id"
        var callerId = "Caller Id"
        var smartAgro = "smartAgro"
        var SmartCityGuide = "SmartCityGuide"
        var WhyWill = "Why Us"
        var WhyUs = "Why Us"
        var Logout = "Logout"
        var Meeting = "Meeting"
        var MyNumber = "My Number"
        var DataBundle = "Data Bundle"
        var Electric = "Electric Bill"
        var TV = "Television Bill"
        var MONEYTRANSFER = "Money Transfer"
        var SMS = "Send Sms"
        var TransferCash = "Transfer Cash"
        var services = "Services"

        var AddMoneyToWallet = "Add Money To Wallet"
        var TransferCredit = "Transfer Credit"
        var MyWallet = "My Wallet"
        var WalletBalance = "Wallet Balance"
        var AddFundsToWallet = "Add Funds to wallet"
        var TransferMobileMoney = "Transfer Mobile Money"
        var LocalMobileTopup = "Local Mobile Topup"
        var InterMobileTopup = "International Mobile Topup"
        var Electricity = "Electricity"
        var LocalElectricity = "LocalElectricity Bill Pay"
        var InterElectricity = "International Electricity Bill Pay"
        var LocalDataBundle = "LocalElectricity Data Bundle"
        var InterDataBundle = "International Data Bundle"
        var TvRecharge = "TV Recharge"
        var LocalTv = "LocalElectricity TV Recharge"
        var InterTv = "International TV Recharge"
        var SendSMS = "Send SMS"
        var CallDetailsReport = "Call Details Report"
        var CallRates = "Call Rates"
        var ELearning = "E-Learning"
        var TeletakTV = "Kakaki Express Tv"
        var TeletalkStore = "Kakaki Express Store"


        public fun showErr() {
//            Toast.makeText(VectorApp.getInstance(), "Available in Paid Version", Toast.LENGTH_LONG)
//                .show()
        }

        fun getUserName(): String {
            var userName = ""
            var settings = PreferenceManager.getDefaultSharedPreferences(VectorApp.getInstance())
            userName = settings?.getString("Username", "").toString()
            return userName
        }


    }

    fun status() {
        c?.startActivity(Intent(c, UserStatusActivity::class.java))
    }

    fun invite() {
        (c as ChatMainActivity).invite()

    }

    fun setting() {
        c?.startActivity(Intent(c, VectorSettingsActivity::class.java))
    }

    fun myBalance() {
        (c as ChatMainActivity).GetBalance()
    }

    fun buyCredit() {
        val myIntent = Intent(c, ExtendedWebview::class.java)
        myIntent.putExtra("Bundle", C.AddMoneyToWallet)
        c?.startActivity(myIntent)
    }

    fun myNumber() {
        try {
            val myIntent = Intent(c, ExtendedWebview::class.java)
            myIntent.putExtra("Bundle", "MyNumber")
            c?.startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                c,
                "No application can handle this request. Please install a webbrowser",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    fun logout() {
        (c as ChatMainActivity).logout()
    }


    fun onHomeItemClick(title: String) {
        when (title) {
            Status -> {
                status()
            }
            InviteFriends -> {
                invite()
            }
            Settings -> {
                setting()
            }
            MyBalance -> {
                myBalance()
            }
            BuyCredit -> {
                buyCredit()
            }
            Meeting -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "meeting")
                c?.startActivity(myIntent)
            }
            VoucherRecharge -> {
                (c as ChatMainActivity).voucherTransfer()
            }
            MobileTopup -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", MobileTopup)
                c?.startActivity(myIntent)
            }
            MobileMoney -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", MobileMoney)
                c?.startActivity(myIntent)
            }
            MobileTransfer -> {
                (c as ChatMainActivity).voucherRegcharge()
            }
            TrnasferHistory -> {
                c?.startActivity(Intent(c, TransferHistoryAcitivty::class.java))

            }
            ContactBackup -> {
                showErr()

            }
            Tracking -> {
                showErr()
            }
            Did -> {
                showErr()

            }
            UpdateProfile -> {
                c?.startActivity(Intent(c, VectorSettingsActivity::class.java))

            }
            Qr -> {
                c?.startActivity(Intent(c, QrActivity::class.java))

            }
            Ticketing -> {
                c?.startActivity(Intent(c, TicketingActivity::class.java))

            }
            Courier -> {
                c?.startActivity(Intent(c, CourierActivity::class.java))

            }
            WillEducation -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", "Education")
                c?.startActivity(myIntent)
            }
            Medical -> {
                showErr()

            }
            Law -> {
                c?.startActivity(Intent(c, LawActivity::class.java))

            }
            smartAgro -> {
                showErr()
            }
            SmartCityGuide -> {
                showErr()
            }
            WhyWill -> {
                try {
                    val myIntent = Intent(c, ExtendedWebview::class.java)
                    myIntent.putExtra("Bundle", "Why")
                    c?.startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        c,
                        "No application can handle this request. Please install a webbrowser",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
            services -> {
                try {
                    val myIntent = Intent(c, SettingsWebActivity::class.java)
                    c?.startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        c,
                        "No application can handle this request. Please install a webbrowser",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }

            Logout -> {
                logout()
            }
            MyNumber -> {
                myNumber()
            }
            bookId -> {
                c?.startActivity(Intent(c, TicketingActivity::class.java))
            }
            callerId -> {
                showErr()
            }
            TransferCash -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", C.TransferCash)
                c?.startActivity(myIntent)
            }
            DataBundle -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", DataBundle)
                c?.startActivity(myIntent)
            }
            Electric -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", Electric)
                c?.startActivity(myIntent)
            }
            TV -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", TV)
                c?.startActivity(myIntent)
            }
            TeletakTV -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", C.TeletakTV)
                c?.startActivity(myIntent)
            }
            TeletalkStore -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", C.TeletalkStore)
                c?.startActivity(myIntent)
            }
            bookId -> {
                showErr()
            }
            callerId -> {
                showErr()
            }
            CallRates -> {
                c?.startActivity(Intent(c, CallRatesActivity::class.java))
            }
            CallDetailsReport -> {
                c?.startActivity(Intent(c, CallDetailsHistoryActivity::class.java))

            }
            Electric,
            Electricity -> {
                val myIntent = Intent(c, ExtendedWebview::class.java)
                myIntent.putExtra("Bundle", Electricity)
                c?.startActivity(myIntent)
            }
            else -> {
                showErr()
            }

        }
    }
}