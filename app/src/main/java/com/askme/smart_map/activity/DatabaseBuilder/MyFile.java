package com.askme.smart_map.activity.DatabaseBuilder;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import io.nlopez.clusterer.Clusterable;

public class MyFile implements Clusterable {
    private String id;
    private String title;
    private String description;
    private String type;
    private String date;
    private String path;
    private String location;
    private String locationLocalName;
    private Context context;


    public MyFile(String id, String title, String description, String type, String date, String path, String location, String locationLocalName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.date = date;
        this.path = path;
        this.location = location;
        this.locationLocalName = locationLocalName;


    }

    public MyFile() {

    }

    public MyFile(String[] info) {

        this.id = info[0];
        this.title = info[1];
        this.description = info[2];
        this.type = info[3];
        this.date = info[4];
        this.path = info[5];
        this.location = info[6];
        this.locationLocalName = info[7];
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String[] getData() {
        String[] data = new String[8];
        data[0] = id;
        data[1] = title;
        data[2] = description;
        data[3] = type;
        data[4] = date;
        data[5] = path;
        data[6] = location;
        data[7] = locationLocalName;
        return data;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationLocalName() {
        return locationLocalName;
    }

    public void setLocationLocalName(String locationLocalName) {
        this.locationLocalName = locationLocalName;
    }

    @Override
    public LatLng getPosition() {

        String latLong[] = location.split(",");
        LatLng locationLatLng = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));
        return locationLatLng;
    }
}
