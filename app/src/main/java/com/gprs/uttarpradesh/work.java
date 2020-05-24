package com.gprs.uttarpradesh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class work extends AppCompatActivity {

    private String name1,role1,place1,phone1,email1,work;
    private String time1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentDateTime = dateFormat.format(new Date()); // Find todays date
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();

        Intent intent=getIntent();
        time1=intent.getStringExtra("time");
        name1=intent.getStringExtra("name");
        role1=intent.getStringExtra("role");
        place1=intent.getStringExtra("place");
        email1=intent.getStringExtra("email");
        phone1=intent.getStringExtra("phone");
        work=intent.getStringExtra("work");

        TextView time=findViewById(R.id.time123);
        time.setText(time1);
        TextView name=findViewById(R.id.name);
        name.setText(name1);
        TextView place=findViewById(R.id.place);
        place.setText(place1);
        TextView phone=findViewById(R.id.phone);
        phone.setText(phone1);
        TextView email=findViewById(R.id.email);
        email.setText(email1);
        TextView work1=findViewById(R.id.work);
        work1.setText(work);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone1));
                Toast.makeText(work.this, "Connecting to " +name1+ " ...", Toast.LENGTH_LONG).show();
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                    return;
                }

            }
        });
                if(!email.getText().toString().isEmpty()) {
                    email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", email1, null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Work Assignment");
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    });
                }

        Button done,reject;

        done=findViewById(R.id.done);
        reject=findViewById(R.id.reject);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkdone();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkreject();
            }
        });


    }

    private void show(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(work.this,your_work.class));
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void checkdone() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to mark this work as done ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("Works").child(pref.getString("user","")).child(time1).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Notification").child(phone1).child("Work").child(currentDateTime).setValue("Work : "+work+"\nis marked as done .");
                show("Work marked as done successfully !");
                Toast.makeText(work.this,"Work marked as done",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void checkreject() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to reject this work ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("Works").child(pref.getString("user","")).child(time1).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Notification").child(phone1).child("Work").child(currentDateTime).setValue("Work : "+work+"\nis rejected by the user.");
                show("Work rejected successfully !");
                Toast.makeText(work.this,"Work marked as rejected",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

}
