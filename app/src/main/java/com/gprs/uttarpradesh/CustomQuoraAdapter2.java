package com.gprs.uttarpradesh;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomQuoraAdapter2 extends ArrayAdapter {

      ArrayList<String> name,answer;
        private Activity context;


        public CustomQuoraAdapter2(Activity context, ArrayList<String> name,ArrayList<String> answer) {
        super(context,R.layout.quoraanswer,name);
        this.context=context;
        this.name=name;
        this.answer=answer;
    }



    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;

            rowView = inflater.inflate(R.layout.quoraanswer, null, true);
            //this code gets references to objects in the listview_row.xml file
            final TextView name1 = rowView.findViewById(R.id.textView22);
            final TextView role1 = rowView.findViewById(R.id.textView24);
            final TextView place1 = rowView.findViewById(R.id.textView26);
            final TextView answer1 = rowView.findViewById(R.id.textView27);

            FirebaseDatabase.getInstance().getReference().child("Users").child(name.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserRegistrationHelper userRegistrationHelper=dataSnapshot.getValue(UserRegistrationHelper.class);
                    name1.setText("Name : "+userRegistrationHelper.getFname());
                    role1.setText("Role : "+userRegistrationHelper.getRole());
                    place1.setText("Place : "+location(userRegistrationHelper.getLat(),userRegistrationHelper.getLon()));
                    answer1.setText("Answer : "+answer.get(position));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        return rowView;


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    String location(double lat,double lon){


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

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
