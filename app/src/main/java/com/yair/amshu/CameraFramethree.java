package com.yair.amshu;

import android.widget.RatingBar;

import org.opencv.imgproc.Imgproc;

public class CameraFramethree  extends CameraFrametwo {
    private String levelName="level3";
    private int pointFlag =0;
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
            pointFlag++;
            if(pointFlag ==3) {
                addScore();
                pointFlag =0;
            }
            hitSound.start();
            hitFlag = false;
            return;
        }
        else if (hitDetection(rightHitArea) && !hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            pointFlag++;
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
        pointFlag =0;
        if(scoremanage1.get_score()>0)
            scoremanage1.addscore(-1,levelName);
    }
    @Override

    public void starNotifay(){
        RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBaronline);
        simpleRatingBar1.setRating(scoremanage1.getmaxstar(levelName));
    }
}
