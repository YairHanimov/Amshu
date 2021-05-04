package com.yair.amshu;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class CameraFrametwo extends CameraFrameone {
    @Override
    public void runGame(){
        if (hitFlag) {
            //Imgproc.rectangle(dst,new Point(0,0),new Point(dst.cols()/2,dst.rows()/2),new Scalar(224));
            Imgproc.rectangle(dst, new Point(faceX - faceWidth, faceY), new Point(faceX, faceY + faceHeight), new Scalar(0, 0, 255), 3);
            Imgproc.rectangle(dst, new Point(faceX + faceWidth, faceY), new Point(faceX + 2 * faceWidth, faceY + faceHeight), new Scalar(255, 255, 255), 3);
        } else {
            //Imgproc.rectangle(dst,new Point(dst.cols()/2,0),new Point(dst.cols(),dst.rows()/2),new Scalar(24));
            Imgproc.rectangle(dst, new Point(faceX + faceWidth, faceY), new Point(faceX + 2 * faceWidth, faceY + faceHeight), new Scalar(0, 0, 255), 3);
            Imgproc.rectangle(dst, new Point(faceX - faceWidth, faceY), new Point(faceX, faceY + faceHeight), new Scalar(255, 255, 255), 3);
        }
        List<MatOfPoint> LHcontours,RHcontours, LMcontours,RMcountours;
        leftMissArea.setRectByDisplayRect();
        rightMissArea.setRectByDisplayRect();
        leftHitArea.setRectByDisplayRect();
        rightHitArea.setRectByDisplayRect();
        leftHitArea.setRoiByRect(dst);
        rightHitArea.setRoiByRect(dst);
        leftMissArea.setRoiByRect(dst);
        rightMissArea.setRoiByRect(dst);
        LHcontours=leftHitArea.MovementDetection(ditaction.getLowBound(),ditaction.getUpBound());
        RHcontours=rightHitArea.MovementDetection(ditaction.getLowBound(),ditaction.getUpBound());
        LMcontours= leftMissArea.MovementDetection(ditaction.getLowBound(),ditaction.getUpBound());
        RMcountours=rightMissArea.MovementDetection(ditaction.getLowBound(),ditaction.getUpBound());
        //drawBallCenter();
        if(RMcountours.size()>0&&!rightMissFlag&&!hitFlag){
            rightMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if(LMcontours.size()>0&&!leftMissFlag&&hitFlag){
            leftMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if (LHcontours.size() > 0 && hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            //scoremanage1.addscore(1,"level1");
            hitFlag = false;
            return;
        }
        else if (RHcontours.size() > 0 && !hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            scoremanage1.addscore(1,"level1");
            hitFlag = true;

            return;
        }
    }
}
