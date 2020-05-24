package com.gprs.uttarpradesh;

import java.util.ArrayList;


public class UserSelfAssessHelper {

    private int status;
    private ArrayList<String> answer=null;
    double lat,lon;


    public UserSelfAssessHelper(double lat, double lon, ArrayList<String> answer1, int status) {
        this.lat=lat;
        this.lon=lon;
        this.answer=answer1;
        this.status=status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserSelfAssessHelper(){

    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList< String> answer) {
        this.answer = answer;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
