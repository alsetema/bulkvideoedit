package com.example.bulkvideoeditor;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
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


import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private VideoView vw1;
    private TextView clipInfoView;
    private Button filePickButton;
    final int CODE_MULTIPLE_VIDEO = 1;

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
                probeVid(testUri);
                vw1.setVideoURI(testUri);
                vw1.start();
            }
        }

    }

    private void probeVid(Uri videoURI) {
        // I think this was supposed to get the information on the video to check the resolution first, and then decide how to square it.
        //need to figure out how the fucking ffmeg library works

        //checking what the uri is a string
        System.out.println("the URI is: " + videoURI.toString());
        MediaInformation info = FFprobe.getMediaInformation(videoURI.toString());
    }





    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
