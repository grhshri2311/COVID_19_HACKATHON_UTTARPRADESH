package com.gprs.uttarpradesh;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.gprs.uttarpradesh.your_work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationFragment extends Fragment {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> arrayList=new ArrayList<>();
    ListView notices;
    ArrayAdapter adapter;
    TextView notice;
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_notification, container, false);

        notice=root.findViewById(R.id.notice);
        notices=root.findViewById(R.id.notices);
        pref = getActivity().getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        adapter = new ArrayAdapter<String>(root.getContext(),R.layout.notice,R.id.notice1,arrayList);
        notices.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("Notification").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null)
                {
                    String str = "0";
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        notice.setVisibility(View.INVISIBLE);
                        for(DataSnapshot dat:data.getChildren())
                            arrayList.add(dat.getValue(String.class)+'\n'+dat.getKey());
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Notification").child(pref.getString("user","")).removeValue();


        return root;
    }


}
