package com.yair.amshu;

import androidx.constraintlayout.solver.widgets.Rectangle;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HitArea {
    private Rect rect;
    public Rectangle displayRect;
    private Mat roi;
    private ArrayList<Mat> frames;
    private Point topLeft;
    private Point botRight;
    //constractor
    HitArea(){
        this.rect=new Rect();
        this.displayRect=new Rectangle();
        this.roi=new Mat();
        this.frames=new ArrayList<>();
        this.topLeft=new Point(0,0);
        this.botRight=new Point(0,0);
    }
    //setters and getters
    public void setRectByDisplayRect(){
        this.rect.set(displayRect.x,displayRect.y,displayRect.width,displayRect.height);
    }
    public void setRoiByRect(Mat frame){
        if(rect.x>=0&&rect.y>=0&&rect.height>=0&&rect.width>=0){
            this.roi=frame.submat(rect);
        }
    }
    public Point getDisplayRectTopLeft(){
        this.topLeft.x=this.displayRect.x;
        this.topLeft.y=this.displayRect.y;
        return this.topLeft;
    }
    public Point getDisplayRectbotRight(){
        this.botRight.x=this.displayRect.x+this.displayRect.width;
        this.botRight.y=this.displayRect.y+this.displayRect.height;
        return this.botRight;
    }
    //detect movement by colors scala in the hit areas and return the detected countours of the moved item
    public List<MatOfPoint> MovementDetection( Scalar lowColorBound,Scalar highColorBound) {
        Mat diff=new Mat();
        Mat gray = new Mat();
        Mat mask=new Mat();
        Mat res=new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        //create  the little mat of the hitarea
        Imgproc.cvtColor(this.roi, gray, Imgproc.COLOR_RGB2HSV_FULL);
        //decide the colors that the mat will detect
        Core.inRange(gray, lowColorBound, highColorBound, mask);
        //make bitwise between the big frame and the mask
        Core.bitwise_and(this.roi, this.roi, res, mask);
        this.frames.add(res);
        if (this.frames.size() != 2) return contours;
        //find diff bitween 2 last frames
        Core.absdiff(this.frames.get(0), this.frames.get(1), diff);
        this.frames.remove(0);
        Imgproc.cvtColor(diff, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Imgproc.threshold(gray, gray, 20, 255, Imgproc.THRESH_BINARY);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(gray, gray, kernel);
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        this.frames.clear();
        return contours;

    }
}