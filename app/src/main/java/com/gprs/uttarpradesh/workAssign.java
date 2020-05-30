package com.gprs.uttarpradesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class workAssign extends AppCompatActivity {

    String name1,role1,place1,phone1,email1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ListView listView;
    VolunteerAdapter adapter;
    private ArrayList<String> namelist,rolelist,placelist,phonelist;
    private ArrayList<Double> latlist,lonlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_assign);

        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();

        namelist=new ArrayList();
        rolelist=new ArrayList();
        placelist=new ArrayList();
        lonlist=new ArrayList();
        latlist=new ArrayList();
        phonelist=new ArrayList();


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("name");
        namelist = (ArrayList<String>) args.getSerializable("nameARRAYLIST");
        args = intent.getBundleExtra("role");
        rolelist = (ArrayList<String>) args.getSerializable("roleARRAYLIST");
        args = intent.getBundleExtra("place");
        placelist = (ArrayList<String>) args.getSerializable("placeARRAYLIST");
        args = intent.getBundleExtra("lat");
        latlist = (ArrayList<Double>) args.getSerializable("latARRAYLIST");
        args = intent.getBundleExtra("lon");
        lonlist = (ArrayList<Double>) args.getSerializable("lonARRAYLIST");
        args = intent.getBundleExtra("phone");
        phonelist = (ArrayList<String>) args.getSerializable("phoneARRAYLIST");



        listView = findViewById(R.id.volunteers);
        adapter = new VolunteerAdapter(this, namelist, rolelist, placelist);
        listView.setAdapter(adapter);

        final EditText work=findViewById(R.id.work);

        Button button=findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!work.getText().toString().isEmpty()){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                    FirebaseDatabase.getInstance().getReference().child("Location").child(pref.getString("user","")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                                UserLocationHelper userLocationHelper=dataSnapshot.getValue(UserLocationHelper.class);
                                workhelper workhelper=new workhelper(userLocationHelper.getFname(),location(userLocationHelper.getLat(),userLocationHelper.getLon()),userLocationHelper.getRole(),userLocationHelper.getEmail(),userLocationHelper.getPhone(),work.getText().toString());

                                for(int i=0;i<phonelist.size();i++) {
                                    FirebaseDatabase.getInstance().getReference().child("Works").child(phonelist.get(i)).child(currentDateTime).setValue(workhelper);


                                    workhelper = new workhelper(namelist.get(i), location(latlist.get(i),lonlist.get(i)), rolelist.get(i),"", phonelist.get(i), work.getText().toString());
                                    FirebaseDatabase.getInstance().getReference().child("Workassign").child(pref.getString("user", "")).child(currentDateTime+ i).setValue(workhelper);
                                    FirebaseDatabase.getInstance().getReference().child("Notification").child(phonelist.get(i)).child("Work").child(currentDateTime).setValue("You are assigned for work");
                                }
                                show();
                                Toast.makeText(workAssign.this,"Work assigned",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    Toast.makeText(workAssign.this,"Please fill all fields ",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Work Assigned Successfully !");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(workAssign.this,assign_work.class));

                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    String location(double lat,double lon){


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(workAssign.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



            String city = addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();

            return  city+','+state;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }

}
