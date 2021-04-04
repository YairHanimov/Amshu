package com.yair.amshu;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public class Kmeans {
    MatOfPoint list;
    Kmeans(MatOfPoint list){
        this.list=list;
    };
    public Point getcenter(){
        Point center=new Point(0,0);
        for(int i=0;i<list.toList().size();i++){
            center.set(new double[]{list.toList().get(i).x+center.x, list.toList().get(i).y+center.y});
        }
        center.set(new double[]{center.x/list.toList().size(),center.y/list.toList().size()});
        return center;
    }

}
