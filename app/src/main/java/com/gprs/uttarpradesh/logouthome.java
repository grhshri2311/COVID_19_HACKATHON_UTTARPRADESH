package com.gprs.uttarpradesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.log;

public class logouthome extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 111;
    private static final int REQUEST_INVITE = 10115;
    RelativeLayout selfassess, dashmap, updates, case_report, helpline, donate, scan, medstore, epass, admission;
    Toolbar toolbar;
    TextView confirm, death;
    BroadcastReceiver br;
    private FusedLocationProviderClient fusedLocationClient;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logouthome);


        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startAlarm(false);
            startAlarm(true);
        }


        if (!pref.getString("user", "").equals("")) {
            startActivity(new Intent(logouthome.this, Login.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            finish();
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(logouthome.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }
        new notificationHelper(this).createOngoingNotification("COVID19RELIEF", "Stay Safe from COVID-19");


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDateTime = dateFormat.format(new Date()); // Find todays date


        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("chatintrologout", false)) {
            chatbotintro();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("chatintrologout", true).apply();
        }


        donate = findViewById(R.id.donate);
        helpline = findViewById(R.id.helpline);
        scan = findViewById(R.id.scan);
        dashmap = findViewById(R.id.dashmap);
        epass = findViewById(R.id.epass);

        selfassess = findViewById(R.id.selfassess);
        confirm = findViewById(R.id.confirm);
        death = findViewById(R.id.death);
        updates = findViewById(R.id.updates);
        medstore = findViewById(R.id.medstore);
        admission = findViewById(R.id.admission);


        TextView scrolltextview = findViewById(R.id.scrollingtextview);
        scrolltextview.setSelected(true);


        APIextract apIextract = new APIextract(this, confirm, death);


        case_report = findViewById(R.id.case_report);
        setToolBar();


        ImageView i1, i2, i3, i4, i5;
        i1 = findViewById(R.id.implink1);
        i2 = findViewById(R.id.implink2);
        i3 = findViewById(R.id.implink3);
        i4 = findViewById(R.id.implink4);
        i5 = findViewById(R.id.implink5);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://www.mohfw.gov.in/");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://www.nhp.gov.in/disease/communicable-disease/novel-coronavirus-2019-ncov");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://www.mohfw.gov.in/");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://www.icmr.gov.in/");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://nhm.gov.in/");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        findViewById(R.id.publiccare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, publichealthcare.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        findViewById(R.id.onlinecourse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, course.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        ImageView shelter;
        LinearLayout faq, faq1;
        shelter = findViewById(R.id.shelter);
        faq = findViewById(R.id.faq);
        faq1 = findViewById(R.id.faq1);


        shelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://drive.google.com/file/d/1mWap_8QEc3HUAiIpPM65K2rZiPjudMO1/view");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://drive.google.com/file/d/1GPoaMCIwbUdd3XDCzHnY_HiP7p2dVJ4x/view?usp=sharing");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        faq1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, pdfViewer.class);
                intent.putExtra("text", "https://drive.google.com/file/d/1A0mY4oMMoSMY5IeuhtKTWVvTkkdOy7lf/view?usp=sharing");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        epass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://164.100.68.164/upepass2/"));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(logouthome.this, "You don't have browser installed", Toast.LENGTH_LONG).show();
                }
            }
        });

        admission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Bottomsheetadmissionfragment().show(getSupportFragmentManager(),"Dialog");            }
        });

        medstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logouthome.this, Medicalshops.class);
                intent.putExtra("text", "Pharmacies");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pref.getString("status", "").equals("victim"))
                    startActivity(new Intent(logouthome.this, victimalert.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
                else {
                    Toast.makeText(logouthome.this, "You are found victim \nYou can't use this festure!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dashmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, MapsActivity.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        case_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, cases_report.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, news.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });

        selfassess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, self_asses.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });


        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, helpline.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logouthome.this, donate.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
            }
        });


        if (!isMyServiceRunning(AlarmForegroundNotification.class)) {
            startService(AlarmForegroundNotification.class);
        }

        if (!isMyServiceRunning(HelpneededBroadcastReceiver.class)) {
            startService(HelpneededBroadcastReceiver.class);
        }

        sendBroadcast(new Intent(this, Restarter.class).setAction("Help"));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulogout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.chatbot) {
            startActivity(new Intent(logouthome.this, Chatbot.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());

        }
        if (id == R.id.translate) {
            SharedPreferences pref;
            SharedPreferences.Editor editor;
            pref = getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode
            editor = pref.edit();
            if (pref.getString("lang", "").equals("")) {
                editor.putString("lang", "hi");
                editor.apply();
                setAppLocale("hi");
            } else {
                editor.putString("lang", "");
                editor.commit();
                setAppLocale("en");
            }
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }

        if (id == R.id.share) {
            share();
            final String appPackageName = BuildConfig.APPLICATION_ID;
            final String appName = getString(R.string.app_name);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id=" +
                    appPackageName;
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(shareIntent, getString(R.string
                    .share_with)));
        }
        return true;
    }

    private void setToolBar() {
        androidx.appcompat.widget.Toolbar tb = findViewById(R.id.hometoolbar);
        setSupportActionBar(tb);
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void Exit1() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void startAlarm(boolean set) {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;


        // SET TIME HERE
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = null;

        // SET TIME HERE
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        myIntent = new Intent(logouthome.this, YourLocationBroadcastReciever.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);


        if (set) {

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent);

        } else if (manager != null) {
            manager.cancel(pendingIntent);
        }

        myIntent = new Intent(logouthome.this, MyNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);


        if (set) {

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent1);

        } else {
            if (manager != null) {
                manager.cancel((pendingIntent1));
            }
        }
        myIntent = new Intent(logouthome.this, HelpneededBroadcastReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);


        if (set) {

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent2);

        } else {
            if (manager != null) {
                manager.cancel((pendingIntent2));
            }

        }

    }

    void location() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Location location = task.getResult();

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(logouthome.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        Toast.makeText(logouthome.this, "Please turn on GPS and accept location permission", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(logouthome.this, "Please turn on GPS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(logouthome.this,"Permission granted",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(logouthome.this,Home.class));
                } else {
                    Toast.makeText(logouthome.this,"Permission denied",Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public void alarm(View view) {
        startActivity(new Intent(this,Alarm.class));
    }

    public void firstresponder(View view) {
        startActivity(new Intent(logouthome.this,firstresponder.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(br);
        }
        catch (Exception e){

        }
    }

    public void chatbot(View view) {
        startActivity(new Intent(logouthome.this,Chatbot.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
    }

    public void whatsapp(View view) {
        try {
            whatsapp(this, "919013151515");
        }
        catch (IllegalStateException e){
            Toast.makeText(this,"You have no whatsapp",Toast.LENGTH_LONG).show();
        }
    }

    public static void whatsapp(Activity activity, String phone) {
        String formattedNumber = (phone);
        try{
            Intent sendIntent =new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT,"Press 1 for latest updates");
            sendIntent.putExtra("jid", formattedNumber +"@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
            activity.startActivity(sendIntent);
        }
        catch(Exception e)
        {
            Toast.makeText(activity,"You don't have Whatsapp"+ e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void chatbotintro() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();


        LayoutInflater inflater=getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_chatbotintro, null, true);


        Button button = view.findViewById(R.id.btn_get_started);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.hide();

            }
        });

        alert.setView(view);
        alert.show();

    }

    public void startService(Class<?> serviceClass) {
        Intent serviceIntent = new Intent(this, serviceClass);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopService(Class<?> serviceClass) {
        Intent serviceIntent = new Intent(this, serviceClass);
        stopService(serviceIntent);

    }

    public void assign(View view) {
        startActivity(new Intent(logouthome.this,assign_work.class));

    }

    public void labs(View view) {
        startActivity(new Intent(logouthome.this,Labsfortestingandresults.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
    }

    public void msme(View view) {
        startActivity(new Intent(logouthome.this,MSME.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());

    }

    public void hosnear(View view) {
        Intent intent=new Intent(this,Medicalshops.class);
        intent.putExtra("text","Hospitals");
        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void login(View view) {
        startActivity(new Intent(logouthome.this,Login.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
        finish();
    }

    private void share() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Share");



        LayoutInflater inflater=getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_share, null, true);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final String appPackageName = BuildConfig.APPLICATION_ID;
        final String appName = getString(R.string.app_name);

        String shareBodyText = "https://play.google.com/store/apps/details?id=" +
                appPackageName;

        TextView textView=view.findViewById(R.id.text_share);
        textView.setText(shareBodyText);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setView(view);
        alert.show();


    }

    public void sectorworkers(View view) {
        startActivity(new Intent(logouthome.this,unorganizedsectors.class), ActivityOptions.makeSceneTransitionAnimation(logouthome.this).toBundle());
    }
}
