package com.chatapp.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chatapp.Settings;
import com.chatapp.adapters.RecentUpdateAdapter;
import com.chatapp.network.VolleyApi;
import com.chatapp.network.VolleyListener;
import com.chatapp.pixly.pix.Options;
import com.chatapp.pixly.pix.Pix;
import com.chatapp.sip.utils.Log;
import com.chatapp.util.LocalContactItem;
import com.chatapp.util.LocalContactsHandler;
import com.chatapp.util.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import im.vector.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment implements View.OnClickListener, VolleyListener, RecentUpdateAdapter.IShareAdapterClickListner {

    private Activity context;
    private RecyclerView rv;
    private ConstraintLayout statusContainer;
    private RecentUpdateAdapter adapter;
    public static List<LocalContactItem> localContactItemList;
    private VolleyApi volleyApi;
    private String CONTACT_STATUS = "CONTACT STATUS";
    SharedPreferences settings;
    LinearLayout pg;
    String userName = "";
    FloatingActionButton camera;


    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        volleyApi = new VolleyApi(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        localContactItemList = new LocalContactsHandler(getContext()).GetLocalContacts();
        settings = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        userName = settings.getString("Username", "");

        camera = view.findViewById(R.id.stat_camera);
        rv = view.findViewById(R.id.rvList);
        pg = view.findViewById(R.id.pg);
        statusContainer = view.findViewById(R.id.statusContainer);
        rv.setLayoutManager(new LinearLayoutManager(context));
        statusContainer.setOnClickListener(this);
        getOtherContacts();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStatusClick();
            }
        });
    }

    private void getOtherContacts() {
        try {
            JSONObject object = new JSONObject();
            ArrayList<String> list = new ArrayList<>();
            for (LocalContactItem item : localContactItemList) {
                list.add(item.Phone);
            }
            object.put("username", userName);
            String otherContacts = userName;
            for (LocalContactItem item : localContactItemList) {
                otherContacts = otherContacts + "," + item.Phone;
            }
            object.put("phonenos", otherContacts);
            Log.d("contacts", otherContacts.toString());
            showPg();
            String url = "";

            String finalOtherContacts = otherContacts;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    hidePg();
                    try {
                        handleResponse(new JSONObject(s));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    hidePg();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", userName);
                    params.put("phonenos", finalOtherContacts);
                    return params;
                }

            };
            new VolleyApi(context).getRequestQueue().add(stringRequest);
        } catch (JSONException e) {
            hidePg();
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = new ArrayList<>();
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    String p = returnValue.get(0);
                    String mimeType = URLConnection.guessContentTypeFromName(p);
                    boolean isVideo = mimeType != null && mimeType.startsWith("video");
                    if (isVideo) {
                        compressedVideo(returnValue.get(0));
                    } else {
                        uploadFiles(returnValue);
                    }
                }
            }
            break;
        }
    }
    private ProgressDialog dialog;

    private void compressedVideo(String path) {
        File desFile = new File(getActivity().getExternalFilesDir(null) + "/" + File.separator + "test.mp4");
        try {
            desFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f = getActivity().getExternalFilesDir("wills");
        VideoCompressor.start(path, desFile.getPath(), new CompressionListener() {
            @Override
            public void onStart() {
                // Compression start
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Preparing...");

            }

            @Override
            public void onSuccess() {
                // On Compression success
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                ArrayList<String> list = new ArrayList<>();
                list.add(desFile.getPath());
                uploadFiles(list);
            }

            @Override
            public void onFailure(String failureMessage) {
                // On Failure
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onProgress(float v) {
                // Update UI with progress value
                runOnUiThread(new Runnable() {
                    public void run() {

                        dialog.show();


//                        progress.setText(progressPercent + "%");
//                        progressBar.setProgress((int) progressPercent);
                    }
                });
            }

            @Override
            public void onCancelled() {
                // On Cancelled
            }
        }, VideoQuality.MEDIUM, false, false);
    }

    private void uploadFiles(ArrayList<String> returnValue) {
        if (NetworkUtils.isConnected()) {
            showPg();
            File file = new File(returnValue.get(0));
            StatusApi getResponse = StatusApiClient.getRetrofit().create(StatusApi.class);
            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody userNameValue = RequestBody.create(okhttp3.MultipartBody.FORM, userName);

            Call<FileUploadResponse> call = getResponse.upload1(userNameValue, fileToUpload);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    hidePg();
                    FileUploadResponse serverResponse = (FileUploadResponse) response.body();

                    if (serverResponse != null) {
                        if (serverResponse.getResult().equalsIgnoreCase("success")) {
                            Toast.makeText(requireActivity(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            getOtherContacts();
                        } else {
                            Toast.makeText(requireActivity(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        assert serverResponse != null;
                        Log.v("Response", serverResponse.toString());
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    hidePg();
                    Log.d("response", t.toString());

                }
            });
        } else {
            Toast.makeText(requireActivity(), "Check Internet connection", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.statusContainer) {
            Pix.start(this, Options.init().
                    setCount(3).
                    setExcludeVideos(true).
                    setRequestCode(100));
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject, String tag) {
        try {
            HashMap<String, ArrayList<String>> map = handleResponse(jsonObject);
            Log.d("res", map.toString());
        } catch (Exception e) {
            hidePg();
        }
    }

    @NotNull
    private HashMap<String, ArrayList<String>> handleResponse(JSONObject jsonObject) throws JSONException {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        hidePg();
        if (jsonObject != null) {
            if (jsonObject.optString("result").
                    equalsIgnoreCase("success")) {
                JSONArray array = jsonObject.optJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    ArrayList<String> currentItems = new ArrayList<String>();
                    JSONObject o = array.getJSONObject(i);
                    String userId = o.getString("Username: ");
                    String image = o.getString("Image URL: ");
                    if (map.get(userId) != null) {
                        currentItems = map.get(userId);
                    }
                    currentItems.add(image);
                    map.put(userId, currentItems);
                }
            }
        }
        setListData(map);
        return map;
    }

    private void setListData(HashMap<String, ArrayList<String>> map) {
        ArrayList<RecentModel> list = new ArrayList<>();
        userImages.clear();
        userRecentModel.userName = userName;
        userRecentModel.imageList.clear();
        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            if (entry.getKey().equalsIgnoreCase(userName)) {
                userImages.addAll(entry.getValue());
                userRecentModel.imageList.addAll(userImages);
            } else {
                RecentModel model = new RecentModel();
                model.userName = entry.getKey();
                model.fullName = getFullName(model.userName);
                model.imageList.addAll(entry.getValue());
                list.add(model);
            }
        }
        list.add(0, userRecentModel);
        adapter = new RecentUpdateAdapter(list, context);
        adapter.setListner(this);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private String getFullName(String userName) {
        String s = "";
        for (LocalContactItem item : localContactItemList) {
            if (userName.equalsIgnoreCase(item.Phone.replace(" ", ""))) {
                return GetContactsName(item.ContactID);
            }
        }
        return s;
    }

    ArrayList<String> userImages = new ArrayList<String>();
    RecentModel userRecentModel = new RecentModel();

    @Override
    public void onError(VolleyError error, String tag) {
        try {
            hidePg();
            Log.d("err", tag);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusClick() {
        Pix.start(this, Options.init().
                setCount(1).
                setExcludeVideos(false).
                setVideoDurationLimitinSeconds(10).
                setRequestCode(100));
    }

    private void showPg() {
        pg.setVisibility(View.VISIBLE);
    }

    private void hidePg() {
        pg.setVisibility(View.GONE);
    }

    private String GetContactsName(String ContactID) {
        String displayName = "";
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, Uri.encode(ContactID));
        Cursor c = getActivity().getContentResolver().query(lookupUri, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
        try {
            c.moveToFirst();
            displayName = c.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return displayName;
    }
}
