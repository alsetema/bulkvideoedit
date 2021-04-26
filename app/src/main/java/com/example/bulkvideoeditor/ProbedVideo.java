package com.example.bulkvideoeditor;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import com.arthenica.ffmpegkit.FFmpegKitConfig;

import java.io.File;
import java.util.Random;

public class ProbedVideo {

    private Uri uri;
    private int localWidth;
    private int localHeight;
    private EditType eType;


    public ProbedVideo(Uri fileUri, int width, int height, EditType editType) {
        uri = fileUri;
        setHeight(height);
        setWidth(width);
        eType = editType;
    }

    public String getReadFilePath(Context ct) {
        return FFmpegKitConfig.getSafParameterForRead(ct, this.uri);
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

    //will return a random value for good measure
    public String genFileRand() {
        Random rd = new Random();
        int bound = 100000;
        return String.format("%06", rd.nextInt(bound));
    }

}
