package com.chatapp.fragments.recentfilter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.chatapp.ChatMainActivity;
import com.chatapp.adapters.RecentAdapter;
import com.chatapp.sip.api.ISipService;
import com.chatapp.util.ChatUtils;
import com.chatapp.util.RecentDBHandler;
import com.chatapp.util.RecentItem;

import org.matrix.androidsdk.MXSession;
import org.matrix.androidsdk.call.IMXCall;
import org.matrix.androidsdk.core.callback.ApiCallback;
import org.matrix.androidsdk.core.model.MatrixError;
import org.matrix.androidsdk.crypto.MXCryptoError;
import org.matrix.androidsdk.data.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.vector.Matrix;
import im.vector.R;
import im.vector.activity.VectorCallViewActivity;
import im.vector.activity.VectorMemberDetailsActivity;
import im.vector.util.PermissionsToolsKt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentCallWithFilter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentCallWithFilter extends ListFragment {

    private ListView listView1;

    private OnFragmentInteractionListener mListener;

    RecentAdapter m_adapter;
    ArrayList<RecentItem> Items;
    private MXSession mSession;
    private int type = 0;

    private String LOG_TAG = "RECENT FRAGEMENT";
    final int PERMISSIONS_REQUEST_CONTACTS = 100;

    // TODO: Rename and change types of parameters
    public static RecentCallWithFilter newInstance() {
        RecentCallWithFilter fragment = new RecentCallWithFilter();
        return fragment;
    }

    public static RecentCallWithFilter newInstance(Bundle b) {
        RecentCallWithFilter fragment = new RecentCallWithFilter();
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
            type = getArguments().getInt("type", 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the list fragment layout
        View view = inflater.inflate(R.layout.fragment_recent_call_with_filter, container, false);
        mSession = Matrix.getInstance(getContext()).getDefaultSession();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getListView().setDivider(null);
        listView1 = getListView();
        ArrayList<RecentItem> Items = new ArrayList<RecentItem>();
        if (m_adapter == null) {
            m_adapter = new RecentAdapter(getActivity(), R.layout.layout_recent, Items);
            new JSONParse().execute();
            m_adapter.SetMXSession(mSession);
        }
        setListAdapter(m_adapter);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        new JSONParse().execute();
//        try {
//            ((ChatMainActivity) getActivity()).setToolTitle("Recents");
//        } catch (Exception e) {
//        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        RecentItem recentItem = Items.get(position);
        if (recentItem.calltype == 2) {
            startCall(mSession, recentItem.phoneno, false);
        } else if (recentItem.calltype == 3) {
            startCall(mSession, recentItem.phoneno, true);
        } else if (recentItem.calltype == 1) {
            String PhoneNo = Items.get(position).phoneno;
            try {
                ChatMainActivity superActivity = ((ChatMainActivity) getActivity());
                ISipService service = superActivity.getConnectedService();
                service.makeCall(PhoneNo, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }


    private class JSONParse extends AsyncTask<String, String, ArrayList<RecentItem>> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<RecentItem> doInBackground(String... args) {

            ArrayList<RecentItem> recentItems = new ArrayList<RecentItem>();
            if (getActivity() != null) {
                RecentDBHandler recentDBHandler = new RecentDBHandler(getActivity());
                List<RecentItem> recentItemList = recentDBHandler.GetRecentCalls(1);
                for (int i = 0; i < recentItemList.size(); i++) {
                    RecentItem recentItem = recentItemList.get(i);
                    if (PermissionsToolsKt.checkPermissions(PermissionsToolsKt.PERMISSIONS_FOR_MEMBERS_SEARCH, getActivity(), PermissionsToolsKt.PERMISSION_REQUEST_CODE)) {
                        HashMap contact = getContactDisplayNameByNumber(recentItem.phoneno);
                        recentItem.name = contact.get("name").toString();
                        recentItem.id = contact.get("contactId").toString();
                    } else {
                        recentItem.id = "";
                    }
                    recentItems.add(recentItem);
                }
            }
            return recentItems;
        }

        @Override
        protected void onPostExecute(final ArrayList<RecentItem> recentItems) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Items = recentItems;
                    updateItemsForCallType();
                }
            });
        }
    }

    ArrayList<RecentItem> rItem = new ArrayList<>();

    private void updateItemsForCallType() {
        rItem = new ArrayList<>();
        for (RecentItem item : Items) {
            if (type == 0) {
                rItem.add(item);
            } else if (type == 1) {
                if (item.direction != 1 && item.direction != 3) {
                    rItem.add(item);
                }
            } else if (type == 2) {
                if (item.direction == 1) {
                    rItem.add(item);
                }
            } else if (type == 3) {
                if (item.direction == 3) {
                    rItem.add(item);
                }
            }
        }
        setItemsInAdapter();

    }

    private void setItemsInAdapter() {
        m_adapter = new RecentAdapter(getActivity(), R.layout.layout_recent, rItem);
        m_adapter.SetMXSession(mSession);
        listView1.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();
    }

    public HashMap getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;
        HashMap contact = new HashMap();
        contact.put("name", name);
        contact.put("contactId", "");
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                contact.remove("name");
                contact.put("name", name);
                contact.remove("contactId");
                contact.put("contactId", contactId);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return contact;
    }


    private void startCall(final MXSession mSession, final String RoomID, final boolean isVideo) {
        if (!mSession.isAlive()) {
            Log.e(LOG_TAG, "startCall : the session is not anymore valid");
            return;
        }
        Room mRoom = mSession.getDataHandler().getRoom(RoomID, false);
        // create the call object
        mSession.mCallsManager.createCallInRoom(mRoom.getRoomId(), false, new ApiCallback<IMXCall>() {
            @Override
            public void onSuccess(final IMXCall call) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call.setIsVideo(isVideo);
                        final Intent intent = new Intent(getActivity(), VectorCallViewActivity.class);

                        intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, mSession.getCredentials().userId);
                        intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, call.getCallId());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        });
                        //TODO
                        /*
                        final Intent intent = new Intent(getActivity(), VectorCallViewActivity.class);

                        intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, mSession.getCredentials().userId);
                        intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, call.getCallId());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().startActivity(intent);
                            }
                        });

                         */
                    }
                });
            }

            @Override
            public void onNetworkError(Exception e) {
                ChatUtils.displayToast(getActivity(), e.getLocalizedMessage());
                Log.e(LOG_TAG, "## startCall() failed " + e.getMessage());
            }

            @Override
            public void onMatrixError(MatrixError e) {
                if (e instanceof MXCryptoError) {
                    MXCryptoError cryptoError = (MXCryptoError) e;
/*
                    if (MXCryptoError.UNKNOWN_DEVICES_CODE.equals(cryptoError.errcode)) {
                        CommonActivityUtils.displayUnknownDevicesDialog(mSession, getActivity(), (MXUsersDevicesMap<MXDeviceInfo>) cryptoError.mExceptionData, new VectorUnknownDevicesFragment.IUnknownDevicesSendAnywayListener() {
                            @Override
                            public void onSendAnyway() {
                                startCall(mSession,RoomID,isVideo);
                            }
                        });

                        return;
                    }
                    */
                    startCall(mSession, RoomID, isVideo);
                }

                ChatUtils.displayToast(getActivity(), e.getLocalizedMessage());
                Log.e(LOG_TAG, "## startCall() failed " + e.getMessage());
            }

            @Override
            public void onUnexpectedError(Exception e) {
                ChatUtils.displayToast(getActivity(), e.getLocalizedMessage());
                Log.e(LOG_TAG, "## startCall() failed " + e.getMessage());
            }
        });
    }


}