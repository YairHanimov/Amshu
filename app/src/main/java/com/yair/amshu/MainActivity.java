package com.yair.amshu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


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
        setContentView(R.layout.firstscreen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkCameraPermission();
        Intent intent = new Intent(this, LoadPageBall1.class);
        startActivity(intent);
//        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        setContentView(R.layout.firstscreen);
//        try {
//            SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//            String channel = (shared.getString("level1", ""));
//            int i;
//            if (channel != null) {
//                i = Integer.parseInt(channel);
//            } else {
//                i = 0;
//            }
//            String channe2 = (shared.getString("level2", "0"));
//            int j;
//            if (channe2 != null) {
//                j = Integer.parseInt(channe2);
//            } else {
//                j = 0;
//            }String channel3 = (shared.getString("level3", "0"));
//            int k;
//            if (channel3 != null) {
//                k = Integer.parseInt(channel3);
//
//            } else {
//                k = 0;
//            }
//            RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBar);
//            simpleRatingBar1.setRating(i);
//            LayerDrawable stars = (LayerDrawable) simpleRatingBar1.getProgressDrawable();
//            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
//
//            RatingBar simpleRatingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
//            simpleRatingBar2.setRating(j);
//            LayerDrawable stars2 = (LayerDrawable) simpleRatingBar1.getProgressDrawable();
//            stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
//
//            RatingBar simpleRatingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
//            simpleRatingBar3.setRating(k);
//            LayerDrawable stars3 = (LayerDrawable) simpleRatingBar1.getProgressDrawable();
//            stars3.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
//            //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
//            //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
//        }
//        catch ( Exception e){
//            int i = 0;
//            RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBar);
//            simpleRatingBar1.setRating(i);
//            //       mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
//            //       mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
//            setContentView(R.layout.firstscreen);
//        }

        //setContentView(R.layout.firstscreen);

    }


//    public void onRadioButtonClicked(View view) {
//
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch(view.getId()) {
//            case R.id.radioButton1:
//                if (checked)
//                   // setContentView(R.layout.loadpage);
//                {
//                    ((RadioButton) view).setChecked(false);
//
//                    Intent intent = new Intent(this, LoadPageBall1.class);
//                    startActivity(intent);
////                    Intent intent = new Intent(this, load_page_ball_1.class);
////                    startActivity(intent);
//
//                }
//                break;
//            case R.id.radioButton2:
//                if (checked) {
//                    ((RadioButton) view).setChecked(false);
//                    try {
//                        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//                        String channel = (shared.getString("level1", "0"));
//
//                        int i;
//
//
//                        if (channel != null) {
//                            i = Integer.parseInt(channel);
//
//                        } else {
//                            i = 0;
//
//
//                        }
//                        if (i>4){
//                             //open level
//                            Intent intent = new Intent(this, Loadpage2.class);
//                            startActivity(intent);
//                        }
//                        else {
//                            popupMessage_level2();                        }
//
//                        //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
//                        //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
//                    }
//                    catch ( Exception e){
//
//                    }
//                    break;
//                }
//            case R.id.radioButton3:
//                if (checked) {
//                    ((RadioButton) view).setChecked(false);
//                    try {
//                        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//                        String channel = (shared.getString("level2", "0"));
//                        int i;
//                        if (channel != null) {
//                            i = Integer.parseInt(channel);
//                        } else {
//                            i = 0;
//                        }
//                        if (i>4){
//                            // open level
//                            Intent intent = new Intent(this, LoadPage3.class);
//                            startActivity(intent);
//                        }
//                        else {
//                            popupMessage_level3();
//                        }
//
//                        //   mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
//                        //     mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
//                    }
//                    catch ( Exception e){
//
//                    }
//                    break;
//                }
//        }
//    }

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
//    public void popupMessage_level2(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("level  1 must be passed" +
//                " With at least 4 stars to open this stage");
//        alertDialogBuilder.setTitle("unlocked level");
//        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d("internet","Ok btn pressed");
//                // add these two lines, if you wish to close the app:
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//    public void popupMessage_level3(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("level  2 must be passed" +
//                " With at least 4 stars to open this stage");
//        alertDialogBuilder.setTitle("unlocked level");
//        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d("internet","Ok btn pressed");
//                // add these two lines, if you wish to close the app:
//
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

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
}