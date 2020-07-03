package com.gprs.uttarpradesh;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;


public class fragment_publiccaremaptview extends Fragment implements OnMapReadyCallback {

    private UiSettings mUiSettings;
    private GoogleMap mMap;

    MapView mapView;
    ArrayList<String> name, district, type;

    ArrayList<Pair<Double, Double>> arrayString;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_publichealthmapview, container, false);

        String[] dis = {"Agra",
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

        String[] hos = {"CHC Baroli Ahir",
                "CHC Vrindavan",
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


        name = new ArrayList<>(Arrays.asList(hos));
        district = new ArrayList<String>(Arrays.asList(dis));
        type = new ArrayList<>();
        for (int i = 0; i < name.size(); i++) {
            type.add("");
        }

        arrayString = new ArrayList<>();
        String string = "[{\"first\":27.1533943,\"second\":78.42430809999999},{\"first\":27.2544971,\"second\":79.1691084},{\"first\":27.9450681,\"second\":78.15501800000001},{\"first\":27.882096299999997,\"second\":78.74024039999999},{\"first\":27.570208599999997,\"second\":77.9439563},{\"first\":27.886626,\"second\":78.7447868},{\"first\":25.956494,\"second\":83.2548083},{\"first\":26.480523599999998,\"second\":81.4367735},{\"first\":26.2010859,\"second\":81.69612769999999},{\"first\":26.3354704,\"second\":81.9932199},{\"first\":26.454207099999998,\"second\":82.7680906},{\"first\":26.7068142,\"second\":82.1336324},{\"first\":26.541247400000003,\"second\":81.8319574},{\"first\":26.7981564,\"second\":80.9015873},{\"first\":27.2387569,\"second\":79.05192799999999},{\"first\":27.1971529,\"second\":77.99592919999999},{\"first\":26.8591233,\"second\":81.1954181},{\"first\":28.339683299999997,\"second\":79.5062408},{\"first\":27.998079699999998,\"second\":79.011912},{\"first\":28.6322665,\"second\":79.70972019999999},{\"first\":27.8321378,\"second\":79.8719617},{\"first\":26.8534147,\"second\":82.40619749999999},{\"first\":27.373657299999998,\"second\":83.114925},{\"first\":26.7671755,\"second\":83.03613759999999},{\"first\":25.7488566,\"second\":84.23973400000001},{\"first\":25.9863261,\"second\":80.0367259},{\"first\":25.426938,\"second\":79.4641984},{\"first\":25.184134099999998,\"second\":80.4709283},{\"first\":27.822825899999998,\"second\":81.77424789999999},{\"first\":25.4467284,\"second\":81.8536166},{\"first\":27.121056900000003,\"second\":78.0593131},{\"first\":28.6556351,\"second\":77.43093449999999},{\"first\":26.8066878,\"second\":83.3818885},{\"first\":26.766178,\"second\":83.378772},{\"first\":26.5958962,\"second\":83.67074629999999},{\"first\":27.2395852,\"second\":83.6747342},{\"first\":26.9384567,\"second\":83.82078400000002},{\"first\":26.0047669,\"second\":79.1604971},{\"first\":28.8713322,\"second\":77.27737069999999},{\"first\":28.6556351,\"second\":77.43093449999999},{\"first\":25.0431752,\"second\":78.4327088},{\"first\":26.9572,\"second\":79.78860000000002},{\"first\":26.8744985,\"second\":78.91087809999999},{\"first\":26.6296479,\"second\":79.55123189999999},{\"first\":24.8356405,\"second\":81.3635582},{\"first\":26.2334326,\"second\":80.195605},{\"first\":26.4974556,\"second\":80.2424233},{\"first\":26.8467088,\"second\":80.9461592},{\"first\":27.0214058,\"second\":80.7415254},{\"first\":26.685845999999998,\"second\":80.9821942},{\"first\":26.535180099999998,\"second\":80.6339229},{\"first\":27.401233299999998,\"second\":80.02355349999999},{\"first\":27.530658799999998,\"second\":80.7453796},{\"first\":25.895951099999998,\"second\":81.2973325},{\"first\":27.882628,\"second\":80.63685389999999},{\"first\":28.864471699999996,\"second\":77.2806207},{\"first\":28.248980099999997,\"second\":77.854903},{\"first\":28.3409937,\"second\":78.2676128},{\"first\":28.971620700000003,\"second\":77.6123245},{\"first\":28.5717353,\"second\":77.43675019999999},{\"first\":28.771514099999997,\"second\":77.5131013},{\"first\":28.728496,\"second\":77.8021519},{\"first\":28.537769599999997,\"second\":79.20012659999999},{\"first\":28.584100399999997,\"second\":78.5719587},{\"first\":28.755275999999995,\"second\":77.286368},{\"first\":26.8467088,\"second\":80.9461592},{\"first\":26.8467088,\"second\":80.9461592},{\"first\":25.448001299999998,\"second\":82.00528829999999},{\"first\":26.728282099999998,\"second\":80.8971858},{\"first\":25.447844399999997,\"second\":81.825681},{\"first\":25.4608435,\"second\":81.2796219},{\"first\":29.456425,\"second\":77.714186},{\"first\":25.8319892,\"second\":81.01226799999999},{\"first\":29.5263557,\"second\":77.2270746},{\"first\":26.8467088,\"second\":80.9461592},{\"first\":25.935726100000004,\"second\":80.8131192},{\"first\":25.162236399999998,\"second\":82.5050566},{\"first\":25.385454799999998,\"second\":82.5848994},{\"first\":24.676855999999997,\"second\":83.2298555},{\"first\":25.615299999999998,\"second\":83.7443},{\"first\":25.6489489,\"second\":82.32121389999999},{\"first\":25.5349112,\"second\":81.8312781}]";

        try {
            JSONArray jsonArray = (JSONArray) new JSONTokener(string).nextValue();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                arrayString.add(new Pair<Double, Double>(jsonObject.optDouble("first"), jsonObject.optDouble("second")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        getall();

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    void getall() {


        mMap.clear();
        LatLng latLng = null;
        for (int i = 0; i < arrayString.size() && i < name.size() && i < district.size() && i < type.size(); i++) {
            latLng = new LatLng(arrayString.get(i).first, arrayString.get(i).second);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name.get(i))
                    .snippet(type.get(i))
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_place_black_24dp)));
            marker.showInfoWindow();
            CustomInfoWindow customInfoWindow = new CustomInfoWindow(getActivity());
            mMap.setInfoWindowAdapter(customInfoWindow);
            marker.showInfoWindow();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    view(marker.getTitle());
                }
            });
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }


    private void view(String x) {

        for (int i = 0; i < name.size(); i++) {
            if (name.get(i).equals(x)) {
                Bundle bundle = new Bundle();
                bundle.putString("name", name.get(i));
                bundle.putString("type", type.get(i));
                bundle.putString("district", district.get(i));

                BottomSheetDialogFragment f = new Bottomsheetpubliccarefragment();
                f.setArguments(bundle);
                f.show(getActivity().getSupportFragmentManager(), "Dialog");
                break;
            }
        }


    }
}
