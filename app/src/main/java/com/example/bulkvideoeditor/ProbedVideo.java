package com.example.bulkvideoeditor;

public class ProbedVideo {

    private String fpath;
    private int localWidth;
    private int localHeight;
    private Enum<EditType> eType;


    public ProbedVideo(String filePath, int width, int height, Enum<EditType> editType) {
        fpath = filePath;
        localWidth = width;
        localHeight = height;
        eType = editType;
    }

    public String getFilePath() {
        return fpath;
    }


}
