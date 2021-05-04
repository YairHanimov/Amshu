package com.yair.amshu;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class scoremanager {
    Context context;
    int score_point =0 ;
    int score_star =0 ;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public scoremanager( Context c){
        this.context=c;
        this.sharedpreferences = c.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
    public void  set_score(int score){
        this.score_point = score;
    }
    public int   get_score(){
        return this.score_point;
    }
    public String  get_max_score(int k){
        SharedPreferences shared = this.context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String channel = (shared.getString("key", ""));
        return channel;
    }
    public void  set_score_star(int star){
        this.score_star = star;
    }
    public int   get_score_star(int star){
        return this.score_star;
    }
    public  void addscore(int i, String level) {
        this.score_point = (this.score_point + i);
        try {
            SharedPreferences shared = this.context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String channel = (shared.getString(level, "0"));
            if (channel != null) {
                if (this.score_point >= Integer.parseInt(channel)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String num = String.valueOf(this.score_point);
                    editor.putString(level, num);
                    editor.apply();
                }
            }
        }catch (Exception r){

        }

    }

}