package com.gprs.uttarpradesh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class hospital extends AppCompatActivity {

    private WebView wview;
    private ProgressDialog progressDialog;
    TextView hos,test,ord;
    String urlgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        wview= findViewById(R.id.webv);

        hos=findViewById(R.id.hos);
        test=findViewById(R.id.test);
        ord=findViewById(R.id.ord);
        progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Please Wait !"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

        progressDialog.setCancelable(true);

        WebSettings wsetting=wview.getSettings();
        wsetting.setJavaScriptEnabled(true);
        wsetting.setAllowContentAccess(false);
        wsetting.setSupportZoom(true);

        hos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlgo="http://www.aarogyasetumitr.in/";
                load();
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlgo="http://www.aarogyasetumitr.in/#labTest";

                load();
            }
        });

        ord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlgo="http://www.aarogyasetumitr.in/#onlnMed";

                load();
            }
        });

        wview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show(); // Display Progress Dialog

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show(); // Display Progress Dialog
                view.loadUrl(url);

                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                progressDialog.hide();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                progressDialog.hide();
                handler.proceed(); // Ignore SSL certificate errors
            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                progressDialog.hide();
                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.loadUrl("about:blank");
                AlertDialog alertDialog = new AlertDialog.Builder(hospital.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Server not Available ! \nCheck your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDialog.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }




        });





    }


    void load(){
        progressDialog.show();
        ord.setVisibility(View.INVISIBLE);
        hos.setVisibility(View.INVISIBLE);
        test.setVisibility(View.INVISIBLE);
        wview.loadUrl(urlgo);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
