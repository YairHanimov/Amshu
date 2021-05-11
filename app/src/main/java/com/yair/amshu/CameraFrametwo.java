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

        drawBallCenter();
        if(hitDetection(topMissArea)){
            Imgproc.putText(dst, "too high", new Point(dst.rows() / 2, dst.rows() / 2),
                    2, 2, new Scalar(123, 44, 121));
            remainingTimeCounter.start();
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
           // scoremanage1.addscore(1,"level1");
            hitFlag = false;
            return;
        }
        else if (hitDetection(rightHitArea) && !hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            scoremanage1.addscore(1,"level1");
            hitFlag = true;

            return;
        }
    }

}
