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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class work extends AppCompatActivity {

    private String name1,role1,place1,phone1,email1,work;
    private String time1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String comment="";
    EditText commented;
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
        commented=findViewById(R.id.comment);





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
                FirebaseDatabase.getInstance().getReference().child("Workassign").child(phone1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WorkAssignHelper workAssignHelper = snapshot.getValue(WorkAssignHelper.class);
                            if(workAssignHelper.getPhone().equals(pref.getString("user","")) && workAssignHelper.getWork().equals(work)) {
                                workAssignHelper.setStatus("done");
                                if(!commented.getText().toString().equals("")){
                                    comment=commented.getText().toString();
                                }
                                workAssignHelper.setEmail(email1);
                                workAssignHelper.setFname(name1);
                                workAssignHelper.setPhone(phone1);
                                workAssignHelper.setRole(role1);
                                workAssignHelper.setPlace(place1);
                                workAssignHelper.setComment(comment);
                                FirebaseDatabase.getInstance().getReference().child("Workassign").child(phone1).child(snapshot.getKey()).setValue(workAssignHelper);
                                FirebaseDatabase.getInstance().getReference().child("Workdone").child(pref.getString("user","")).child(snapshot.getKey()).setValue(workAssignHelper);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                FirebaseDatabase.getInstance().getReference().child("Workassign").child(phone1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WorkAssignHelper workAssignHelper = snapshot.getValue(WorkAssignHelper.class);
                            if(workAssignHelper.getPhone().equals(pref.getString("user","")) && workAssignHelper.getWork().equals(work)) {
                                workAssignHelper.setStatus("rejected");
                                if(!commented.getText().toString().equals("")){
                                    comment=commented.getText().toString();
                                }
                                workAssignHelper.setEmail(email1);
                                workAssignHelper.setFname(name1);
                                workAssignHelper.setPhone(phone1);
                                workAssignHelper.setRole(role1);
                                workAssignHelper.setPlace(place1);
                                workAssignHelper.setComment(comment);
                                FirebaseDatabase.getInstance().getReference().child("Workassign").child(phone1).child(snapshot.getKey()).setValue(workAssignHelper);
                                FirebaseDatabase.getInstance().getReference().child("Workdone").child(pref.getString("user","")).child(snapshot.getKey()).setValue(workAssignHelper);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
