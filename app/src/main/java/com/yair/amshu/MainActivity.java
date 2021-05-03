package com.yair.amshu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends Activity  {
    private static final String  TAG              = "MainActivity";


    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());

    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.firstscreen);
        try {
            SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String channel = (shared.getString("key", ""));
            int i;
            if (channel != null) {
                i = Integer.parseInt(channel);
            } else {
                i = 0;
            }
            RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBar);
            simpleRatingBar1.setRating(i);
            LayerDrawable stars = (LayerDrawable) simpleRatingBar1.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
            //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
        }
        catch ( Exception e){
            int i = 0;
            RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBar);
            simpleRatingBar1.setRating(i);
            //       mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
            //       mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
            setContentView(R.layout.firstscreen);
        }

        //setContentView(R.layout.firstscreen);

    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                   // setContentView(R.layout.loadpage);
                {
                    ((RadioButton) view).setChecked(false);

                    Intent intent = new Intent(this, load_page_ball_1.class);
                    startActivity(intent);
//                    Intent intent = new Intent(this, load_page_ball_1.class);
//                    startActivity(intent);

                }
                break;
            case R.id.radioButton2:
                if (checked) {
                    ((RadioButton) view).setChecked(false);
                    try {
                        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                        String channel = (shared.getString("key", ""));
                        int i;
                        if (channel != null) {
                            i = Integer.parseInt(channel);
                        } else {
                            i = 0;
                        }
                        if (i>60){
                            // open level
//                            Intent intent = new Intent(this, m2.class);
//                            startActivity(intent);
                        }
                        else {
                            popupMessage_level2();                        }

                        //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
                        //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
                    }
                    catch ( Exception e){

                    }
                    break;
                }
            case R.id.radioButton3:
                if (checked) {
                    ((RadioButton) view).setChecked(false);
                    try {
                        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                        String channel = (shared.getString("key", ""));
                        int i;
                        if (channel != null) {
                            i = Integer.parseInt(channel);
                        } else {
                            i = 0;
                        }
                        if (i>100){
                            // open level
//                            Intent intent = new Intent(this, m3.class);
//                            startActivity(intent);
                        }
                        else {
                            popupMessage_level3();
                        }

                        //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
                        //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
                    }
                    catch ( Exception e){

                    }
                    break;
                }
        }
    }

    public void vid_exm(View v){
        setContentView(R.layout.vid1_page);
        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.test);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                setContentView(R.layout.loadpage);
            }
        });
    }

    public void go_to_start(View v ){



    }
    public  void  exit_from_vid_1 (View v){
        setContentView(R.layout.loadpage);

    }

    public void exit_from_view(View view) {
        //  mp1.stop();
        //   mp2.stop();
        recreate();

    }
    public void back_to_menu(View view) {
        setContentView(R.layout.firstscreen);

    }
    public void popupMessage_level2(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("level  1 must be passed" +
                " With at least 4 stars to open this stage");
        alertDialogBuilder.setTitle("unlocked level");
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("internet","Ok btn pressed");
                // add these two lines, if you wish to close the app:
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void popupMessage_level3(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("level  2 must be passed" +
                " With at least 4 stars to open this stage");
        alertDialogBuilder.setTitle("unlocked level");
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("internet","Ok btn pressed");
                // add these two lines, if you wish to close the app:

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}