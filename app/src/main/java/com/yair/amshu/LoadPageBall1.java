package com.yair.amshu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoadPageBall1 extends Activity {

    private static final String TAG = "LoadPageBall1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_page);
        checkCameraPermission();

    }

    public void go_to_start(View view) {
        Intent intent = new Intent(this, LevelsManager.class);
        startActivity(intent);
    }

    public void vid_exm(View view) {
        setContentView(R.layout.vid1_page);
        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.vid_exm);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                setContentView(R.layout.loadpage);
            }
        });
    }

    public void back_to_menu(View view) {
        Intent intent =   new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    public void exit_from_vid_1(View view) {
        recreate();

    }

    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
    private void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission not available requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            Log.d(TAG,"Permission has already granted");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"permission was granted! Do your stuff");
                } else {
                    Log.d(TAG,"permission denied! Disable the function related with permission.");
                }
                return;
            }
        }
    }

    public void quit_all(View view) {

        System.exit(0);
    }
}