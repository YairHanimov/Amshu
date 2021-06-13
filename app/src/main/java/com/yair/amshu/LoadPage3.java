package com.yair.amshu;

import android.content.Intent;
import android.view.View;

public class LoadPage3 extends LoadPageBall1 {

    @Override
    public void go_to_start(View view) {
        Intent intent = new Intent(this, CameraFramethree.class);
        startActivity(intent);
    }
}
