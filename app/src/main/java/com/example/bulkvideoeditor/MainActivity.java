package com.example.bulkvideoeditor;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;


import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FFmpeg ffmpeg = null;
    private VideoView vw1;
    private TextView clipInfoView;
    private Button filePickButton;
    final int CODE_MULTIPLE_VIDEO = 1;
    private FFprobe ffprobe = null;
    final Toast errorToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    String[] texts = {"super cool", "amazing", "seems to be working!", "nice", "69", "420", "poop", "this is supposed to be a really long text to see how it would wrap arround the screen or what happens"};
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePickButton = findViewById(R.id.pickfile);
        vw1 = findViewById(R.id.vw1);
        clipInfoView = findViewById(R.id.clipinfo);

        loadLibs();

        filePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               choseFile();
            }
        });

    }

    public void choseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("video/mp4");
        startActivityForResult(intent, CODE_MULTIPLE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CODE_MULTIPLE_VIDEO && resultCode == RESULT_OK) {
            ClipData cd = data.getClipData();

            if (cd != null) {
                vw1.setVideoURI(cd.getItemAt(rand.nextInt(cd.getItemCount())).getUri());
                vw1.start();
            }
        }

    }

    private void probeVid

    public void ffprobeLoad() {
        Context appContext = getApplicationContext();
        //check for ffprobe
        if(FFprobe.getInstance(this).isSupported()) {
            ffprobe = ffprobe.getInstance(this);
        } else {
            String text = "Error loading FFprobe library";
            errorToast.setText(text);
            errorToast.show();
            finish();
        }
    }

    public void ffmpegLoad() {
        Context appContext = getApplicationContext();
        // first check for ffmpeg availability
        if (FFmpeg.getInstance(this).isSupported()) {
            ffmpeg = FFmpeg.getInstance(this);

            //String text = "Success loading the FFmpeg Library!";
           // final Toast loadSuccess = Toast.makeText(appContext,text,Toast.LENGTH_LONG);
            //loadSuccess.show();
            //testing the exiting of the app anyway
            //sleep(1000);
            //finish();

        } else {
            String text = "Error Loading the FFmpeg Library!";
            errorToast.setText(text);
            errorToast.show();
            finish();
        }

    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadLibs() {
        ffmpegLoad();
        ffprobeLoad();
    }

}
