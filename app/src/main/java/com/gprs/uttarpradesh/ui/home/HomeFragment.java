package com.gprs.uttarpradesh.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gprs.uttarpradesh.APIextract;
import com.gprs.uttarpradesh.Adapterviewer;
import com.gprs.uttarpradesh.MapsActivity;
import com.gprs.uttarpradesh.Modelviewer;
import com.gprs.uttarpradesh.R;
import com.gprs.uttarpradesh.UserLocationHelper;
import com.gprs.uttarpradesh.UserRegistrationHelper;
import com.gprs.uttarpradesh.UserSelfAssessHelper;
import com.gprs.uttarpradesh.advice;
import com.gprs.uttarpradesh.assign_work;
import com.gprs.uttarpradesh.firstresponder;
import com.gprs.uttarpradesh.victimalert;
import com.gprs.uttarpradesh.your_work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment{
    RelativeLayout viewmap,todo;
    TextView confirm,death,mystatus,mystatus1,mystatus2,todotext;
    ImageView todoimage;
    int total=0,sick=0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    UserRegistrationHelper userRegistrationHelper;
    RelativeLayout scan,firstresponder;
    boolean flag=false;
    TextView  scrolltextview;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        scrolltextview = (TextView) root.findViewById(R.id.scrollingtextview);
        scrolltextview.setSelected(true);

        scan=root.findViewById(R.id.scan);
        todo=root.findViewById(R.id.todo);
        viewmap=root.findViewById(R.id.viewmap);
        mystatus=root.findViewById(R.id.mystatus);
        mystatus1=root.findViewById(R.id.mystatus1);
        mystatus2=root.findViewById(R.id.mystatus2);
        pref =root.getContext().getSharedPreferences("user", 0); //
        editor=pref.edit();

        todoimage=root.findViewById(R.id.todoimage);
        todotext=root.findViewById(R.id.todotext);

        setview();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDateTime = dateFormat.format(new Date()); // Find todays date

        if(!pref.getString("today1","").equals(currentDateTime)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    editor.putString("today1",currentDateTime);
                    editor.commit();
                    Intent i = new Intent(root.getContext(), advice.class);
                    startActivity(i);
                }
            }, 10000);

        }

        check();







        confirm=root.findViewById(R.id.confirm);
        death=root.findViewById(R.id.death);

        APIextract apIextract=new APIextract(root.getContext(),confirm,death);


        viewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), MapsActivity.class));
            }
        });




        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    if(userRegistrationHelper.getRole().equals("Monitors")){
                        startActivity(new Intent(root.getContext(), assign_work.class));
                    }
                    else {
                        startActivity(new Intent(root.getContext(), your_work.class));
                    }
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pref.getString("status","").equals("victim"))
                    startActivity(new Intent(root.getContext(), victimalert.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                else {
                    Toast.makeText(root.getContext(),"You are found victim \nYou can't use this festure!",Toast.LENGTH_LONG).show();
                }
            }
        });

        firstresponder=root.findViewById(R.id.firstresponder);
        firstresponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), com.gprs.uttarpradesh.firstresponder.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        return root;
    }

    void check() {

        FirebaseDatabase.getInstance().getReference().child("Location").child(pref.getString("user","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserLocationHelper userLocationHelper1 = dataSnapshot.getValue(UserLocationHelper.class);
                if(userLocationHelper1!=null){

                    FirebaseDatabase.getInstance().getReference().child("Assess")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if(snapshot.getKey().equals(pref.getString("user",""))){
                                            continue;
                                        }
                                        final UserSelfAssessHelper userLocationHelper = snapshot.getValue(UserSelfAssessHelper.class);
                                        float[] results = new float[1];

                                        Location.distanceBetween(userLocationHelper.getLat(), userLocationHelper.getLon(),
                                                userLocationHelper1.getLat(), userLocationHelper.getLon(), results);
                                        if (results[0] < 1000) {
                                            total = total + 1;
                                            if (userLocationHelper.getStatus() >=3) {
                                                sick = sick + 1;
                                            }
                                        }


                                    }
                                    if(sick>0){
                                        mystatus.setText("You have to be Safe !\n");
                                        mystatus.setTextColor(getActivity().getResources().getColor(R.color.RED));
                                        mystatus1.setText("Total assessed : "+total);
                                        mystatus2.setText("Found Sick : "+sick+"\nWithin 1 KM radius");

                                    }
                                    else {
                                        mystatus.setText("You are Safe !\n");
                                        mystatus.setTextColor(getActivity().getResources().getColor(R.color.GREEN));
                                        mystatus1.setText("Total assessed : "+total);
                                        mystatus2.setText("Found Sick : "+sick+"\nWithin 1 KM radius");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });



                }
                else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    void setview(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    userRegistrationHelper=dataSnapshot.getValue(UserRegistrationHelper.class);
                    flag=true;
                    if(userRegistrationHelper.getRole().equals("Monitors")){
                        todoimage.setImageDrawable(root.getContext().getDrawable(R.drawable.assign));
                        todotext.setText("Assign Work");
                    }
                    else {
                        todoimage.setImageDrawable(root.getContext().getDrawable(R.drawable.todo));
                        todotext.setText("Todo List");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
