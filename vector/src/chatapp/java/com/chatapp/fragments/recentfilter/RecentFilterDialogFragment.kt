package com.chatapp.fragments.recentfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chatapp.fragments.RecentFragment
import im.vector.R
import kotlinx.android.synthetic.chatapp.fragment_recent_filter_dialog.*
import kotlinx.android.synthetic.chatapp.fragment_recent_filter_dialog.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [RecentFilterDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RecentFilterDialogFragment : DialogFragment() {
    val list: ArrayList<RecentFragment> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_filter_dialog, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(b: Bundle): RecentFilterDialogFragment {
            val f = RecentFilterDialogFragment()
            f.arguments = b
            return f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RecentTabAdapter(childFragmentManager)
        pager.adapter = adapter
        tab_layout.setupWithViewPager(pager)

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}

class RecentTabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence {
        val b = Bundle()
        when (position) {
            0 -> {
                return "All"
            }
            1 -> {
                return "Incoming"
            }
            2 -> {
                return "Out"
            }
            3 -> {
                return "Missed"
            }
        }
        return "All"
    }

    override fun getItem(position: Int): Fragment {
        val b = Bundle()
        when (position) {
            0 -> {
                b.putInt("type",0)
                return RecentCallWithFilter.newInstance(b)
            }
            1 -> {
                b.putInt("type",1)
                return RecentCallWithFilter.newInstance(b)
            }
            2 -> {
                b.putInt("type",2)
                return RecentCallWithFilter.newInstance(b)
            }
            3 -> {
                b.putInt("type",3)
                return RecentCallWithFilter.newInstance(b)
            }
        }
        return RecentCallWithFilter.newInstance(b)
    }
}