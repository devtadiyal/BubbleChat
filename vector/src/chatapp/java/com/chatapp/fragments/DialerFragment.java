package com.chatapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chatapp.ChatMainActivity;
import com.chatapp.PrefixEditText;
import com.chatapp.Settings;
import com.chatapp.sip.api.ISipService;
import com.chatapp.sip.utils.AccountListUtils;
import com.chatapp.util.RecentDBHandler;
import com.chatapp.util.Utils;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import im.vector.R;

import static com.chatapp.Settings.asHex;
import static com.chatapp.Settings.encrypt;

public class DialerFragment extends Fragment implements View.OnClickListener {

    EditText txtDialNumber;
    private static final int PERMISSIONS_REQUEST_MICROPHONE = 200;
    String ExPhone;
    public static TextView txtStatus;
    ToneGenerator dtmfGenerator = new ToneGenerator(0, ToneGenerator.MAX_VOLUME);

    AccountListUtils.AccountStatusDisplay accountStatusDisplay;
    private TextView balance;
    private String UserCurrency;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    TextView rate;

    View vDialerFragment;

    public DialerFragment() {
        // Required empty public constructor
    }

    public static DialerFragment newInstance() {
        DialerFragment fragment = new DialerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vDialerFragment = inflater.inflate(R.layout.fragment_dialer, container, false);
        balance = (TextView) vDialerFragment.findViewById(R.id.balance);

        rate = vDialerFragment.findViewById(R.id.rate);
        ImageButton btn1 = (ImageButton) vDialerFragment.findViewById(R.id.one);
        ImageButton btn2 = (ImageButton) vDialerFragment.findViewById(R.id.two);
        ImageButton btn3 = (ImageButton) vDialerFragment.findViewById(R.id.three);
        ImageButton btn4 = (ImageButton) vDialerFragment.findViewById(R.id.four);
        ImageButton btn5 = (ImageButton) vDialerFragment.findViewById(R.id.five);
        ImageButton btn6 = (ImageButton) vDialerFragment.findViewById(R.id.six);
        ImageButton btn7 = (ImageButton) vDialerFragment.findViewById(R.id.seven);
        ImageButton btn8 = (ImageButton) vDialerFragment.findViewById(R.id.eight);
        ImageButton btn9 = (ImageButton) vDialerFragment.findViewById(R.id.nine);
        ImageButton btn0 = (ImageButton) vDialerFragment.findViewById(R.id.zero);
        ImageButton btnstar = (ImageButton) vDialerFragment.findViewById(R.id.star);
        ImageButton btnpound = (ImageButton) vDialerFragment.findViewById(R.id.pound);
        ImageView btnCall = (ImageView) vDialerFragment.findViewById(R.id.btn_dialpad_call);
        ImageView btnDelete = (ImageView) vDialerFragment.findViewById(R.id.btn_dialpad_delete);
        ImageView btnContact = (ImageView) vDialerFragment.findViewById(R.id.btn_dialpad_contact);

        txtStatus = (TextView) vDialerFragment.findViewById(R.id.txtRegStatus);
        txtStatus.setText("");

        txtDialNumber = vDialerFragment.findViewById(R.id.dialdigits);
        txtDialNumber.setText("");
/*
        txtDialNumber.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    return true;
                }
                return false;
            }

        });
*/
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnstar.setOnClickListener(this);
        btnpound.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnContact.setOnClickListener(this);

        btnDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                txtDialNumber.setText("");
                return true;
            }
        });

        btn0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "+");
                return true;
            }
        });
        GetBalance();


        txtDialNumber.addTextChangedListener(new TextWatcher() {
                                                 @Override
                                                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                 }

                                                 @Override
                                                 public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                 }

                                                 @Override
                                                 public void afterTextChanged(Editable s) {
                                                     if (s.toString().length() >= 3) {
                                                         if (s.toString().substring(0, 2).equalsIgnoreCase("00")) {
                                                             if (s.toString().length() >= 5)
                                                                 getRate(s.toString());
                                                         } else
                                                             getRate(s.toString());
                                                     } else {
                                                         rate.setText("");
                                                     }
                                                 }
                                             }
        );
        return vDialerFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String countryZipCode = Utils.getCountryZipCode(getActivity());
