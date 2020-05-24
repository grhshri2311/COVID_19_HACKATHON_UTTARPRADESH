package com.gprs.uttarpradesh;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    BottomNavigationView bottomNav;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DrawerLayout drawer;
    BroadcastReceiver br;
    TextView headename,headerphone,headerrole;
    ImageView headerimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref =getSharedPreferences("user", 0); //
        editor=pref.edit();
        br = new InternetBroadcastReciever();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);

        getdetails();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startAlarm(false);
            startAlarm(true);
        }



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share,R.id.nav_notification,R.id.nav_profile,R.id.nav_covidcases,R.id.nav_updates)
                .setDrawerLayout(drawer)
                .build();

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        View hView =  navigationView.getHeaderView(0);
        headename = (TextView)hView.findViewById(R.id.navheadername);
        headerphone=(TextView)hView.findViewById(R.id.navheaderphone);
        headerrole=(TextView)hView.findViewById(R.id.navheaderrole);
        headerimage=hView.findViewById(R.id.navheaderimage);
        setheader();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDateTime = dateFormat.format(new Date()); // Find todays date

        if(!pref.getString("todaychatintro","").equals(currentDateTime)) {
            chatbotintro();
            editor.putString("todaychatintro",currentDateTime);
            editor.commit();
        }

        FirebaseDatabase.getInstance().getReference().child("Location").child(pref.getString("user","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    UserLocationHelper u = dataSnapshot.getValue(UserLocationHelper.class);

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(Home.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(u.getLat(), u.getLon(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



                        String city = addresses.get(0).getLocality();
                        String state=addresses.get(0).getAdminArea();

                        editor.putString("city",city);
                        editor.putString("state",state);
                        editor.commit();



                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

     /*   if(!isMyServiceRunning(AlarmForegroundNotification.class)) {
            startService(AlarmForegroundNotification.class);
        }


      */

        sendBroadcast(new Intent(this, Restarter.class).setAction("Help"));




        FirebaseDatabase.getInstance().getReference().child("Notification").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    Long count1=dataSnapshot.getChildrenCount();
                    if(count1>=1) {
                        bottomNav.getMenu().getItem(3).setIcon(R.drawable.ic_notifications_active_black_24dp1);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void setheader() {

        pref = getSharedPreferences("user", 0); // 0 - for private mode

        String useremail = pref.getString("user", "");
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        if(mStorageRef.child(useremail)!=null) {
            StorageReference sr = mStorageRef.child("proImg").child(useremail+".jpg");
            sr.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    headerimage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(useremail);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserRegistrationHelper helper = dataSnapshot.getValue(UserRegistrationHelper.class);
                    headename.setText(helper.getFname());
                    headerphone.setText(helper.getPhone());
                    headerrole.setText(helper.getRole());
                    bottomNav.getMenu().getItem(3).setIcon(getDrawable(R.drawable.ic_notifications_active_black_24dp1));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(item.getItemId() == android.R.id.home){ // use android.R.id
            drawer.openDrawer(GravityCompat.START);
        }
        if(id==R.id.logoutmenu){
           startAlarm(false);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(101);
            mNotificationManager.cancel(33);
            Exit();
        }

        if(id==R.id.chatbot){
            startActivity(new Intent(Home.this,Chatbot.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());

        }
        if(id==R.id.translate){
            SharedPreferences pref;
            SharedPreferences.Editor editor;
            pref = getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode
            editor = pref.edit();
            if (pref.getString("lang", "").equals("")) {
                editor.putString("lang", "hi");
                editor.commit();
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

        if(id==R.id.share){
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

    public void Exit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref;
                SharedPreferences.Editor editor;


                pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
                editor = pref.edit();

                editor.clear();
                editor.apply();
                if(isMyServiceRunning(VictimAlertForegroundNotification.class))
                    stopService(VictimAlertForegroundNotification.class);
                startActivity(new Intent(Home.this,Login.class));
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
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

    void  getdetails(){
        final SharedPreferences pref;
        final SharedPreferences.Editor editor;

        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();

        FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user","")).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer status=dataSnapshot.getValue(Integer.class);
                if(status!=null && status==1) {
                    editor.putString("status", "victim");
                    editor.commit();
                    if(!isMyServiceRunning(VictimAlertForegroundNotification.class)) {
                        startService(VictimAlertForegroundNotification.class);
                    }
                }
                else {
                    editor.putString("status", "normal");
                    editor.commit();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserRegistrationHelper userRegistrationHelper=dataSnapshot.getValue(UserRegistrationHelper.class);
                if(userRegistrationHelper!=null) {
                    editor.putString("role", userRegistrationHelper.getRole());
                    editor.commit();
                }
                else {
                    editor.putString("role", "not defined");
                    editor.commit();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void chatbotintro() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();


        LayoutInflater inflater=getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_chatbotintro, null, true);


        Button button = (Button) view.findViewById(R.id.btn_get_started);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.hide();

            }
        });

        alert.setView(view);
        alert.show();

    }

    void startAlarm(boolean set) {

        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;


        // SET TIME HERE
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());




        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = null;

        // SET TIME HERE
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

   /*     myIntent = new Intent(home.this, YourLocationBroadcastReciever.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,myIntent,0);


        if(set){

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent);

        }
        else
        if (manager!= null) {
            manager.cancel(pendingIntent);
        }

        myIntent = new Intent(home.this, MyNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(),0,myIntent,0);


        if(set){

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent1);

        }
        else {
            if (manager != null) {
                manager.cancel((pendingIntent1));
            }
        }

    */
        myIntent = new Intent(Home.this, HelpneededBroadcastReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(),0,myIntent,0);


        if(set){

            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            1 * 1000, pendingIntent2);

        }
        else {
            if (manager != null) {
                manager.cancel((pendingIntent2));
            }

        }

    }
}

