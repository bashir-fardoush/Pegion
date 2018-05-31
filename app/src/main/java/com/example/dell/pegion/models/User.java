package com.example.dell.pegion.models;

/**
 * Created by DELL on 5/30/2018.
 */

public class User {
    private String name;
    private String status;
    private String imageUrl;
    private String thumbImageUrl;

    public User() {
    }

    public User(String name, String status, String imageUrl, String thumbImageUrl) {
        this.name = name;
        this.status = status;
        this.imageUrl = imageUrl;
        this.thumbImageUrl = thumbImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }
}
