package com.yair.amshu;

import android.widget.RatingBar;

public class CameraFramethree  extends CameraFrametwo {

    @Override
    protected  void add1scorelevel(){
        scoremanage1.addscore(1,"level3");
    }
    @Override
    protected  void sub1scorelevel(){
        if(scoremanage1.get_score()>0)
            scoremanage1.addscore(-1,"level3");
    }
    @Override

    public void starNotifay(){

        RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBaronline);
        simpleRatingBar1.setRating(scoremanage1.getmaxstar("level3"));
    }
}
