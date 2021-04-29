package com.example.bulkvideoeditor;

import java.text.SimpleDateFormat;
import java.time.Instant;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.TextView;
import com.arthenica.ffmpegkit.*;

public class Processor {

    private ArrayList<FFmpegSession> fFmpegSessions;
    private Context globalCt;
    private TextView logbox;



    public Processor (Context androidContext) {
        fFmpegSessions = new ArrayList<>();
        globalCt = androidContext;

    }
    
    public void process(ProbedVideo pv, TextView logBox) {
        logbox = logBox;
        switch (pv.getEditType()) {
            case SQUARE: {
                turnSquare(pv);
            }
            break;
            default:
                //do nothing IG? return error or crash or something
        }
        
    }

    private int cleanup() {
        int cleanedProcs = 0;
        for (FFmpegSession session: fFmpegSessions) {
            if(session.getReturnCode().isSuccess()) {
                //this wont work if im removing an object inside an iterator, just so I know
                fFmpegSessions.remove(session);
                cleanedProcs++;
            }
        }
        return cleanedProcs;
    }
    
    private void turnSquare(ProbedVideo pv) {

        //getting a file
        String outPath = FFmpegKitConfig.getSafParameterForWrite(globalCt,allocateFile(pv));

        if(pv.getHeight() > pv.getWidth()) {
            //do whatever if the video is vertical
            String args;
            args = "-i " + pv.getReadFilePath(globalCt) + " -filter_complex \"[v:0]split=2[tmp][og];" +
                    "[tmp]crop=h=iw:w=iw,boxblur=luma_radius=40:chroma_radius=20,scale=h="+pv.getHeight()+ ":" +
                    "w="+pv.getHeight()+"[tmp];[tmp][og] overlay=y=(H-h)/2:x=(W-w)/2\" "+ outPath;
            fFmpegSessions.add(FFmpegKit.executeAsync(args, new ExecuteCallback() {
                        @Override
                        public void apply(Session session) {
                            //finished
                            //remove it from the processing queue (its done
                            fFmpegSessions.remove(session);
                        }
                    }, new LogCallback() {
                        @Override
                        public void apply(Log log) {
                            if (logbox != null) {
                                logbox.append(log.toString());
                            }

                        }
                    }, new StatisticsCallback() {
                        @Override
                        public void apply(Statistics statistics) {

                        }
                    }
            ));

        } else if (pv.getWidth() > pv.getHeight()) {
            String args;
            args = "-i" + pv.getReadFilePath(globalCt) + " -filter_complex \"[v:0]split=2[tmp][og];" +
                    "[tmp]crop=h=ih:w=ih,boxblur=luma_radius=40:chroma_radius=20,scale=h="+pv.getWidth()+ ":" +
                    "w="+pv.getWidth()+"[tmp];[tmp][og] overlay=y=(H-h)/2:x=(W-w)/2\" " + outPath;
            // do whatver if the video is horizontal
        } else {
            //do nothing if the video is already 1:1
        }
    }

    private String genOutFilename(ProbedVideo pv) {
        return "bulk_video_" + pv.genFileRand() + "_" + Instant.now().getEpochSecond() + ".mp4";
    }

    private Uri allocateFile(ProbedVideo pv) {
        ContentResolver resolver = globalCt.getContentResolver();
        Uri videoCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentValues videoDetails = new ContentValues();
        //videoDetails.put(MediaStore.Video.Media.CONTENT_TYPE, "video/mp4");
        videoDetails.put(MediaStore.Video.Media.DISPLAY_NAME, genOutFilename(pv));

        return resolver.insert(videoCollection,videoDetails);
    }


}
