package com.example.bulkvideoeditor;

import android.content.Context;
import android.net.Uri;
import java.math.*;
import java.io.*;
import java.util.Random;


public class FileFunc {

    //error handling will be done in exceptions no int shit
    protected static String uriToFile(Uri inputURI, String path, Context ct) throws IOException, FileNotFoundException {

        InputStream fileIS = ct.getContentResolver().openInputStream(inputURI);

        File outFile = genTempFile(path);
        OutputStream fileOutput = new FileOutputStream(outFile);
        byte[] buffer = new byte[10000*1024];
        int read;
        do {
            //read into the buffer, and put number of byter read into "read"
            read = fileIS.read(buffer);
            if (read != -1) {
                //write into the file out, from the buffer, the number of bytes previously read, no offset
                fileOutput.write(buffer,0,read);
            }
        } while (read != -1);
        fileOutput.flush();
        fileOutput.close();
        //returns in a string the name of the temp file if you need to do anything in it
        return ct.getCacheDir() + "/" + outFile.getName();

    }

    private static File genTempFile(String path) {
        Random rd = new Random();
        int bound = 100000;
        String fileName = rd.nextInt(bound) + ".tmp";
        File tempFile = new File(path,fileName);
        while ((tempFile.exists() && tempFile.isFile()) || tempFile.isDirectory()) {
            fileName = rd.nextInt(bound) + ".tmp";
            tempFile = new File(path,fileName);
        }
        return tempFile;
    }



}
