package com.gprs.uttarpradesh;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


class CustomChatBotAdapter extends ArrayAdapter {
    private Activity context;
    private ArrayList<Message> message;
    private ArrayList<Integer> toggle;
    private ArrayList<ArrayList<String>> option;

    public CustomChatBotAdapter(Activity context, ArrayList<Message> message, ArrayList<Integer> toggle, ArrayList<ArrayList<String>> option) {
        super(context,R.layout.activity_chatbot,message);
        this.context=context;
        this.message=message;
        this.toggle=toggle;
        this.option=option;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;

        if(message.get(position).getId().equals("2")) {
            rowView = inflater.inflate(R.layout.chat_item_watson, null, true);
            TextView textView=rowView.findViewById(R.id.message);
            textView.setText(message.get(position).getMessage());
            RelativeLayout relativeLayout=rowView.findViewById(R.id.their);
            Spinner spinner=rowView.findViewById(R.id.option);

            relativeLayout.removeView(spinner);
        }
        else {
            rowView = inflater.inflate(R.layout.chat_item_self, null, true);

            TextView textView=rowView.findViewById(R.id.message);
            textView.setText(message.get(position).getMessage());
        }

        return rowView;

    };


}


