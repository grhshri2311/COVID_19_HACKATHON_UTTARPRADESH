package com.gprs.uttarpradesh;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class assign_work extends AppCompatActivity {

    Button role,filter;
    ArrayList<String> namelist, rolelist, placelist;
    ArrayList<UserLocationHelper> volunteer;
    ListView listView,listview1;
    VolunteerAdapter adapter;
    VolunteerAdapter1 adapter1;
    EditText place;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> n,r,p,w,s,c;
    AlertDialog alert;
    Boolean select=true;
    int selectedcount=0;

    ArrayList<Integer> number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_work);

        namelist = new ArrayList<>();
        rolelist = new ArrayList<>();
        placelist = new ArrayList<>();
        listView = findViewById(R.id.volunteers);
        listview1=findViewById(R.id.volunteers1);

        number=new ArrayList<>();
        place=findViewById(R.id.place);
        filter=findViewById(R.id.filter);
        role = findViewById(R.id.role);
        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor=pref.edit();


        n=new ArrayList<>();
        r=new ArrayList<>();
        p=new ArrayList<>();
        w=new ArrayList<>();
        s=new ArrayList<>();
        c=new ArrayList<>();
        volunteer = new ArrayList<>();

        adapter = new VolunteerAdapter(this, namelist, rolelist, placelist);
        listView.setAdapter(adapter);

        adapter1=new VolunteerAdapter1(this,n,r,p,w,c,s);
        listview1.setAdapter(adapter1);

        final ScrollView sv=findViewById(R.id.sv);

        listview1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        sv.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(view.findViewById(R.id.checked).getVisibility()!=View.VISIBLE){
                    view.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                    selectedcount = selectedcount + 1;
                    findViewById(R.id.go).setVisibility(View.VISIBLE);
                    Toast.makeText(assign_work.this, selectedcount + " selected", Toast.LENGTH_LONG).show();
                    number.add(position);
                    select = false;
                }
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(select){
                    show(position,view);
                }
                else if(view.findViewById(R.id.checked).getVisibility()==View.VISIBLE){
                    selectedcount=selectedcount-1;
                    view.findViewById(R.id.checked).setVisibility(View.INVISIBLE);
                    for(int i=0;i<number.size();i++){
                        if(number.get(i)==position)
                        {
                            number.remove(i);
                        break;
                        }
                    }
                }
                else {
                    selectedcount=selectedcount+1;
                    number.add(position);
                    view.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                }
                if(selectedcount==0){
                    select=true;
                findViewById(R.id.go).setVisibility(View.INVISIBLE);
                }
            }
        });

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendlist();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namelist.clear();
                rolelist.clear();
                placelist.clear();
                volunteer.clear();
                adapter.notifyDataSetChanged();
                if (role.getText().toString().equals("Select catagory")) {
                    FirebaseDatabase.getInstance().getReference().child("Location")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);
                                        if (location(userLocationHelper.getLat(), userLocationHelper.getLon()).toLowerCase().contains(place.getText().toString().toLowerCase()) && !userLocationHelper.getPhone().equals(pref.getString("user",""))) {
                                            volunteer.add(userLocationHelper);
                                            namelist.add(userLocationHelper.getFname());
                                            rolelist.add(userLocationHelper.getRole());
                                            placelist.add(location(userLocationHelper.getLat(), userLocationHelper.getLon()));
                                            adapter.notifyDataSetChanged();
                                            place.setText("");
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Location")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);
                                        if (userLocationHelper.getRole().equals(role.getText().toString()) &&location(userLocationHelper.getLat(), userLocationHelper.getLon()).toLowerCase().contains(place.getText().toString().toLowerCase()) && !userLocationHelper.getPhone().equals(pref.getString("user",""))) {
                                            volunteer.add(userLocationHelper);
                                            namelist.add(userLocationHelper.getFname());
                                            rolelist.add(userLocationHelper.getRole());
                                            placelist.add(location(userLocationHelper.getLat(), userLocationHelper.getLon()));
                                            adapter.notifyDataSetChanged();
                                            place.setText("");
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            }

        });
        role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu rolemenu = new PopupMenu(getApplicationContext(), role);
                rolemenu.getMenuInflater().inflate(R.menu.role, rolemenu.getMenu());
                rolemenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        role.setText(item.getTitle().toString());
                        namelist.clear();
                        rolelist.clear();
                        placelist.clear();
                        volunteer.clear();
                        adapter.notifyDataSetChanged();
                        FirebaseDatabase.getInstance().getReference().child("Location")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);
                                            if (userLocationHelper.getRole().equals(item.getTitle().toString()) && !userLocationHelper.getPhone().equals(pref.getString("user",""))) {
                                                volunteer.add(userLocationHelper);
                                                namelist.add(userLocationHelper.getFname());
                                                rolelist.add(userLocationHelper.getRole());
                                                placelist.add(location(userLocationHelper.getLat(), userLocationHelper.getLon()));
                                                adapter.notifyDataSetChanged();
                                                place.setText("");
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                        return true;
                    }
                });
                rolemenu.show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Workassign").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   WorkAssignHelper workhelper=snapshot.getValue(WorkAssignHelper.class);
                  n.add(workhelper.getFname());
                    r.add(workhelper.getRole());
                    p.add(workhelper.getPlace());
                    w.add(workhelper.getWork());
                    s.add(workhelper.getStatus());
                    c.add(workhelper.getComment());



                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);
                            if(!userLocationHelper.getPhone().equals(pref.getString("user",""))) {
                                volunteer.add(userLocationHelper);
                                namelist.add(userLocationHelper.getFname());
                                rolelist.add(userLocationHelper.getRole());
                                placelist.add(location(userLocationHelper.getLat(), userLocationHelper.getLon()));
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void sendlist() {
        ArrayList name=new ArrayList();
        ArrayList role=new ArrayList();
        ArrayList place=new ArrayList();
        ArrayList<Double>lat=new ArrayList();
        ArrayList<Double>lon=new ArrayList();
        ArrayList phone=new ArrayList();

        for(int i=0;i<number.size();i++){
            name.add(volunteer.get(number.get(i)).getFname());
            role.add(volunteer.get(number.get(i)).getRole());
            place.add(volunteer.get(number.get(i)).getPhone());
            lat.add(Double.valueOf(volunteer.get(number.get(i)).getLat()));
            lon.add(Double.valueOf(volunteer.get(number.get(i)).getLon()));
            phone.add(volunteer.get(number.get(i)).getPhone());
        }


        Intent intent = new Intent(assign_work.this, workAssign.class);
        Bundle args = new Bundle();
        args.putSerializable("nameARRAYLIST", name);
        intent.putExtra("name",args);
        args.putSerializable("roleARRAYLIST", role);
        intent.putExtra("role",args);
        args.putSerializable("placeARRAYLIST", place);
        intent.putExtra("place",args);
        args.putSerializable("latARRAYLIST", lat);
        intent.putExtra("lat",args);
        args.putSerializable("lonARRAYLIST", lon);
        intent.putExtra("lon",args);
        args.putSerializable("phoneARRAYLIST", phone);
        intent.putExtra("phone",args);
        startActivity(intent);
        finish();
    }

    private void show(final int id, final View view1) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(volunteer.get(id).fname);

        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.bottom_sheet_volunteerview_layout, null, true);


        TextView email = view.findViewById(R.id.mapemail);
        TextView phone = view.findViewById(R.id.mapphone);
        TextView role = view.findViewById(R.id.maprole);
        final Button selectvolunteer=view.findViewById(R.id.select);
        TextView name=view.findViewById(R.id.name);

        name.setText(volunteer.get(id).getFname());
        email.setText(volunteer.get(id).email);
        phone.setText(volunteer.get(id).phone);
        role.setText(volunteer.get(id).role);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + volunteer.get(id).phone));
                Toast.makeText(assign_work.this, "Connecting to " + volunteer.get(id).fname + " ...", Toast.LENGTH_LONG).show();
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                    return;
                }

            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",volunteer.get(id).getEmail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Work Assignment");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        selectvolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedcount=selectedcount+1;
                findViewById(R.id.go).setVisibility(View.VISIBLE);
                Toast.makeText(assign_work.this, selectedcount + " selected",Toast.LENGTH_LONG).show();
                number.add(id);
                select=false;
                view1.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                alert.hide();
            }
        });

        alert = builder.create();
        alert.setView(view);
        alert.show();

    }

    String location(double lat,double lon){


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(assign_work.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



            String city = addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();

            return  city+','+state;


        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }
}
