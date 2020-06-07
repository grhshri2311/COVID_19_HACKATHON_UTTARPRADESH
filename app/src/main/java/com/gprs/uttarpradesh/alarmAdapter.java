package com.gprs.uttarpradesh;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.abs;

class alarmAdapter extends ArrayAdapter {


    ArrayList<String> name,desc,time;
    ArrayList<Boolean> onoff;
    private Activity context;
    long mills = 0;
    long finalHrs=0,finalSeconds=0;
    int hours = 0,mins=0;
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd h:mm a");


    SimpleDateFormat dateFormat= new SimpleDateFormat("HH:MM");
    String currentDateTime = dateFormat.format(new Date());
    public alarmAdapter(ArrayList<String> name, ArrayList<String> desc, ArrayList<String> time, ArrayList<Boolean> onoff, Activity context) {
        super(context,R.layout.alarmitem,name);
        this.name = name;
        this.desc = desc;
        this.time = time;
        this.onoff=onoff;
        this.context = context;

    }


    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=null;
         rowView = inflater.inflate(R.layout.alarm_view, null, true);
            //this code gets references to objects in the listview_row.xml file
            final TextView time1=rowView.findViewById(R.id.time);
            TextView ampm=rowView.findViewById(R.id.time);

            TextView name1 = rowView.findViewById(R.id.name);
            final TextView onoff1= rowView.findViewById(R.id.onoff);
            final Switch aswitch = rowView.findViewById(R.id.switch1);

            if(position<name.size() && position<desc.size() && position<time.size()) {
                name1.setText(" | "+name.get(position));
                ampm.setText(time.get(position).substring(5,7));
                time1.setText(time.get(position).substring(0,5));
                if(onoff.get(position)){
                    aswitch.setChecked(true);
                }
                else
                    aswitch.setChecked(false);
            }

        String curtime=df.format(new Date());


        Date date1 = null,date2;
        try {

             date1 = df.parse(curtime);


             date2 = df.parse(curtime.substring(0,11)+time.get(position));

            long minutes = 0;

            if(date1.after(date2)) {

                long ltime=date2.getTime()+24*60*60*1000;
                String newdate=df.format(new Date(ltime));
                date2=df.parse(newdate);
                minutes = abs(((date1.getTime() / 1000) - (date2.getTime() /1000))/60) ;

            } else {

                minutes = abs(((date2.getTime() / 1000) - (date1.getTime() /1000))/60) ;

            }

            finalHrs = (minutes / 60);

            finalSeconds = minutes % 60;




        } catch (ParseException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }


        if(aswitch.isChecked()) {
            onoff1.setText("Alarm in " + finalHrs + " hours " + finalSeconds + " minutes");
            time1.setTextColor(Color.BLACK);
        }
        else {
            onoff1.setText("Off");
            time1.setTextColor(Color.GRAY);
        }






            aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked) {

                        String curtime=df.format(new Date());


                        Date date1 = null,date2;
                        try {

                            date1 = df.parse(curtime);


                            date2 = df.parse(curtime.substring(0,11)+time.get(position));

                            long minutes = 0;

                            if(date1.after(date2)) {

                                long ltime=date2.getTime()+24*60*60*1000;
                                String newdate=df.format(new Date(ltime));
                                date2=df.parse(newdate);
                                minutes = abs(((date1.getTime() / 1000) - (date2.getTime() /1000))/60) ;

                            } else {

                                minutes = abs(((date2.getTime() / 1000) - (date1.getTime() /1000))/60) ;

                            }

                            finalHrs = (minutes / 60);

                            finalSeconds = minutes % 60;




                        } catch (ParseException e) {

                            // TODO Auto-generated catch block

                            e.printStackTrace();

                        }

                        onoff.set(position,true);
                        time1.setTextColor(Color.BLACK);
                        Toast.makeText(context, "Alarm will go off in " + finalHrs + " hours and " + finalSeconds + " minutes.",Toast.LENGTH_LONG).show();
                        onoff1.setText("Alarm in "+finalHrs+" hours "+finalSeconds+" minutes");

                    }
                    else{
                        onoff1.setText("Off");
                        onoff.set(position,false);
                        Toast.makeText(context,"Alarm set Off",Toast.LENGTH_LONG).show();
                        time1.setTextColor(Color.GRAY);
                    }
                    saveMap();

                }
            });

         rowView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {

                 delete(position,alarmAdapter.this);
                 notifyDataSetChanged();
                 return true;
             }
         });


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view(position);
                }
            });

        return rowView;

    }

    private void saveMap(){
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(f);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(name);
            objectOutputStream.writeObject(desc);
            objectOutputStream.writeObject(time);
            objectOutputStream.writeObject(onoff);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String extStorageDirectory = Environment.getExternalStorageDirectory()
            .toString();
    File f=new File(extStorageDirectory + "/COVI19RELIEF/alarm/obj.dat");


    private void loadMap(){
        try {
            FileInputStream fileInputStream=new FileInputStream(f);
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            name=(ArrayList<String>)objectInputStream.readObject();
            desc=(ArrayList<String>)objectInputStream.readObject();
            time=(ArrayList<String>)objectInputStream.readObject();
            onoff=(ArrayList<Boolean>)objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    void view(int pos) {
        Bundle bundle=new Bundle();
        bundle.putString("name",name.get(pos));
        bundle.putString("time",time.get(pos));
        bundle.putBoolean("onoff",onoff.get(pos));
        bundle.putString("desc",desc.get(pos));


        BottomSheetDialogFragment f=new BottomsheetAlarmfragment();
        f.setArguments(bundle);
        FragmentActivity fr=(FragmentActivity) context;
        f.show(fr.getSupportFragmentManager(),"Dialog");
    }

    void delete(final int pos, final alarmAdapter alarmAdapter){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Delete Alarm");
        builder.setMessage("Do you want to delete alarm : "+name.get(pos)+" ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name.remove(pos);
                desc.remove(pos);
                time.remove(pos);
                onoff.remove(pos);
                saveMap();
                Toast.makeText(context,"Alarm deleted",Toast.LENGTH_LONG).show();
                alarmAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();



    }
}
