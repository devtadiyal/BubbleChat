package com.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chatapp.sip.utils.Log;

import im.vector.BuildConfig;
import im.vector.R;

public class ExtendedWebview extends AppCompatActivity implements AdvanceWebView.Listener {

    private String url = "";

    private AdvanceWebView webView;
    SharedPreferences settings;


    Activity activity;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
        }
        setUrl();
        activity = this;
        progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
        if (BuildConfig.DEBUG)
            progDailog.setCancelable(true);
        else
            progDailog.setCancelable(false);
        webView = findViewById(R.id.webView);
        webView.setListener(this, this);
        webView.setGeolocationEnabled(false);
        webView.setMixedContentAllowed(true);
        webView.setCookiesEnabled(true);
        webView.setDesktopMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setThirdPartyCookiesEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        });
        webView.addHttpHeader("X-Requested-With", "");
        Log.d("url", url);
        webView.loadUrl(url);
    }

    private void setUrl() {
        Bundle b = getIntent().getExtras();
        url = b.getString("Bundle");
        String baseUrl = "https://billing.kakakicall.com/billing/customer/";
        if (url.equalsIgnoreCase("Why")) {
            url = "https://billing.kakakicall.com/privacy-policy.html";
            setTitle("Why Kakaki Express ?");
        } else if (url.equalsIgnoreCase("Wallet")) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_mobile_money.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Mobile Wallet");
        } else if (url.equalsIgnoreCase(C.Companion.getAddMoneyToWallet())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "mobile_payment.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Buy Credit ");
        } else if (url.equalsIgnoreCase(C.Companion.getMobileTopup())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_airtime.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Mobile Topup");
        } else if (url.equalsIgnoreCase("TopupB")) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_mobile_topup_app.php?pr_login=" + userName + "&pr_password=" + password + "&mobile_done=submit_log";
            setTitle("Topup");
        } else if (url.equalsIgnoreCase(C.Companion.getDataBundle())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_databundles.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Data Bundle Topup");
        } else if (url.equalsIgnoreCase(C.Companion.getElectric())||url.equalsIgnoreCase(C.Companion.getElectricity())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_electricity_bill.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Electricity Bill's Payment");
        } else if (url.equalsIgnoreCase(C.Companion.getTV())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_dth.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Television Bill's Payment");
        } else if (url.equalsIgnoreCase("payment")) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "mobile_payment.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Mobile Payment");
        } else if (url.equalsIgnoreCase(C.Companion.getTransferCash())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = "https://billing.kakakicall.com/billing/kakaki_api/billing_balance_transfer_balance/balance_transfer.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle("Transfer Credit");
        }else if (url.equalsIgnoreCase(C.Companion.getTeletakTV())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_dth.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle(C.Companion.getTeletakTV());
        }else if (url.equalsIgnoreCase(C.Companion.getTeletalkStore())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
//            url = baseUrl + "billing_mobile_money.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            url = "https://store.class5voipsoftswitch.com/";
            setTitle(C.Companion.getTeletalkStore());
        }else if (url.equalsIgnoreCase(C.Companion.getMobileMoney())) {
            String userName = settings.getString("Username", "");
            String password = settings.getString("Password", "");
            url = baseUrl + "billing_mobile_money.php?pr_login=" + userName + "&pr_password=" + password + "&mobiledone=submit_log";
            setTitle(C.Companion.getMobileMoney());
        }
    }

    public void setTitle(String title) {
        try {
            Log.d("url", url);
            System.out.println("url "+url.toString() );
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            Log.d("error", url);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progDailog.show();

    }

    @Override
    public void onPageFinished(String url) {
        progDailog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
