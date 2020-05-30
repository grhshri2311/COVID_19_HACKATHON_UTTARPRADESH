package com.gprs.uttarpradesh;


import java.util.ArrayList;

public class QuoraHelper {

   String title,desc,message,role,phone,name,time;
   boolean nil,image,vedio;
   ArrayList<String> answer;
   ArrayList<String> user;
    String uri;



    public QuoraHelper() {
    }

    public QuoraHelper(String title, String desc, String message, String role, String phone, String name, String time, boolean nil, boolean image, boolean vedio, ArrayList<String> arrayList, String url, ArrayList<String> user) {
        this.title = title;
        this.desc = desc;
        this.message = message;
        this.role = role;
        this.phone = phone;
        this.name = name;
        this.time = time;
        this.nil=nil;
        this.image=image;
        this.vedio=vedio;
        this.answer=arrayList;
        this.uri=url;
        this.user=user;
    }

    public ArrayList<String> getUser() {
        return user;
    }

    public void setUser(ArrayList<String> user) {
        this.user = user;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public boolean isNil() {
        return nil;
    }

    public void setNil(boolean nil) {
        this.nil = nil;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public boolean isVedio() {
        return vedio;
    }

    public void setVedio(boolean vedio) {
        this.vedio = vedio;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
