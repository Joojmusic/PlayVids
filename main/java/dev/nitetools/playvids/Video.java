package dev.nitetools.playvids;

import android.graphics.Bitmap;

public class Video {

    private String title, duration, path;
    Bitmap thumbnail;

    public Video(String title, String duration, String path, Bitmap thumbnail) {
        this.title = title;
        this.duration = duration;
        this.path = path;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
