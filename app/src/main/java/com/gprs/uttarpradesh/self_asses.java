package com.gprs.uttarpradesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class self_asses extends AppCompatActivity {

    static ListView message_view;
    private static Integer[] togle;
    static ArrayList<String> question1=new ArrayList<String>();
    static ArrayList<String> answer1=new ArrayList<String>();
    static ArrayList<String> answer=new ArrayList<>();
    static ArrayList<ArrayList<String>> menu1=new ArrayList<ArrayList<String>>();
    static CustomAdapter customAdapter;
    static ArrayList<String> question=new ArrayList<>();
    static ArrayList<ArrayList<String>> menu=new ArrayList<>();
    static HashMap<String,Integer> result;
    static int count=0,pos=0;
    static UserLocationHelper userLocationHelper;

    static DatabaseReference reference;

    static int status=0;
    static  String message;
    static boolean isolate=false,noprob=false,covid=false,general=false;
    static FirebaseDatabase database;

    void init(){
        status=0;
        answer=new ArrayList<>();
        result=new HashMap<String, Integer>();
        String[] ques = getResources().getStringArray(R.array.self_assessq);
        question= new ArrayList<>(Arrays.asList(ques));
        menu=new ArrayList<>();
        answer=new ArrayList<>();
        isolate=false;
        noprob=false;
        covid=false;
        general=false;
        message_view=findViewById(R.id.messages_view);

        String[] menus = getResources().getStringArray(R.array.self_assessm0);
        menu.add(new ArrayList<>(Arrays.asList(menus)));

        menus = getResources().getStringArray(R.array.self_assessm1);
        menu.add(new ArrayList<>(Arrays.asList(menus)));


        result.put(menu.get(1).get(1),3);
        result.put(menu.get(1).get(2),3);
        result.put(menu.get(1).get(3),3);
        result.put(menu.get(1).get(4),0);





        menus = getResources().getStringArray(R.array.self_assessm2);
        menu.add(new ArrayList<>(Arrays.asList(menus)));
        result.put(menu.get(2).get(1),2);
        result.put(menu.get(2).get(2),2);
        result.put(menu.get(2).get(3),2);
        result.put(menu.get(2).get(4),0);



        menus = getResources().getStringArray(R.array.self_assessm3);
        menu.add(new ArrayList<>(Arrays.asList(menus)));
        result.put(menu.get(3).get(1),1);
        result.put(menu.get(3).get(2),0);



        menus = getResources().getStringArray(R.array.self_assessm4);
        menu.add(new ArrayList<>(Arrays.asList(menus)));

        result.put(menu.get(4).get(1),1);
        result.put(menu.get(4).get(2),1);
        result.put(menu.get(4).get(3),0);


        question1=new ArrayList<>();
        answer1=new ArrayList<>();
        menu1=new ArrayList<>();
        togle=new Integer[20];
        count=0;pos=0;
        if(pos<question.size()) {
            question1.add(question.get(pos));
            menu1.add(menu.get(pos));
            answer1.add("");
            togle[count++] = -1;
            question1.add(question.get(++pos));
            menu1.add(menu.get(pos));
            answer1.add("");
            togle[count++] = 1;

        }




        customAdapter=new CustomAdapter(this,menu1,togle,question1,answer1);
        message_view.setAdapter(customAdapter);

        customAdapter.notifyDataSetChanged();

    }

    public static void answered(String itemAtPosition, Activity context) {
        if(!itemAtPosition.equals("")) {
            question1.add("");
            menu1.add(null);
            int r=result.get(itemAtPosition);
            if(r==1)
                isolate=true;
            else if (r==3)
                covid=true;
            else if(r==2)
                general=true;
            else
                noprob=true;

            answer.add(itemAtPosition);
            answer1.add(itemAtPosition);
            togle[count++] = 2;
            customAdapter.notifyDataSetChanged();
        }
        if(pos+1<question.size()) {
            answer1.add("");
            question1.add(question.get(++pos));
            menu1.add(menu.get(pos));
            togle[count++]=1;
            customAdapter.notifyDataSetChanged();
            message_view.setSelection(pos);
        }
        else {
            next(context);
        }
    }

    private static void next(Activity context) {

        if(covid){
            status=5;
            message="You may have COVID-19 Symptomns ,So you are recommended to consult a doctor\n";

        }
        else if(isolate){
            status=3;
            message="You are recommended to isolate yourself and to have COVID-19 test\n";
        }
        else if(general){
            status=1;
            message="You are may consult doctor for health improvements\n";
        }
        else
        {
            status=0;
            message="Your infection risk is low Stay Home and Stay Safe !";
        }


        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = context.getSharedPreferences("user", 0); // 0 - for private mode

            save(context);
            question1.clear();
            question.clear();
            answer1.clear();
            menu.clear();
            menu1.clear();


            menu1.add(null);
            answer1.add("");
            togle[0]=11;
            question1.add(message);
            customAdapter.notifyDataSetChanged();





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_asses);

        init();


        database=FirebaseDatabase.getInstance();
        reference=database.getReference().child("Assess");
        message_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

    }


    @Override
    public void onBackPressed() {
        Exit1();
    }

    public void Exit1() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to quit Self-Assess?");
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
    static void save(final Context context){

        try {
            final SharedPreferences pref;
            SharedPreferences.Editor editor;
            pref = context.getSharedPreferences("user", 0); // 0 - for private mode
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            if(!pref.getString("user","").equals("")) {
                FirebaseDatabase.getInstance().getReference().child("Location").child(pref.getString("user", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userLocationHelper = dataSnapshot.getValue(UserLocationHelper.class);
                        if (userLocationHelper != null) {
                            UserSelfAssessHelper userSelfAssessHelper = new UserSelfAssessHelper(userLocationHelper.getLat(), userLocationHelper.getLon(), self_asses.answer, status);
                            FirebaseDatabase.getInstance().getReference().child("Assess").child(pref.getString("user", "")).setValue(userSelfAssessHelper);
                            if (status >= 3)
                                FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user", "")).child("status").setValue(1);
                            answer.clear();
                            Toast.makeText(context, "Tested Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Tested locally \nCheck your internet connection ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                Toast.makeText(context, "Tested locally", Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e){

        }

    }


}
