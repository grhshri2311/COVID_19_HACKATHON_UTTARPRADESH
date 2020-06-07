package com.gprs.uttarpradesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class your_work extends AppCompatActivity {

    TextView title;
    ListView listView;
    ArrayList<String> time1,from,work;
    VolunteerAdapter1 adapter1;
    ArrayList<String> n,r,p,w,s,c;
    ListView listview1;
    ArrayAdapter adapter;
    ArrayList<workhelper> worklist;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_work);


        pref = getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode
        editor = pref.edit();

        title=findViewById(R.id.worktitle);
        listView=findViewById(R.id.yourwork);
        title.setText("Loading work");
        listview1=findViewById(R.id.volunteers1);
        time1=new ArrayList<>();
        from=new ArrayList<>();
        worklist=new ArrayList<>();
        work=new ArrayList<>();

        adapter=new VolunteerAdapter(this,time1,from,work);

        listView.setAdapter(adapter);

        n=new ArrayList<>();
        r=new ArrayList<>();
        p=new ArrayList<>();
        w=new ArrayList<>();
        s=new ArrayList<>();
        c=new ArrayList<>();


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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(your_work.this,work.class);
                intent.putExtra("time",time1.get(position));
                intent.putExtra("name",worklist.get(position).getFname());
                intent.putExtra("role",worklist.get(position).getRole());
                intent.putExtra("place",worklist.get(position).getPlace());
                intent.putExtra("phone",worklist.get(position).getPhone());
                intent.putExtra("email",worklist.get(position).getEmail());
                intent.putExtra("work",worklist.get(position).getWork());
                startActivity(intent);
                finish();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Works").child(pref.getString("user",""))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            title.setText("Your Works");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                time1.add(snapshot.getKey());
                                workhelper workhelper = snapshot.getValue(workhelper.class);
                                from.add(workhelper.getFname());
                                work.add(workhelper.getWork());
                                worklist.add(workhelper);
                                adapter.notifyDataSetChanged();
                            }
                            if(time1.size()<1)
                                title.setText("No Works assigned for you !");

                        } else {
                            title.setText("No Works assigned for you");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        title.setText("Error loading works");
                    }
                });


        FirebaseDatabase.getInstance().getReference().child("Workdone").child(pref.getString("user","")).addValueEventListener(new ValueEventListener() {
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
    }
}
