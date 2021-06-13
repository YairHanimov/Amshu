package com.yair.amshu;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.RatingBar;
import android.widget.VideoView;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class CameraFrametwo extends CameraFrameone {
    protected Scalar whiteColor=new Scalar(255,255,255);
    protected int numberOfBall=2;
    private String levelName="level2";
    @Override
    public void runGame(){
        if (hitFlag) {
            Imgproc.rectangle(dst, leftHitArea.getDisplayRectTopLeft(), leftHitArea.getDisplayRectbotRight(), blueColor, 3);
            Imgproc.rectangle(dst, rightHitArea.getDisplayRectTopLeft(), rightHitArea.getDisplayRectbotRight(), whiteColor, 3);
        } else {
            Imgproc.rectangle(dst, rightHitArea.getDisplayRectTopLeft(), rightHitArea.getDisplayRectbotRight(), blueColor, 3);
            Imgproc.rectangle(dst, leftHitArea.getDisplayRectTopLeft(), leftHitArea.getDisplayRectbotRight(), whiteColor, 3);
        }
        drawBallCenter();
        if(hitDetection(topMissArea)&&!topMissFlag){
            subScore();
            toHighsound.start();
            topMissFlag=true;
            remainingTimeCounter2.start();
            return;
        }
        if(hitDetection(rightMissArea)&&!rightMissFlag&&!hitFlag){
            rightMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if(hitDetection(leftMissArea)&&!leftMissFlag&&hitFlag){
            leftMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if (hitDetection(leftHitArea) && hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            //addScore();
            hitSound.start();
            hitFlag = false;
            return;
        }
        else if (hitDetection(rightHitArea) && !hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            addScore();
            hitSound.start();
            hitFlag = true;
            return;
        }
    }

    @Override
    protected  void addScore(){
        scoremanage1.addscore(1,levelName);
    }
    @Override
    protected  void subScore(){
        if(scoremanage1.get_score()>0)
            scoremanage1.addscore(-1,levelName);
    }
    @Override

    public void starNotifay(){
        RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBaronline);
        simpleRatingBar1.setRating(scoremanage1.getmaxstar(levelName));
    }
    public void showVideo(View view) {
        if (openSound.isPlaying()){
            openSound.stop();
        }
        if (timersound.isPlaying()){
            timersound.stop();
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                findViewById(R.id.button_exit).setVisibility(View.INVISIBLE);
                findViewById(R.id.button_exit_2_video).setVisibility(View.VISIBLE);
                findViewById(R.id.scanbtn).setVisibility(View.INVISIBLE);
                findViewById(R.id.qmark).setVisibility(View.INVISIBLE);
                findViewById(R.id.qmark).setVisibility(View.INVISIBLE);
                findViewById(R.id.score_counter_xml).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView).setVisibility(View.INVISIBLE);
                findViewById(R.id.ratingBaronline).setVisibility(View.INVISIBLE);
                findViewById(R.id.videoView2).setVisibility(View.VISIBLE);
                VideoView videoView = (VideoView)findViewById(R.id.videoView2);
                videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.ball2);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        findViewById(R.id.button_exit).setVisibility(View.VISIBLE);

                        findViewById(R.id.videoView2).setVisibility(View.INVISIBLE);
                        findViewById(R.id.qmark).setVisibility(View.VISIBLE);
                        findViewById(R.id.scanbtn).setVisibility(View.VISIBLE);
                        findViewById(R.id.score_counter_xml).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView).setVisibility(View.VISIBLE);
                        findViewById(R.id.ratingBaronline).setVisibility(View.VISIBLE);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });
    }
}
