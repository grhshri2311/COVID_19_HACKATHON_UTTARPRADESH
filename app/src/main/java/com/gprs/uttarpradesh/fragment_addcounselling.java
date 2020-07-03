package com.gprs.uttarpradesh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class fragment_addcounselling extends Fragment {

    ArrayList<String> name, role, time, message;
    ArrayList<UserRegistrationHelper> users;
    CustomCounsellingApplicationAdapter c;
    ListView list;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_addcounselling, container, false);

        name = new ArrayList<>();
        role = new ArrayList<>();
        time = new ArrayList<>();
        users = new ArrayList<>();
        message = new ArrayList<>();
        list = view.findViewById(R.id.list);
        c = new CustomCounsellingApplicationAdapter(getActivity(), name, role, message, time);
        list.setAdapter(c);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                view(position);
                view.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
            }
        });
        pref = getActivity().getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode

        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.refresh).setVisibility(View.INVISIBLE);
                name.clear();
                users.clear();
                role.clear();
                message.clear();
                time.clear();
                FirebaseDatabase.getInstance().getReference().child("counselling").child("application").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {

                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                CounsellorHelper ch = d.getValue(CounsellorHelper.class);
                                users.add(ch.getU());
                                name.add(ch.getU().getFname());
                                role.add(ch.getU().getRole());
                                message.add(ch.getMessage());
                                time.add(ch.getTime());
                            }
                            c.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference().child("counselling").child("application").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    name.clear();
                    users.clear();
                    role.clear();
                    message.clear();
                    time.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        CounsellorHelper ch = d.getValue(CounsellorHelper.class);
                        users.add(ch.getU());
                        name.add(ch.getU().getFname());
                        role.add(ch.getU().getRole());
                        message.add(ch.getMessage());
                        time.add(ch.getTime());
                    }
                    c.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void view(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", users.get(position).getFname());
        bundle.putString("phone", users.get(position).getPhone());
        bundle.putString("email", users.get(position).getEmail());
        bundle.putString("role", users.get(position).getRole());
        bundle.putString("message", message.get(position));
        bundle.putString("time", time.get(position));
        bundle.putDouble("lat", users.get(position).getLat());
        bundle.putDouble("lon", users.get(position).getLon());

        BottomSheetDialogFragment f = new Bottomsheetcounsellingapplicationviewfragment();
        f.setArguments(bundle);
        f.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

}