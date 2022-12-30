package com.chatapp.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.chatapp.*
import com.chatapp.C.Companion.showErr
import com.chatapp.activity.CourierActivity
import com.chatapp.activity.LawActivity
import com.chatapp.activity.QrActivity
import com.chatapp.activity.TicketingActivity
import com.chatapp.adapters.HomeAdapter
import com.chatapp.fragments.HomeModel
import com.chatapp.share.UserStatusActivity
import im.vector.R
import im.vector.activity.VectorSettingsActivity
import kotlinx.android.synthetic.chatapp.fragment_home2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeSecond.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeSecond : Fragment(),HomeAdapter.iHomClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = GridLayoutManager(activity, 3)
        val adapter = HomeAdapter(context, HomeModel.getSecHomeList(), this)
        rv.adapter = adapter
    }



    override fun onHomeClick(title: String?) {
        val c= activity?.let { C(it) }
        if (title != null) {
            c?.onHomeItemClick(title)
        }
    }

}