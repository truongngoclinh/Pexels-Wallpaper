package com.dpanic.wallz.pexels.data.model;

import java.util.ArrayList;

/**
 * Created by dpanic on 10/7/2016.
 * Project: Pexels
 */

public class ImageDetail {
    private String title;
    private String author;
    private String avatarUrl;
    private String dimen;
    private String size;
    private ArrayList<String> colors;
    private ArrayList<String> tags;


    public ImageDetail(String title, String author, String avatarUrl, String dimen, String size, ArrayList<String>
            colors,
                       ArrayList<String> tags) {
        setTitle(title);
        setAuthor(author);
        setAvatarUrl(avatarUrl);
        setDimen(dimen);
        setSize(size);
        setColors(colors);
        setTags(tags);
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    private void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    private void setAuthor(String author) {
        this.author = author;
    }

    public String getDimen() {
        return dimen;
    }

    public void setDimen(String dimen) {
        this.dimen = dimen;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    private void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    private void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
