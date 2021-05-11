package com.yair.amshu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraFrameone  extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2  {
    private static final String  TAG              = "CameraFrameone";
    private boolean colorselect = false;
    protected Mat dst, thespectrum;
    private Scalar ballcolorrgb, ballcolorhsv, counter;
    protected Coloralgo ditaction;
    private Size spectorsize;
    private CascadeClassifier cascadeClassifier;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private CameraBridgeViewBase opencvcam;
    private int absoluteFaceSize;
    scoremanager scoremanage1;
    protected boolean hitFlag =true, faceSizeFlag =true, countBackFlag, faceDetectFlag,
            leftMissFlag ,rightMissFlag;
    SharedPreferences sharedpreferences;
    MediaPlayer mp2 ;
    MediaPlayer mp1;
    protected HitArea leftHitArea,rightHitArea,leftMissArea,rightMissArea,topMissArea;
    protected int faceX, faceY, faceWidth, faceHeight;
    private List<Point> pointsDeque = new ArrayList<Point>();
    public CountDownTimer remainingTimeCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_frameone);
        opencvcam = (CameraBridgeViewBase) findViewById(R.id.mycamera);
        opencvcam.setVisibility(SurfaceView.VISIBLE);
        opencvcam.setCvCameraViewListener(this);
        scoremanage1 = new scoremanager(this);
       // mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
        mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);
       // mp1.start();
    }


    private BaseLoaderCallback theLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    initializeOpenCVDependencies();
                    Log.i(TAG, "OpenCV loaded successfully");
                    opencvcam.enableView();
                    opencvcam.setOnTouchListener(CameraFrameone.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        dst = new Mat();
        leftHitArea=new HitArea();
        rightHitArea=new HitArea();
        leftMissArea =new HitArea();
        rightMissArea=new HitArea();
        topMissArea=new HitArea();
        ditaction = new Coloralgo();
        thespectrum = new Mat();
        ballcolorrgb = new Scalar(255);
        ballcolorhsv = new Scalar(255);
        spectorsize = new Size(200, 64);
        counter = new Scalar(255,0,0,255);
    }

    @Override
    public void onCameraViewStopped() {
        dst.release();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (opencvcam != null)
            opencvcam.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, theLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            theLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (opencvcam != null)
            opencvcam.disableView();
    }


    private void initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }
    }



    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat rgba=inputFrame.rgba();
        Core.flip(rgba,rgba,0);
        int cols = rgba.cols();
        int rows = rgba.rows();
        Mat m=Imgproc.getRotationMatrix2D(new Point(cols/2,rows/2),90,0.75);
        Imgproc.warpAffine(rgba, dst,m,rgba.size());
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGBA2RGB);
        //Imgproc.medianBlur(dst,dst,3);
        TextView score   = (TextView) findViewById(R.id.score_counter_xml);
        score.setText(String.valueOf(scoremanage1.get_score()));
        if(countBackFlag) {
            faceDetection();
        }
        if(colorselect) {
            if(!faceDetectFlag){
                Imgproc.putText(dst, "I need to see your face", new Point(dst.rows() / 2, dst.rows() / 2),
                        1, 2, new Scalar(0, 0, 0));
                return dst;
            }
            runGame();
        }

        return dst;
    }
    public void faceDetection(){
        MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(dst, faces, 1.1, 3, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            if (facesArray.length != 1) {
                Imgproc.putText(dst, "only 1 person allow", new Point(dst.rows() / 2, dst.rows() / 2),
                        1, 2, new Scalar(0, 0, 0));
                break;
            }
            if (faceSizeFlag) {
                faceWidth = facesArray[i].width * 2 / 3;
                faceHeight = facesArray[i].height * 2 / 3;
                leftMissArea.displayRect.setBounds(0,0,dst.cols()/2,dst.rows()/2);
                rightMissArea.displayRect.setBounds(dst.cols()/2,0,dst.cols()/2,dst.rows()/2);

                faceSizeFlag = false;
            }
            Imgproc.rectangle(dst,new Point(0,0),new Point(dst.cols(),80),new Scalar(111,11,1),3);
            faceX = (facesArray[i].x > 0) ? facesArray[i].x : 0;
            faceY = (facesArray[i].y - faceHeight > 0) ? facesArray[i].y - faceHeight : 0;
            topMissArea.displayRect.setBounds(0,0,dst.cols(),80);
            leftHitArea.displayRect.setBounds(faceX - faceWidth, faceY, faceWidth, faceHeight);
            rightHitArea.displayRect.setBounds(faceX + faceWidth, faceY, faceWidth, faceHeight);

            faceDetectFlag =true;
        }
    }
    public void runGame(){
        if (hitFlag) {
          //  Imgproc.rectangle(dst,new Point(0,0),new Point(dst.cols()/2,dst.rows()/2),new Scalar(224));
            Imgproc.rectangle(dst, new Point(faceX - faceWidth, faceY), new Point(faceX, faceY + faceHeight), new Scalar(0, 0, 255), 3);
            //Imgproc.rectangle(dst, new Point(faceX + faceWidth, faceY), new Point(faceX + 2 * faceWidth, faceY + faceHeight), new Scalar(255, 255, 255), 3);
        } else {
            //Imgproc.rectangle(dst,new Point(dst.cols()/2,0),new Point(dst.cols(),dst.rows()/2),new Scalar(24));
            Imgproc.rectangle(dst, new Point(faceX + faceWidth, faceY), new Point(faceX + 2 * faceWidth, faceY + faceHeight), new Scalar(0, 0, 255), 3);
            //Imgproc.rectangle(dst, new Point(faceX - faceWidth, faceY), new Point(faceX, faceY + faceHeight), new Scalar(255, 255, 255), 3);
        }
        drawBallCenter();
        if(hitDetection(topMissArea)){
            Imgproc.putText(dst, "too high", new Point(dst.rows() / 2, dst.rows() / 2),
                    2, 2, new Scalar(123, 44, 121));
            remainingTimeCounter.start();
            return;
        }
        if(hitDetection(rightMissArea)&&!rightMissFlag&&!hitFlag){
            rightMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if(hitDetection(leftMissArea)&&!leftMissFlag&&hitFlag){
            leftMissFlag =true;
            remainingTimeCounter.start();
            return;
        }
        else if (hitDetection(leftHitArea) && hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            scoremanage1.addscore(1,"level1");
            hitFlag = false;
            return;
        }
        else if (hitDetection(rightHitArea) && !hitFlag) {
            leftMissFlag =false;
            remainingTimeCounter.cancel();
            scoremanage1.addscore(1,"level1");
            hitFlag = true;

            return;
        }
    }
    protected boolean hitDetection(HitArea area){
        area.setRectByDisplayRect();
        area.setRoiByRect(dst);
        if(area.MovementDetection(ditaction.getLowBound(),ditaction.getUpBound()).size()>0)
            return true;
        return false;
    }
    protected void drawBallCenter(){
        List<Point> centers = new ArrayList<Point>();
        float[] radius = new float[1];
        MatOfPoint2f list2f = new MatOfPoint2f();
        //draw center of the ball
        ditaction.process(dst);
        final List<MatOfPoint> contours = ditaction.getContours();
        for(MatOfPoint list:contours){
            centers.add(KMeans.Cluster(list));
            list.convertTo(list2f, CvType.CV_32F);
        }
        //draw detecting line after ball movement
        for(Point center:centers) {
            Imgproc.drawMarker(dst, center, new Scalar(255, 0, 0));
            pointsDeque.add(center);
            if (pointsDeque.size() >= 10)
                pointsDeque.remove(0);
//            for (int i = 0; i < pointsDeque.size() - 1; i++) {
//                if (pointsDeque.get(i).x > 0 && pointsDeque.get(i).y > 0 &&
//                        pointsDeque.get(i + 1).x > 0 && pointsDeque.get(i + 1).y > 0)
//                    Imgproc.line(dst, pointsDeque.get(i), pointsDeque.get(i + 1),
//                            new Scalar(141, 222, 23), 2);
//            }
            //draw circle around the ball
//            Imgproc.minEnclosingCircle(list2f,center,radius);
//            Imgproc.circle(dst,center,(int)radius[0],new Scalar(255,0,0));
        }

    }

    public void show_vid_one(View view) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                findViewById(R.id.scanbtn).setVisibility(View.INVISIBLE);
                findViewById(R.id.qmark).setVisibility(View.INVISIBLE);
                findViewById(R.id.qmark).setVisibility(View.INVISIBLE);
                findViewById(R.id.score_counter_xml).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView).setVisibility(View.INVISIBLE);



                findViewById(R.id.videoView2).setVisibility(View.VISIBLE);
                VideoView videoView = (VideoView)findViewById(R.id.videoView2);
                videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.test);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        findViewById(R.id.videoView2).setVisibility(View.INVISIBLE);
                        findViewById(R.id.qmark).setVisibility(View.VISIBLE);
                        findViewById(R.id.scanbtn).setVisibility(View.VISIBLE);
                        findViewById(R.id.score_counter_xml).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView).setVisibility(View.VISIBLE);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);


                    }
                });

            }
        });

    }
    public void image_person_click(View view) {
    }

    public void exit_from_view(View view) {
        Intent intent =   new Intent(this,load_page_ball_1.class);
        startActivity(intent);
    }

    public void scan_btn(View view) {
        //mp2.start();
        setBallColor();
        view.setVisibility(View.INVISIBLE);
        ImageButton person_image  = (ImageButton) findViewById(R.id.button_person);
        person_image.setVisibility(View.VISIBLE);
        TextView timer_xml   = (TextView) findViewById(R.id.timer_time);
        ImageView ball  = (ImageView) findViewById(R.id.imageView8);
        ball.setVisibility(View.INVISIBLE);
        person_image.setVisibility(View.VISIBLE);
        timer_xml.setText(String.valueOf(5));
        timer_xml.setVisibility(View.VISIBLE);
        new CountDownTimer(5000, 1000) {
            TextView timer_xml   = (TextView) findViewById(R.id.timer_time);
            public void onTick(long millisUntilFinished) {
                timer_xml.setText(String.valueOf((int)(millisUntilFinished / 1000)));
            }
            public void onFinish() {
                ImageButton person_image  = (ImageButton) findViewById(R.id.button_person);

                timer_xml.setVisibility(View.INVISIBLE);
                person_image.setVisibility(View.INVISIBLE);
                countBackFlag =true;
            }

        }.start();
        missDetect();

    }
    public void missDetect(){
        remainingTimeCounter =  new CountDownTimer(300, 100) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Imgproc.putText(dst, "miss", new Point(dst.rows() / 2, dst.rows() / 2),
                        1, 2, new Scalar(0, 255, 0));
                if(scoremanage1.get_score()>0)
                    scoremanage1.addscore(-1,"level1");
                if(leftMissFlag) {
                    leftMissFlag = false;
                }
                if(rightMissFlag) {
                    rightMissFlag = false;
                }
                // this.start(); //start again the CountDownTimer
            }
        };



    }


    public void setBallColor(){
        int cols = dst.cols();
        int rows = dst.rows();
        int x = cols/2;
        int y = rows/2;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = dst.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        ballcolorhsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < ballcolorhsv.val.length; i++)
            ballcolorhsv.val[i] /= pointCount;

        ballcolorrgb = converScalarHsv2Rgba(ballcolorhsv);

        Log.i(TAG, "Touched rgba color: (" + ballcolorrgb.val[0] + ", " + ballcolorrgb.val[1] +
                ", " + ballcolorrgb.val[2] + ", " + ballcolorrgb.val[3] + ")");

        ditaction.setHsvColor(ballcolorhsv);

        Imgproc.resize(ditaction.getSpectrum(), thespectrum, spectorsize, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        colorselect = true;
        touchedRegionRgba.release();
        touchedRegionHsv.release();
        afterballt();
    }
    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }



    public void  afterballt(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ImageView ballvisbility  = (ImageView) findViewById(R.id.imageView8);

                if (ballvisbility.getVisibility() == View.VISIBLE) {
                    makevisble();
                    ballvisbility.setVisibility(View.INVISIBLE);
                    ImageButton xcanbtb  = (ImageButton) findViewById(R.id.scanbtn);
                    xcanbtb.setVisibility(View.INVISIBLE);

                    //  mp2.start();



                }
            }
        });
    }


    public void makevisble(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ImageButton person_image  = (ImageButton) findViewById(R.id.button_person);
                person_image.setVisibility(View.VISIBLE);
            }
        });

    }
}