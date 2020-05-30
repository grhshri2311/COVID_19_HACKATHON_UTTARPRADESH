package com.gprs.uttarpradesh;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter {

    private final ArrayList<ArrayList<String>> menu;
    ArrayList<String> question,answer;
    private final Integer[] toggle;
    private Activity context;
    ArrayList<String> s;
    private int n=0;


    public CustomAdapter(Activity context, ArrayList<ArrayList<String>> menu, Integer[] togle, ArrayList<String> questtion, ArrayList<String> answer) {
        super(context,R.layout.their_message,questtion);
        this.context=context;
        this.menu=menu;
        this.toggle=togle;
        this.answer=answer;
        this.question=questtion;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;
        if(toggle==null && answer==null && menu==null && question==null)
            return rowView;
        else if(toggle[position]==11 ){
            rowView = inflater.inflate(R.layout.their_message, null, true);
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.name);
            TextView infoTextField = rowView.findViewById(R.id.message_body);
            ImageView imageView = rowView.findViewById(R.id.avatar);

            final Spinner spinner = rowView.findViewById(R.id.answer);

           spinner.setVisibility(View.INVISIBLE);
            //this code sets the values of the objects to values from the arrays
            nameTextField.setText("Covid19Relief");
            infoTextField.setText(question.get(position));
            imageView.setImageResource(R.drawable.collective_intelligence_icon_use);

        }
        else if(toggle[position]==1 || toggle[position]==-1) {

            rowView = inflater.inflate(R.layout.their_message, null, true);

            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.name);
            TextView infoTextField = rowView.findViewById(R.id.message_body);
            ImageView imageView = rowView.findViewById(R.id.avatar);
            final RelativeLayout their=rowView.findViewById(R.id.their);

            final Spinner spinner = rowView.findViewById(R.id.answer);

            if(position<n)
                spinner.setVisibility(View.INVISIBLE);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner_item, R.id.txt_bundle, menu.get(position));
            spinner.setAdapter(dataAdapter);
            spinner.setGravity(11);
            spinner.setSelection(0, false);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    n=n+2;
                    their.removeView(spinner);
                    self_asses.answered((String) parent.getItemAtPosition(position), context);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(toggle[position]==-1) {
                spinner.setVisibility(View.INVISIBLE);
            }

            //this code sets the values of the objects to values from the arrays
            nameTextField.setText("Covid19Relief");
            infoTextField.setText(question.get(position));
            imageView.setImageResource(R.drawable.collective_intelligence_icon_use);
        }
        else {
            rowView = inflater.inflate(R.layout.my_message, null, true);

            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.message_body1);
            nameTextField.setText(answer.get(position));
        }
        return rowView;

    }

    @Override
    public boolean isEnabled(int position) {
        return position >= n;
    }
}
