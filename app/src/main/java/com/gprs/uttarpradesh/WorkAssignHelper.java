package com.gprs.uttarpradesh;


public class WorkAssignHelper {

    String fname;
    String email;
    String place;
    String role;
    String phone;
    String work;
    String status;
    String comment;

    public WorkAssignHelper(String fname, String place, String role, String email, String phone, String work,String status,String comment) {
        this.fname = fname;
        this.place=place;
        this.role = role;
        this.email=email;
        this.phone=phone;
        this.work=work;
        this.status=status;
        this.comment=comment;
    }

    public WorkAssignHelper() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
