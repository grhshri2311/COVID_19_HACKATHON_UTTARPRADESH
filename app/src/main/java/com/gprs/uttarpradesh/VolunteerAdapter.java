package com.gprs.uttarpradesh;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class VolunteerAdapter extends ArrayAdapter {

    private  ArrayList<String> name,role,place;
   private Activity context;


    public VolunteerAdapter(Activity context, ArrayList name, ArrayList role, ArrayList place ) {
        super(context,R.layout.volunteers,name);
        this.context=context;
       this.name=name;
       this.role=role;
       this.place=place;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;

            rowView = inflater.inflate(R.layout.volunteers, null, true);
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.name);
            TextView infoTextField = rowView.findViewById(R.id.role);
            TextView textView = rowView.findViewById(R.id.place);


            nameTextField.setText(name.get(position));
            infoTextField.setText(role.get(position));
            textView.setText(place.get(position));



        return rowView;

    }


}
