package com.gprs.uttarpradesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class test extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    TextView textView;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissions()) {
            textView.setText("All permissions are Granted.");
            textView.setTextColor(getResources().getColor(R.color.GREEN));
            startActivity(new Intent(test.this,Login.class));
            finish();
        } else {
            // show dialog informing them that we lack certain permissions
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView=findViewById(R.id.textView17);

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                boolean flag=true;
                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                        flag=false;
                    }
                }
                if (flag) {
                    textView.setText("All permissions are Granted.");
                    textView.setTextColor(getResources().getColor(R.color.GREEN));
                    startActivity(new Intent(test.this,Login.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    finish();
                } else {
                    textView.setText("All permissions are required for the app to work normally.");
                    textView.setTextColor(getResources().getColor(R.color.RED));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent i=new Intent(test.this,test.class);
                            startActivity(i);
                            finish();
                        }
                    }, 3000);

                }
                return;
            }
        }
    }
}