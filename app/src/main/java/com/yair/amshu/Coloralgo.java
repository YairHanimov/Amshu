package com.yair.amshu;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class Coloralgo {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar LowBound = new Scalar(0);
    private Scalar UpBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double minarea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar ColorRadius = new Scalar(25,50,50,0);
    private Mat Spectrumm = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    // Cache
    Mat PyrDownMat = new Mat();
    Mat HsvMat = new Mat();
    Mat Mask = new Mat();
    Mat DilatedMask = new Mat();
    Mat Hierarchy = new Mat();
    public Scalar getLowBound(){
        return this.LowBound;
    }
    public Scalar getUpBound(){
        return this.UpBound;
    }

    public void setColorRadius(Scalar radius) {
        ColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= ColorRadius.val[0]) ? hsvColor.val[0]- ColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+ ColorRadius.val[0] <= 255) ? hsvColor.val[0]+ ColorRadius.val[0] : 255;

        LowBound.val[0] = minH;
        UpBound.val[0] = maxH;

        LowBound.val[1] = hsvColor.val[1] - ColorRadius.val[1];
        UpBound.val[1] = hsvColor.val[1] + ColorRadius.val[1];

        LowBound.val[2] = hsvColor.val[2] - ColorRadius.val[2];
        UpBound.val[2] = hsvColor.val[2] + ColorRadius.val[2];

        LowBound.val[3] = 0;
        UpBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, Spectrumm, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return Spectrumm;
    }

    public void setMinContourArea(double area) {
        minarea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, PyrDownMat);
        Imgproc.pyrDown(PyrDownMat, PyrDownMat);

        Imgproc.cvtColor(PyrDownMat, HsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(HsvMat, LowBound, UpBound, Mask);
        Imgproc.dilate(Mask, DilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(DilatedMask, contours, Hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > minarea *maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}
