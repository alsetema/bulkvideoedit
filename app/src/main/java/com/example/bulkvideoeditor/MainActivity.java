package com.example.bulkvideoeditor;


import android.content.ClipData;
import android.content.Intent;

import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;


import java.io.*;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private VideoView vw1;
    private TextView clipInfoView;
    private Button filePickButton;
    final int CODE_MULTIPLE_VIDEO = 2;
    // FOR NOW ONLY MULTIPLE FILE, final int CODE_SINGLE_VIDEO = 1;
    private Toast errorToast;
    String[] texts = {"super cool", "amazing", "seems to be working!", "nice", "69", "420", "poop", "this is supposed to be a really long text to see how it would wrap arround the screen or what happens"};
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(errorToast == null) {
            errorToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
        }
        filePickButton = findViewById(R.id.pickfile);
        vw1 = findViewById(R.id.vw1);
        clipInfoView = findViewById(R.id.clipinfo);



        filePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               choseFile();
            }
        });

    }

    //I changed the library thing to a new one so I need to re-write everythign the other library did

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
                Uri testUri = cd.getItemAt(rand.nextInt(cd.getItemCount())).getUri();
                //this URI provided by the file picker doesnt seem to be too good for the library?
                probeVid(testUri);
                //vw1.setVideoURI(testUri);
                //vw1.start();
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
            InputStream fileInputFromURI = getContentResolver().openInputStream(videoURI);
            System.out.println(getCacheDir().getAbsolutePath());
            File testFile = new File(getCacheDir(), "testOutputVideo.mp4");
            OutputStream fileOutput = new FileOutputStream(testFile);
            byte[] buffer = new byte[10000*1024];
            int read = 0;
            while (read != -1) {
                //read into the buffer, and put number of byter read into "read"
                read = fileInputFromURI.read(buffer);
                //write into the file out, from the buffer, the number of bytes previously read, no offset
                if(read != -1) {
                    fileOutput.write(buffer, 0, read);
                }

            }
            fileOutput.flush();
            fileOutput.close();
            System.out.println("Test file path thing " + testFile.getPath());
            MediaInformation info = FFprobe.getMediaInformation(testFile.getPath());
            System.out.println(info.getFormat());
            vw1.setVideoURI(Uri.fromFile(testFile));
            vw1.start();
        }catch (FileNotFoundException e) {
            System.out.println("what the fuck this failed :))))");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
