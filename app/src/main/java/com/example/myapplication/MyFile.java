package com.example.myapplication;

public class MyFile {
    private String name;
    private String path;
    private int duration;

    public MyFile(String name, String path, int duration) {
        this.name = name;
        this.path = path;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getDuration() {
        return duration;
    }
}
