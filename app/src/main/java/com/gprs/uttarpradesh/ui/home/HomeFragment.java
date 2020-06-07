package com.gprs.uttarpradesh.ui.home;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gprs.uttarpradesh.APIextract;
import com.gprs.uttarpradesh.AdapterHome;
import com.gprs.uttarpradesh.ApiClient;
import com.gprs.uttarpradesh.Home;
import com.gprs.uttarpradesh.Labsfortestingandresults;
import com.gprs.uttarpradesh.MSME;
import com.gprs.uttarpradesh.Model.Articles;
import com.gprs.uttarpradesh.Model.Headlines;
import com.gprs.uttarpradesh.R;
import com.gprs.uttarpradesh.UserLocationHelper;
import com.gprs.uttarpradesh.UserRegistrationHelper;
import com.gprs.uttarpradesh.UserSelfAssessHelper;
import com.gprs.uttarpradesh.advice;
import com.gprs.uttarpradesh.course;
import com.gprs.uttarpradesh.donate;
import com.gprs.uttarpradesh.pdfViewer;
import com.gprs.uttarpradesh.publichealthcare;
import com.gprs.uttarpradesh.victimalert;
import com.gprs.uttarpradesh.your_work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{
    RelativeLayout todo,helpline;
    TextView confirm,death,mystatus,mystatus1,mystatus2,todotext;
    ImageView todoimage;
    int total=0,sick=0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RelativeLayout scan,firstresponder;
    boolean flag=false;
    TextView  scrolltextview;
    View root;

    RecyclerView recyclerView;
    Button btnAboutUs;
    Dialog dialog;
    final String API_KEY = "81e919346ed94b8491dc88809d40d4eb";
    AdapterHome adapter;
    List<Articles>  articles = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        scrolltextview = root.findViewById(R.id.scrollingtextview);
        scrolltextview.setSelected(true);

        scan=root.findViewById(R.id.scan);
        todo=root.findViewById(R.id.todo);
        mystatus=root.findViewById(R.id.mystatus);
        mystatus1=root.findViewById(R.id.mystatus1);
        helpline=root.findViewById(R.id.helpline);
        mystatus2=root.findViewById(R.id.mystatus2);
        pref =root.getContext().getSharedPreferences("user", 0); //
        editor=pref.edit();

        todoimage=root.findViewById(R.id.todoimage);
        todotext=root.findViewById(R.id.todotext);
        ImageView shelter;
        LinearLayout faq,faq1;
        shelter=root.findViewById(R.id.shelter);
        faq=root.findViewById(R.id.faq);
        faq1=root.findViewById(R.id.faq1);
        shelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(), pdfViewer.class);
                intent.putExtra("text","https://drive.google.com/file/d/1mWap_8QEc3HUAiIpPM65K2rZiPjudMO1/view");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://drive.google.com/file/d/1GPoaMCIwbUdd3XDCzHnY_HiP7p2dVJ4x/view?usp=sharing");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        faq1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://drive.google.com/file/d/1A0mY4oMMoSMY5IeuhtKTWVvTkkdOy7lf/view?usp=sharing");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(), com.gprs.uttarpradesh.helpline.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());

            }
        });
        root.findViewById(R.id.getdirection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(), Labsfortestingandresults.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        root.findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://drive.google.com/file/d/1cEkR_bTmlz8se71Xtbtk1t4OlhLJYdPA/view?usp=sharing");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDateTime = dateFormat.format(new Date()); // Find todays date

        if(!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("today1","").equals(currentDateTime)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("today1",currentDateTime).apply();
                    Intent i = new Intent(getActivity(), advice.class);
                    startActivity(i);
                }
            }, 10000);
        }

        check();

        if(!androidx.preference.PreferenceManager.getDefaultSharedPreferences(root.getContext()).getBoolean("chatintro",false)) {
            root.findViewById(R.id.rellayout).setVisibility(View.VISIBLE);
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(root.getContext()).edit().putBoolean("chatintro",true).apply();
        }


        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.findViewById(R.id.rellayout).setVisibility(View.INVISIBLE);
            }
        });


        root.findViewById(R.id.donate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), donate.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));

        btnAboutUs = root.findViewById(R.id.aboutUs);
        dialog = new Dialog(root.getContext());


        final String country = getCountry();

        retrieveJson("covid uttar pradesh adityanath india",country,API_KEY);

        TextView seemoreupdates=root.findViewById(R.id.seemoreupdates);
        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        final  BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
        seemoreupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(bottomNav.getMenu().getItem(1).getItemId());
            }
        });



        confirm=root.findViewById(R.id.confirm);
        death=root.findViewById(R.id.death);

        APIextract apIextract=new APIextract(root.getContext(),confirm,death);



        ImageView i1,i2,i3,i4,i5;
        i1=root.findViewById(R.id.implink1);
        i2=root.findViewById(R.id.implink2);
        i3=root.findViewById(R.id.implink3);
        i4=root.findViewById(R.id.implink4);
        i5=root.findViewById(R.id.implink5);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://www.mohfw.gov.in/");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://www.nhp.gov.in/disease/communicable-disease/novel-coronavirus-2019-ncov");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://www.mohfw.gov.in/");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://www.icmr.gov.in/");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(root.getContext(),pdfViewer.class);
                intent.putExtra("text","https://nhm.gov.in/");
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        startActivity(new Intent(root.getContext(), your_work.class));

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pref.getString("status","").equals("victim"))
                    startActivity(new Intent(root.getContext(), victimalert.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                else {
                    Toast.makeText(root.getContext(),"You are found victim \nYou can't use this festure!",Toast.LENGTH_LONG).show();
                }
            }
        });

        firstresponder=root.findViewById(R.id.firstresponder);
        firstresponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), com.gprs.uttarpradesh.firstresponder.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        return root;
    }

    void check() {

        FirebaseDatabase.getInstance().getReference().child("Location").child(pref.getString("user","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserLocationHelper userLocationHelper1 = dataSnapshot.getValue(UserLocationHelper.class);
                if(userLocationHelper1!=null){

                    FirebaseDatabase.getInstance().getReference().child("Assess")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if(snapshot.getKey().equals(pref.getString("user",""))){
                                            continue;
                                        }
                                        final UserSelfAssessHelper userLocationHelper = snapshot.getValue(UserSelfAssessHelper.class);
                                        float[] results = new float[1];

                                        Location.distanceBetween(userLocationHelper.getLat(), userLocationHelper.getLon(),
                                                userLocationHelper1.getLat(), userLocationHelper.getLon(), results);
                                        if (results[0] < 1000) {
                                            total = total + 1;
                                            if (userLocationHelper.getStatus() >=3) {
                                                sick = sick + 1;
                                            }
                                        }


                                    }
                                    if(sick>0){
                                        mystatus.setText("You have to be Safe !\n");
                                        mystatus.setTextColor(getActivity().getResources().getColor(R.color.RED));
                                        mystatus1.setText("Total assessed : "+total);
                                        mystatus2.setText("Found Sick : "+sick+"\nWithin 1 KM radius");

                                    }
                                    else {
                                        mystatus.setText("You are Safe !\n");
                                        mystatus.setTextColor(getActivity().getResources().getColor(R.color.GREEN));
                                        mystatus1.setText("Total assessed : "+total);
                                        mystatus2.setText("Found Sick : "+sick+"\nWithin 1 KM radius");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });



                }
                else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    public void retrieveJson(String query ,String country, String apiKey){
        Call<Headlines> call= ApiClient.getInstance().getApi().getSpecificData(query,apiKey);

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    articles.clear();
                    articles = response.body().getArticles();
                    articles= articles.subList(0,5);
                    adapter = new AdapterHome(root.getContext(),articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                Toast.makeText(root.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }


}
