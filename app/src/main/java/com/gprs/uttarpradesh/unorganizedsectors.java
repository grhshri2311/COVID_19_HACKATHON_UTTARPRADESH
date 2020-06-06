package com.gprs.uttarpradesh;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gprs.uttarpradesh.Model.Articles;
import com.gprs.uttarpradesh.Model.Headlines;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class unorganizedsectors extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Button btnAboutUs;
    Dialog dialog;
    final String API_KEY = "81e919346ed94b8491dc88809d40d4eb";
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unorganizedsectors);


        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);


        btnAboutUs = findViewById(R.id.aboutUs);
        dialog = new Dialog(unorganizedsectors.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country = getCountry();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("",country,API_KEY);
            }
        });
        retrieveJson("unorganised sector workers haryana india",country,API_KEY);

        new DownloadImageTask((ImageView) findViewById(R.id.image1)).execute("https://imgk.timesnownews.com/media/36aa0164-beca-4ed2-be7e-2cb4fb889314.jpg");
        new DownloadImageTask((ImageView) findViewById(R.id.train)).execute("https://i.pinimg.com/originals/6a/72/4b/6a724b9501764fd83a4abcd37b58144d.png");

        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://uplabour.gov.in/"));
                    startActivity(i);
                }
                catch (ActivityNotFoundException e){
                    Toast.makeText(unorganizedsectors.this,"You don't have browser installed",Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.viewtrain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(unorganizedsectors.this, pdfViewer.class);
                intent.putExtra("text", "https://enquiry.indianrail.gov.in/mntes/q?opt=TrainRunning&subOpt=splTrnDtl");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(unorganizedsectors.this).toBundle());
            }
        });


        findViewById(R.id.getdirection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://jansunwai.up.nic.in/"));
                    startActivity(i);
                }
                catch (ActivityNotFoundException e){
                    Toast.makeText(unorganizedsectors.this,"You don't have browser installed",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void retrieveJson(String query ,String country, String apiKey){


        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call= ApiClient.getInstance().getApi().getSpecificData(query,apiKey);

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(unorganizedsectors.this,articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(unorganizedsectors.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
