package com.gprs.uttarpradesh;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class CustomPubliccareAdapter extends ArrayAdapter {
    private ArrayList name,type,district;
    private Activity context;


    public CustomPubliccareAdapter(Activity context,ArrayList name,ArrayList district,ArrayList type) {
        super(context,R.layout.labsitem,name);
        this.context=context;
        this.name=name;
        this.district=district;
        this.type=type;

    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;

        rowView = inflater.inflate(R.layout.publiccareitem, null, true);


            if(position<name.size() && position<district.size() && position<type.size()) {
                ImageView logo=rowView.findViewById(R.id.logo);
                if(position%2==1)
                    logo.setBackground(rowView.getResources().getDrawable(R.drawable.textviewbackground1));

                TextView name1 = rowView.findViewById(R.id.name);
                TextView type1 = rowView.findViewById(R.id.type);
                TextView type2 = rowView.findViewById(R.id.type1);
                TextView getdirection = rowView.findViewById(R.id.getdirection);

                name1.setText((String) name.get(position));
                type1.setText((String) district.get(position));
                type2.setText((String) type.get(position));

                getdirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, pdfViewer.class);
                        intent.putExtra("text", "https://www.google.com/maps/search/" + name.get(position));
                        context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
                    }
                });

            }

        return rowView;

    }


}