//        txtDialNumber.setText(countryZipCode);
//        txtDialNumber.setSelection(countryZipCode.length());
//        Selection.setSelection(txtDialNumber.getText(), txtDialNumber.getText().length());
        txtDialNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if(!s.toString().startsWith(countryZipCode)){
//                    txtDialNumber.setText(countryZipCode);
//                    Selection.setSelection(txtDialNumber.getText(), txtDialNumber.getText().length());
//                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = getActivity().managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactName.setText(name);
                txtDialNumber.setText(number);
                //contactEmail.setText(email);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);

        //updateStatus(MainActivity.RegStatus);
        if (ExPhone != null) {
            if (!ExPhone.equals("")) {
                txtDialNumber.setText(ExPhone);
                txtDialNumber.setSelection(txtDialNumber.getText().length());
                ExPhone = "";
            }
        }
        try {
            ((ChatMainActivity) getActivity()).hideItem();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        timer.cancel();
        timer = null;
        super.onPause();
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            accountStatusDisplay = AccountListUtils.getAccountDisplay(getContext(), 1);
                            updateStatus(accountStatusDisplay.statusLabel);
                            GetBalance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zero:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "0");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_0, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.one:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "1");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_1, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.two:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "2");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_2, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.three:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "3");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_3, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.four:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "4");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_4, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.five:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "5");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_5, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.six:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "6");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_6, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.seven:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "7");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_7, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.eight:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "8");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_8, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.nine:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "9");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_9, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.star:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "*");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_S, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.pound:
                if (txtDialNumber.getText().length() < 16)
                    txtDialNumber.getText().insert(txtDialNumber.getSelectionStart(), "#");
                dtmfGenerator.startTone(ToneGenerator.TONE_DTMF_P, 1000);
                dtmfGenerator.stopTone();
                break;
            case R.id.btn_dialpad_delete:
                if (txtDialNumber.getText().toString().length() > 0) {
                    int selectionStart = txtDialNumber.getSelectionStart();
                    if (selectionStart > 0) {
                        String s = txtDialNumber.getText().toString();
                        String beforeCursor = s.substring(0, selectionStart - 1);
                        String afterCursor = s.substring(selectionStart, s.length());
                        txtDialNumber.setText(beforeCursor + afterCursor);
                        txtDialNumber.setSelection(selectionStart - 1);
                    }
/*

                    txtDialNumber.setText(txtDialNumber.getText().toString().substring(0, txtDialNumber.getText().toString().length() - 1));
                    txtDialNumber.setSelection(txtDialNumber.getText().length());
*/
                }
                break;
            case R.id.btn_dialpad_contact:

                if (txtDialNumber.getText().toString().length() > 0) {
                    Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri.parse("tel:" + txtDialNumber.getText()));
                    intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
                    startActivity(intent);
                }
                break;

            case R.id.btn_dialpad_call:
                String PhoneNo = txtDialNumber.getText().toString();
                if (PhoneNo.length() == 0) {
                    RecentDBHandler recentDBHandler = new RecentDBHandler(getContext());
                    txtDialNumber.setText(recentDBHandler.LastCalledNo());
                } else if (PhoneNo.length() > 7) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_MICROPHONE);
                    } else {
                        OnCallButtonClick();
                    }

                } else {
                    Toast.makeText(getContext(), "Please check the phone number",
                            Toast.LENGTH_LONG).show();
                }


                break;

        }
    }

    private void OnCallButtonClick() {

        if (accountStatusDisplay.availableForCalls) {
            final String PhoneNo = txtDialNumber.getText().toString().replace("+", "");
            if (PhoneNo.length() > 7) {

//                Intent i = new Intent(getContext(), InCallActivity.class);
//                i.putExtra("CallType", "Outbound");
//                i.putExtra("PhoneNo", PhoneNo);
//                startActivity(i);

                try {
                    ChatMainActivity superActivity = ((ChatMainActivity) getActivity());
                    ISipService service = superActivity.getConnectedService();
                    service.makeCall(PhoneNo, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                txtDialNumber.setText("");
            } else {
                Toast.makeText(getContext(), "Please check the phone number",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "App Not registered. Check your internet connection and restart the app.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateStatus(final String status) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                public void run() {

                    try {
                        if (status.equals(getResources().getString(R.string.acct_registered))) {
                            txtStatus.setText(status + " (" + ChatMainActivity.Username + ")");
                            txtStatus.setTextColor(getResources().getColor(R.color.button_color));
                        } else {
                            txtStatus.setText(status);
                            txtStatus.setTextColor(getResources().getColor(R.color.button_color));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private void GetBalance() {

        try {
            SharedPreferences settings = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());

            final String cust_id = asHex(encrypt(settings.getString("Username", ""), Settings.ENC_KEY).getBytes());
            final String cust_pass = asHex(encrypt(settings.getString("Password", ""), Settings.ENC_KEY).getBytes());


            String url = Settings.BALANCE_API;

            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        response = response.trim();
                        JSONObject json = new JSONObject(response);
                        if (!json.isNull("credit")) {
                            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
                            UserCurrency = json.getString("currency").substring(0, 3);
                            format.setCurrency(Currency.getInstance(UserCurrency));
                            final Double d = json.getDouble("credit");
//                            DecimalFormat df2 = new DecimalFormat("#.##");
//                            String s = df2.format(d);
                            String s = String.valueOf(d);

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        balance.setText(String.format("$%s", s));
                                    }
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Toast.makeText(getActivity(), "An error, please try again later.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(getActivity(), "An Internal error, please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    final VolleyError error1 = error;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), error1.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("cust_id", cust_id);
                    params.put("cust_pass", cust_pass);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }

            };
            queue.add(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getRate(String phoneNo) {
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String userName = settings.getString("Username", "");

            final String rates = asHex(Settings.encrypt(phoneNo, Settings.ENC_KEY).getBytes());
            final String name = asHex(Settings.encrypt(userName, Settings.ENC_KEY).getBytes());

            String url = Settings.RATES_API;
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject json = new JSONObject(response);
                        String success = json.getString("result");
                        if (success.equals("success")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String rates = json.optString("rates");
                                    String country = json.optString("country");
                                    rate.setText(String.format("Rate :$%s", rates));
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rate.setText("");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    final VolleyError error1 = error;
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("rates", rates);
                    params.put("username", name);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }

            };
            queue.add(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}




