package com.yair.amshu;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public  class KMeans {
    //Get list of points and calulate the center of them
    public static Point Cluster(MatOfPoint list){
        Point center=new Point(0,0);
        double x = 0, y = 0;
        for(int i=0;i<list.toList().size();i++){
            x+=list.toList().get(i).x;
            y+=list.toList().get(i).y;
        }
        center.set(new double[]{x/list.toList().size(),y/list.toList().size()});
        return center;
    }
}
