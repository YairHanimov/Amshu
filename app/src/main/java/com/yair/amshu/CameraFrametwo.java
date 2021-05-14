package com.yair.amshu;

import android.widget.RatingBar;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class CameraFrametwo extends CameraFrameone {
    protected Scalar whiteColor=new Scalar(255,255,255);
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
}
