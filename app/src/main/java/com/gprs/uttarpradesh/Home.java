package com.gprs.uttarpradesh;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import androidx.core.app.NotificationCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity{

    private static final int LAUNCH_SECOND_ACTIVITY = 2;
    private AppBarConfiguration mAppBarConfiguration;
    BottomNavigationView bottomNav;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DrawerLayout drawer;
    BroadcastReceiver br;
    TextView headename,headerphone,headerrole;
    ImageView headerimage;
    TextView notificationnumber;
    NavController navController;

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


        if(pref.getString("user","").equals("")){
            startActivity(new Intent(Home.this,logouthome.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }


        getdetails();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startAlarm(false);
            startAlarm(true);
        }



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share,R.id.nav_notification,R.id.nav_profile,R.id.nav_covidcases,R.id.nav_updates)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        View hView =  navigationView.getHeaderView(0);
        headename = hView.findViewById(R.id.navheadername);
        headerphone= hView.findViewById(R.id.navheaderphone);
        headerrole= hView.findViewById(R.id.navheaderrole);
        headerimage=hView.findViewById(R.id.navheaderimage);

        new notificationHelper(this).createOngoingNotification("COVID19RELIEF","Stay Safe from COVID-19");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "Home": navController.navigate(menuItem.getItemId());break;
                    case "Covid Updates": navController.navigate(menuItem.getItemId());break;

                    case "Cases Report": navController.navigate(menuItem.getItemId());break;

                    case "Notification": navController.navigate(menuItem.getItemId());break;

                    case "Profile": navController.navigate(menuItem.getItemId());break;

                    case "Donate":startActivity(new Intent(Home.this,donate.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "First Responder":startActivity(new Intent(Home.this,firstresponder.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Geo fencing":startActivity(new Intent(Home.this,victimalert.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Mapping":startActivity(new Intent(Home.this,MapsActivity.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Self-Assess":startActivity(new Intent(Home.this,self_asses.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Alarm Manager":startActivity(new Intent(Home.this,Alarm.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Assign work":startActivity(new Intent(Home.this,assign_work.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Appointments/Admissions":startActivity(new Intent(Home.this,hospital.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "MSME products":startActivity(new Intent(Home.this,MSME.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Medical shops":
                        Intent intent=new Intent(Home.this,Medicalshops.class);
                        intent.putExtra("text","Pharmacies");
                        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());
                        break;
                    case "Hospital near me":
                        intent=new Intent(Home.this,Medicalshops.class);
                        intent.putExtra("text","Hospitals");
                        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());
                        break;
                    case "Public health care location":startActivity(new Intent(Home.this,publichealthcare.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;
                    case "Online Course":startActivity(new Intent(Home.this,course.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;
                    case "Labs for test":
                        startActivity(new Intent(Home.this,Labsfortestingandresults.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());
                        break;

                    case "ePass and Government service":
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("http://164.100.68.164/upepass2/"));
                            startActivity(i);
                        }
                        catch (ActivityNotFoundException e){
                            Toast.makeText(Home.this,"You don't have browser installed",Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Quora":startActivity(new Intent(Home.this,quora.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;

                    case "Helpline":startActivity(new Intent(Home.this,helpline.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());break;


                    case "Share":final String appPackageName = BuildConfig.APPLICATION_ID;
                        final String appName = getString(R.string.app_name);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        String shareBodyText = "https://play.google.com/store/apps/details?id=" +
                                appPackageName;
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                        startActivity(Intent.createChooser(shareIntent, getString(R.string
                                .share_with)));
                        navController.navigate(menuItem.getItemId());
                        break;
                    case "Whatsapp Queries":
                        try {
                            whatsapp(Home.this, "919013151515");
                        }
                        catch (IllegalStateException e){
                            Toast.makeText(Home.this,"You have no whatsapp",Toast.LENGTH_LONG).show();
                        }
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                if(menuItem.getTitle().equals("Notification"))
                    notificationnumber.setVisibility(View.INVISIBLE);
                headename.setText(String.valueOf(menuItem.getItemId()));
                return false;
            }
        });
        drawer.setBackgroundColor(Color.WHITE);

        setheader();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDateTime = dateFormat.format(new Date()); // Find todays date

        if(!pref.getString("today","").equals(currentDateTime)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    editor.putString("today",currentDateTime);
                    editor.commit();
                    Intent i = new Intent(Home.this, stepstofollow.class);
                    startActivity(i);
                }
            }, 20000);

        }




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

       if(!isMyServiceRunning(AlarmForegroundNotification.class)) {
            startService(AlarmForegroundNotification.class);
        }




        sendBroadcast(new Intent(this, Restarter.class).setAction("Help"));

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                navController.navigate(menuItem.getItemId());
                if(menuItem.getTitle().equals("Notification"))
                    notificationnumber.setVisibility(View.INVISIBLE);
                if(menuItem.getTitle().equals("Donate")){
                    startActivity(new Intent(Home.this,donate.class), ActivityOptions.makeSceneTransitionAnimation(Home.this).toBundle());
                }
                return true;
            }
        });
        notificationnumber=findViewById(R.id.notificationnumber);


        FirebaseDatabase.getInstance().getReference().child("Notification").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    Long count1=dataSnapshot.getChildrenCount();
                    if(count1>=1) {

                        notificationnumber.setText(String.valueOf(count1));

                    }
                    else
                        notificationnumber.setVisibility(View.INVISIBLE);
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
        }
        else if(!bottomNav.getMenu().getItem(0).isChecked()){
            navController.navigate(bottomNav.getMenu().getItem(0).getItemId());
        }
        else {
            if (doubleBackToExitPressedOnce) {
                finish();
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
            startActivityForResult(new Intent(Home.this,Chatbot.class), LAUNCH_SECOND_ACTIVITY);

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
                startActivity(new Intent(Home.this,logouthome.class));
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

       myIntent = new Intent(Home.this, YourLocationBroadcastReciever.class);
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

        myIntent = new Intent(Home.this, MyNotificationBroadcastReceiver.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                switch (result){
                    case "casesreport":navController.navigate(bottomNav.getMenu().getItem(2).getItemId());break;
                    case "updates":navController.navigate(bottomNav.getMenu().getItem(1).getItemId());break;
                    case "home":navController.navigate(bottomNav.getMenu().getItem(0).getItemId());break;
                    case "notification":navController.navigate(bottomNav.getMenu().getItem(3).getItemId());break;
                    case "profile":navController.navigate(bottomNav.getMenu().getItem(4).getItemId());break;


                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
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
}

