package com.example.bulkvideoeditor;

import android.provider.MediaStore;

public class ProbedVideo {

    private String fpath;
    private int localWidth;
    private int localHeight;
    private EditType eType;


    public ProbedVideo(String filePath, int width, int height, EditType editType) {
        fpath = filePath;
        setHeight(height);
        setWidth(width);
        eType = editType;
    }

    public String getFilePath() {
        return fpath;
    }

    private void setWidth(int width) {
        if (width >= 20000) {
            localWidth = 20000;
        } else if (width <= 1) {
            localWidth = 1;
        } else {
            localWidth = width;
        }
    }

    private void setHeight(int height) {
        if (height >= 20000) {
            localHeight = 20000;
        } else if (height <= 1) {
            localHeight = 1;
        } else {
            localHeight = height;
        }
    }

    public int getHeight() {
        return  localHeight;
    }

    public int getWidth() {
        return  localWidth;
    }

    public EditType getEditType () {
        return eType;
    }

    //returns the filename without extension
    public String getFileName() {
        String[] subdirs = fpath.split("/");
        return subdirs[subdirs.length-1].split("\\.")[0]; //split on literal dot for only filename
    }

}
