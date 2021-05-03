package com.yair.amshu;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class CameraFrametwo extends CameraFrameone {
    @Override
    public void runGame(){
        Imgproc.rectangle(dst,new Point(0,0),new Point(dst.cols()/2,dst.rows()/2),new Scalar(224));
    }
}
