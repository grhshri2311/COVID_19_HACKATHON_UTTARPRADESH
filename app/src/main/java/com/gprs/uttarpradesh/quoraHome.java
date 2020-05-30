package com.gprs.uttarpradesh;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class quoraHome extends Fragment {


    private View root;
    ListView listView;
    CustomQuoraAdapter customAdapter;
    ArrayList<QuoraHelper> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =inflater.inflate(R.layout.fragment_quora_home, container, false);

        arrayList=new ArrayList<>();



        listView=root.findViewById(R.id.quoralist);

        customAdapter=new CustomQuoraAdapter(getActivity(),arrayList);
        listView.setAdapter(customAdapter);

        customAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference().child("Quora").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren() ) {
                        QuoraHelper quoraHelper = snapshot.getValue(QuoraHelper.class);
                        arrayList.add(quoraHelper);

                        Collections.sort(arrayList,new comp());
                        customAdapter.notifyDataSetChanged();
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    class comp implements Comparator<QuoraHelper> {

        @Override
        public int compare(QuoraHelper o1, QuoraHelper o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    }
}
