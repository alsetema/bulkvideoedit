package com.example.bulkvideoeditor;


import android.content.ClipData;
import android.content.Intent;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;


import java.io.*;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private VideoView vw1;
    private TextView clipInfoView;
    private Button filePickMultiButton;
    final int CODE_PICKED_VIDEO = 2;
    final int CODE_SINGLE_VIDEO = 1;
    // FOR NOW ONLY MULTIPLE FILE, final int CODE_SINGLE_VIDEO = 1;
    private Toast errorToast;
    String[] texts = {"super cool", "amazing", "seems to be working!", "nice", "69", "420", "poop", "this is supposed to be a really long text to see how it would wrap arround the screen or what happens"};
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (errorToast == null) {
            errorToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
        }
        filePickMultiButton = findViewById(R.id.pickfile);
        vw1 = findViewById(R.id.vw1);
        clipInfoView = findViewById(R.id.clipinfo);


        filePickMultiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseFile();
            }
        });

    }

    //I changed the library thing to a new one so I need to re-write everythign the other library did

    public void choseFile() {
        Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
        filePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePicker.setType("video/mp4");
        startActivityForResult(filePicker, CODE_PICKED_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this can actually make me pick only one so I am happy
        if (requestCode == CODE_PICKED_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri testUri = null;
            ClipData cd = data.getClipData();


            if (cd != null) {
                //checking if more than one file was picked and then chosing one randomly
                testUri = cd.getItemAt(rand.nextInt(cd.getItemCount())).getUri();
                //this URI provided by the file picker doesnt seem to be too good for the library?

            } else if (cd == null) {
                //if the content data is null it means that the thing is PROBABLY only one video rather than an array of videos, could have
                //an array to hold all of these
                testUri = data.getData();
            }
            if(testUri != null) {
                probeVid(testUri);
            }
        }
    }


    private void probeVid(Uri videoURI) {
        // I think this was supposed to get the information on the video to check the resolution first, and then decide how to square it.
        //need to figure out how the fucking ffmeg library works

        //checking what the uri is a string
        System.out.println("the URI path is: " + videoURI.getPath());
        System.out.println("the URI string is: " + videoURI.toString());
        // so the URI doesnt work becuase of security. need to use content resolver
        //to make my own copy of the file thta I control, and put that one into ffmpeg (i know
        //the location of that one)
        //hardcoded thing shit uri value for test
        try {
            String filename = FileFunc.uriToFile(videoURI, getCacheDir().toString(), this);
            String filepath = getCacheDir() + "/" + filename;
            Log.i("file path thing", filepath);
            //System.out.println("file path thing "+ filepath);
            MediaInformation info = FFprobe.getMediaInformation(filepath);
            System.out.println(info.getFormat());

            vw1.setVideoURI(Uri.fromFile(new File(filepath)));
            vw1.start();
            //dont print stuff, use logcat
        } catch (FileNotFoundException e) {
            Log.e("EXCEPTION", "what the fuck this failed :)))) (file not found)");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("EXCEPTION", "this is a IO exception thrown at trying to uritofile");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("EXCEPTION,RANDOM", "you're fucked, random exception");
        }


    }


    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
