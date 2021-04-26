package com.example.bulkvideoeditor;


import android.content.ClipData;
import android.content.Intent;

import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.arthenica.ffmpegkit.ExecuteCallback;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.FFprobeSession;
import com.arthenica.ffmpegkit.Session;



import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private VideoView vw1;
    private TextView clipInfoView;
    private Button processButton;
    private Button filePickMultiButton;
    final int CODE_PICKED_VIDEO = 2;
    private ArrayList<ProbedVideo> probedVideoQueue = new ArrayList<>();
    Processor videoProcessor = new Processor(MainActivity.this);


    private Toast errorToast;
    String[] texts = {"super cool", "amazing", "seems to be working!", "nice", "69", "420", "poop", "this is supposed to be a really long text to see how it would wrap arround the screen or what happens"};
    Random rand = new Random();
    private Toast infoToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (errorToast == null) {
            errorToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
        }
        if (infoToast == null) { //basically hasnt been initialised
            infoToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
        }
        filePickMultiButton = findViewById(R.id.pickfile);
        vw1 = findViewById(R.id.vw1);
        clipInfoView = findViewById(R.id.clipinfo);
        processButton = findViewById(R.id.processButton);


        filePickMultiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseFile();
            }
        });

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processQueue();
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

    public void processQueue() {
        if (probedVideoQueue.size() <= 0) {
            infoToast.setText("The processing queue is empty, nothing to do :)\n PD: add some files to process");
            infoToast.show();
        }
        else {
            clipInfoView.setMovementMethod(new ScrollingMovementMethod());
            for(ProbedVideo pv: probedVideoQueue) {

                videoProcessor.process(pv,clipInfoView);
                probedVideoQueue.remove(pv);
            }
        }
            //this shrould remove it from the queue and do something with it

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri> uriQueue = new ArrayList<>();
        //this can actually make me pick only one so I am happy
        if (requestCode == CODE_PICKED_VIDEO && resultCode == RESULT_OK && data != null) {
           // Uri testUri = null;
            ClipData cd = data.getClipData();


            if (cd != null) {
                //checking if more than one file was picked and then chosing one randomly
               // testUri = cd.getItemAt(rand.nextInt(cd.getItemCount())).getUri();
                //this URI provided by the file picker doesnt seem to be too good for the library?
                for(int counter = 0; counter < cd.getItemCount(); counter++) {
                    uriQueue.add(cd.getItemAt(counter).getUri());
                }

            } else if (cd == null) {
                //if the content data is null it means that the thing is PROBABLY only one video rather than an array of videos, could have
                //an array to hold all of these
                uriQueue.add(data.getData());
            }
            if(uriQueue.size() > 0) {
                //dequeue the uris into actual tmp files
                for (int ctr = 0; ctr < uriQueue.size(); ctr++) {
                    probeUri(uriQueue.get(ctr));
                }
                uriQueue.clear();
            }
        }
    }

    private void turnSquare(int width, int height, String filepath) {}

    private void probeUri(Uri videoURI) {
        // I think this was supposed to get the information on the video to check the resolution first, and then decide how to square it.
        //need to figure out how the fucking ffmeg library works

        //checking what the uri is a string
        //System.out.println("the URI path is: " + videoURI.getPath());
        // so the URI doesnt work becuase of security. need to use content resolver
        //to make my own copy of the file thta I control, and put that one into ffmpeg (i know
        //the location of that one)
        //hardcoded thing shit uri value for test
        try {
            String tempFilepath = FileFunc.uriToFile(videoURI, getCacheDir().toString(), this);
            Log.i("file path thing", tempFilepath);
            //System.out.println("file path thing "+ filepath);
            //select streams opt will select video 0 (the first video stream found in the mp4), show entries will show what was found, and the oput format csv=p=0 removes "print section" in theoutput

            String probeResult = FFprobeKit.execute("-i " + tempFilepath + " -print_format csv=p=0 -select_streams v:0 -show_entries stream=width,height").getOutput();

            String[] split = probeResult.split("\n");
            String finalLine = split[split.length-1];
            int width = Integer.parseInt(finalLine.split(",")[0]);
            int height = Integer.parseInt(finalLine.split(",")[1]);

            probedVideoQueue.add(new ProbedVideo(tempFilepath, width, height, EditType.SQUARE));
            clipInfoView.setText("" + probedVideoQueue.size());


           // vw1.setVideoURI(Uri.fromFile(new File(tempFilepath)));
           // vw1.start();
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
