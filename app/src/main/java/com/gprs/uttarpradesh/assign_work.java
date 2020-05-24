package com.gprs.uttarpradesh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class assign_work extends AppCompatActivity {


    Button role,filter;
    ArrayList<String> namelist, rolelist, placelist;
    ArrayList<UserLocationHelper> volunteer;
    ListView listView;
    VolunteerAdapter adapter;
    EditText place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_work);

        namelist = new ArrayList<>();
        rolelist = new ArrayList<>();
        placelist = new ArrayList<>();
        listView = findViewById(R.id.volunteers);
        place=findViewById(R.id.place);
        filter=findViewById(R.id.filter);
        role = findViewById(R.id.role);

        volunteer = new ArrayList<>();
        adapter = new VolunteerAdapter(this, namelist, rolelist, placelist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show(position);
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
                                            if (location(userLocationHelper.getLat(), userLocationHelper.getLon()).toLowerCase().contains(place.getText().toString().toLowerCase())) {
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
                                            if (userLocationHelper.getRole().equals(role.getText().toString()) &&location(userLocationHelper.getLat(), userLocationHelper.getLon()).toLowerCase().contains(place.getText().toString().toLowerCase())) {
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
                rolemenu.getMenuInflater().inflate(R.menu.role1, rolemenu.getMenu());
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
                                            if (userLocationHelper.getRole().equals(item.getTitle().toString())) {
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

        FirebaseDatabase.getInstance().getReference().child("Location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);
                            if(!userLocationHelper.getRole().equals("Monitors")) {
                                volunteer.add(userLocationHelper);
                                namelist.add(userLocationHelper.getFname());
                                rolelist.add(userLocationHelper.getRole());
                                placelist.add(location(userLocationHelper.getLat(), userLocationHelper.getLon()));
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void show(final int id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(volunteer.get(id).fname);

        AlertDialog alert;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.volunteerview, null, true);


        TextView email = (TextView) view.findViewById(R.id.mapemail);
        TextView phone = (TextView) view.findViewById(R.id.mapphone);
        TextView role = (TextView) view.findViewById(R.id.maprole);
        Button select=view.findViewById(R.id.select);

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

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(assign_work.this,workAssign.class);
                intent.putExtra("name",volunteer.get(id).getFname());
                intent.putExtra("role",volunteer.get(id).getRole());
                intent.putExtra("place",location(volunteer.get(id).getLat(), volunteer.get(id).getLon()));
                intent.putExtra("phone",volunteer.get(id).getPhone());
                intent.putExtra("email",volunteer.get(id).getEmail());
                startActivity(intent);
                finish();
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
