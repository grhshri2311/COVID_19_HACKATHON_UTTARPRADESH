package com.gprs.uttarpradesh;


public class workhelper {

    String fname;
    String email;
    String place;
    String role;
    String phone;
    String work;

    public workhelper(String fname, String place, String role, String email, String phone, String work) {
        this.fname = fname;
        this.place=place;
        this.role = role;
        this.email=email;
        this.phone=phone;
        this.work=work;
    }

    public workhelper() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }


    public String getWork() {
        return work;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
