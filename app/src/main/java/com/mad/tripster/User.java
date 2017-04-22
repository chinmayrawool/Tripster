package com.mad.tripster;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chinmay Rawool on 4/20/2017.
 */

public class User {
    public String userfirstname;
    public String userlastname;
    public String email;
    public String gender;
    public String image_id;
    public String image_url;
    public String deleteMsg ="";
    public String joinedTrip="";
    public String user_id;

    public String getJoinedTrip() {
        return joinedTrip;
    }

    public void setJoinedTrip(String joinedTrip) {
        this.joinedTrip = joinedTrip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserfirstname() {
        return userfirstname;
    }

    public void setUserfirstname(String userfirstname) {
        this.userfirstname = userfirstname;
    }

    public String getUserlastname() {
        return userlastname;
    }

    public void setUserlastname(String userlastname) {
        this.userlastname = userlastname;
    }

    public String getDeleteMsg() {
        return deleteMsg;
    }

    public void setDeleteMsg(String deleteMsg) {
        this.deleteMsg = deleteMsg;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email",email);
        result.put("gender",gender);
        result.put("image_id",image_id);
        result.put("image_url",image_url);
        result.put("user_id",user_id);
        result.put("userfirstname",userfirstname );
        result.put("userlastname", userlastname);
        return result;
    }


    public User(String userfirstname, String userlastname, String email, String gender, String image_id, String image_url, String user_id) {
        this.userfirstname = userfirstname;
        this.userlastname = userlastname;
        this.email = email;
        this.gender = gender;
        this.image_id = image_id;
        this.image_url = image_url;
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "userfirstname='" + userfirstname + '\'' +
                ", userlastname='" + userlastname + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", image_id='" + image_id + '\'' +
                ", image_url='" + image_url + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}