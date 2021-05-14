package com.yair.amshu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

public class LoadPageBall1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_page);
    }

    public void go_to_start(View view) {
        Intent intent = new Intent(this, CameraFrameone.class);
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
}