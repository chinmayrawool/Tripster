package com.mad.tripster;

/**
 * Created by Chinmay Rawool on 4/20/2017.
 */

public class Trip {
    public String title;
    public String location;
    public String image_id;
    public String image_url;
    public String created_by;
    public String trip_id;

    public Trip() {
    }

    public Trip(String title, String location, String image_id, String image_url, String created_by,String trip_id) {
        this.title = title;
        this.location = location;
        this.image_id = image_id;
        this.image_url = image_url;
        this.created_by = created_by;
        this.trip_id = trip_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", image_id='" + image_id + '\'' +
                ", image_url='" + image_url + '\'' +
                ", created_by='" + created_by + '\'' +
                ", trip_id='" + trip_id + '\'' +
                '}';
    }
}
