package com.gprs.uttarpradesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class publichealthcare extends AppCompatActivity {

    ArrayList<String> district,hospital,type,district1,hospital1,type1;
    SearchView searchView;
    ListView listView;
    CustomPubliccareAdapter customPubliccareAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publichealthcare);

        searchView=findViewById(R.id.search);
        listView=findViewById(R.id.list);


        String dis[]={"Agra",
                "Mathura",
                "Firozabad",
                "Firozabad",
                "Mainpuri",
                "Aligarh",
                "Etah",
                "Hathras",
                "Kasganj",
                "Azamgarh",
                "Ballia",
                "Amethi",
                        "Sultanpur",
                        "Ambedkarnagar",
                        "Ayodhya",
                        "Ayodhya",
                        "Ayodhya",
                        "Barabanki",
                        "Bareilly",
                        "Badaun",
                        "Pilibhit",
                        "Shahjahanpur",
                        "Basti",

                        "Siddhartha Nagar",

                        "Santkabir Nagar",
                        "Chitrakoot",

                        "Hamirpur",

                        "Mahoba",

                        "Banda",
                        "Shrawasti",

                        "Balrampur",
                        "Bahraich",

                        "Gonda",
                        "Gonda",
                        "Gorakhpur",
                        "Gorakhpur",
                        "Deoria",
                        "Mahrajganj",

                        "Kushinagar",
                        "Jalaun",

                        "Jhansi",
                        "Jhansi",
                        "Lalitpur",

                        "Kannauj",
                        "Etawah",

                        "Auraiya",

                        "Farrukhabad",

                        "Kanpur Dehat",

                        "Kanpur Nagar",


                        "Lucknow",
                        "Lucknow",
                        "Lucknow",


                        "Unnao",

                        "Hardoi",

                        "Sitapur",

                        "Raibareili",

                        "LakhimpurKhiri",


                        "Bagpat",
                        "Bulandshahr",
                        "Bulandshahr",

                        "Meerut",
                        "GB Nagar",
                        "Ghaziabad",

                        "Hapur",

                        "Rampur",
                        "Sambhal",


                        "Bijnor",
                        "Moradabad",
                        "Amroha",


                        "Prayagraj",
                        "Prayagraj",
                        "Prayagraj",
                        "Kaushambi",

                        "Pratapgarh",

                        "Fatehpur",

                        "Shamli",
                        "Muzaffarnagar",

                        "Saharanpur",


                        "Mirzapur",
                        "Bhadohi",

                        "Sonbhadra",
                        "Ghazipur",


                        "Chandauli",

                        "Jaunpur",
                        "Varanasi\n"
        };

        String hos[]={"CHC Baroli Ahir",
                "CHC Vrindavan"  ,
                "CHC Jasrana",
                "CHC Deedamai",
                "CHC Bhogon",
                "CHC Harduaganj",
                "CHC Baghwala,",
                "CHC Mursan",
                "DCH soron",
                "CHC-Kolhukhor",
                "CHC Basantpur",
                "CHC Gauriganj",
                        "CHC Kurwar",
                        "CHC jalalpur",
                        "Mashudha",
                        "100 Bedded Kumarganj",
                        "Hospital",
                        "Old Building District",
                        "Women Hospital",
                        "CHC Satarik",
                        "Bithrichainpur",
                        "CHC Ujhani",
                        "CHC Jahanabad",
                        "CHC-Dadraul",
                        "Munderwa",

                        "CHC Birdpur",

                        "CHC Khalilabad",
                        "CHC Shiv Rampur",

                        "CHC Kurara",

                        "CHC Panwari",

                        "CHC Naraini",
                        "CHC Bhangha",

                        "Memorial Hospital",
                        "CHC Chittaura",

                        "Railway Hospital",
                        "Pandri kripal",
                        "CHC, Chargawan",
                        "LNM Railway Hospital",
                        "Guari Bazar",
                        "CHC Mithaura",

                        "CHC Sapaha",
                        "CHC  Konch",

                        "CHC Badagaon",
                        "Railway Hospital",
                        "CHC Talbehat",

                        "CHC Tirwa",
                        "CHC Jaswant Nagar",

                        "MCH wing CHC Dibiyapur,",

                        "CHC Baraun",

                        "CHC Gajner",

                        "CHC Sarsaul",


                        "RSM 100 BED DCH BKT",
                        "CHC Malihabad",
                        "CHC Mohanlalganj",


                        "CHC, Bichhiya",

                        "CHC Bawan",

                        "CHC Khairabad",

                        "CHC ROHANIYA",

                        "Bhejam",


                        "Khekra",
                        "SSMJ KHURJA",
                        "JP Hospital Anupshahar",

                        "CHC- Jani Khurd",
                        "CHC Bisrakh",
                        "CHC Muradnagar",

                        "CHC Hapur",

                        "CHC Milak",
                        "CHC Narauli",


                        "CHC  Nazibabad",
                        "DWC Hospital",
                        "Old building of DCH",


                        "CHC Kotwa at Bani-",
                        "CRPF Camp Hospital",
                        "Central Hospital Railways",
                        "PHC Manjhanpur- Vistar Patal",

                        "Trauma Centre sadar",

                        "CHC Thariyaon",

                        "CHC Jhinjhna",
                        "CHC Makhiyall",

                        "CHC Fatehpur",


                        "CHC Vindhyachal Mirzapur",
                        "CHC Bhadohi",

                        "CHC Madhupur",
                        "CHC, mohamadabad",


                        "CHC Bhogawar",

                        "CHC",

                        "UCHC Shivpur\n"
        };




        district=new ArrayList<String>(Arrays.asList(dis));
        String typ[]=new String[district.size()];

        for(int i=0;i<district.size();i++){
            typ[i]="L1";
        }
        hospital=new ArrayList<String>(Arrays.asList(hos));
        type=new ArrayList<String>(Arrays.asList(typ));
        district1=new ArrayList<>(district);
        type1=new ArrayList<>(type);
        hospital1=new ArrayList<>(hospital);

        customPubliccareAdapter =new CustomPubliccareAdapter(this,hospital1,district1,type1);
        listView.setAdapter(customPubliccareAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                hospital1.clear();
                district1.clear();
                type1.clear();

                int i;
                for(i=0;i<hospital.size();i++){
                    if(i<hospital.size() && i<district.size() && i<type.size())
                        if(hospital.get(i).toLowerCase().contains(query.toLowerCase())){
                            hospital1.add(hospital.get(i));
                            district1.add(district.get(i));
                            type1.add(type.get(i));
                        }
                }
                if(hospital1.size()!=0){
                    customPubliccareAdapter.notifyDataSetChanged();
                    Toast.makeText(publichealthcare.this, String.valueOf(hospital1.size())+" results found",Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(publichealthcare.this, String.valueOf(i)+"No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

    }
}
