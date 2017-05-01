package com.mad.tripster;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Chinmay Rawool on 4/30/2017.
 */

public class PlaceObject implements Serializable{
    String place_id;
    String place_name;
    double place_lat, place_lng;
    String address;

    public PlaceObject() {
    }

    public PlaceObject(String place_id, String place_name, double place_lat, double place_lng, String address) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.place_lat = place_lat;
        this.place_lng = place_lng;
        this.address = address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public double getPlace_lat() {
        return place_lat;
    }

    public void setPlace_lat(double place_lat) {
        this.place_lat = place_lat;
    }

    public double getPlace_lng() {
        return place_lng;
    }

    public void setPlace_lng(double place_lng) {
        this.place_lng = place_lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PlaceObject{" +
                "place_id='" + place_id + '\'' +
                ", place_name='" + place_name + '\'' +
                ", place_lat=" + place_lat +
                ", place_lng=" + place_lng +
                ", address='" + address + '\'' +
                '}';
    }
}
