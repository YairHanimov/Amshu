package com.yair.amshu;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class scoremanager {
    Context context;
    CameraFrameone CameraFrameone;
    int score_point =0 ;
    int score_star =0 ;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public scoremanager( Context c, CameraFrameone cam){
        this.context=c;
        this.sharedpreferences = c.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        this.CameraFrameone=cam;

    }
    public int  getmaxstar(String level) {
        try {
            SharedPreferences shared = this.context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String channel = (shared.getString(level, "0"));
            if (channel != null) {
                return Integer.parseInt(channel);
            }


        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }
        public void  set_score(int score){
        this.score_point = score;
    }
    public int   get_score(){
        return this.score_point;
    }

    public void  set_score_star(int star){
        this.score_star = star;
    }

    public int   get_score_star(){
        return this.score_star;
    }
    public  void addscore(int i, String level) {
        this.score_point = (this.score_point + i);
       if (score_point/10>score_star){
           set_score_star(score_point/10);
       }
        try {
            SharedPreferences shared = this.context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String channel = (shared.getString(level, "0"));
            if (channel != null) {
                if (this.score_star > Integer.parseInt(channel)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String num = String.valueOf(this.score_star);
                    editor.putString(level, num);
                    editor.apply();
                    this.CameraFrameone.starNotifay();

                }
            }
        }catch (Exception r){

        }

    }


}