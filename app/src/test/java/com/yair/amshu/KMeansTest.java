package com.yair.amshu;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KMeansTest {
   //  MatOfPoint mymat = new MatOfPoint(new Point(0,0), new Point(10,10));
    @org.junit.jupiter.api.Test
    void cluster() {
         Point testpoint_1 = KMeans.Cluster(new MatOfPoint(new Point(0,0), new Point(10,10)));
        assertEquals(testpoint_1,new Point(5,5));
    }
}