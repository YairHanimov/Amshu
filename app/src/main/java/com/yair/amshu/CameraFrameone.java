package com.yair.amshu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.constraintlayout.solver.widgets.Rectangle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class CameraFrameone  extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2  {
    private static final String  TAG              = "CameraFrameone";
    private boolean colorselect = false;
    private Mat dst, thespectrum;
    private Scalar ballcolorrgb, ballcolorhsv, counter;
    private Coloralgo ditaction;
    private Size spectorsize;
    private CascadeClassifier cascadeClassifier;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private Mat oldFrame;
    private CameraBridgeViewBase opencvcam;
    Mat mat1;
    private int absoluteFaceSize;
    private Rectangle rect1,rect2;
    private Rect aaa,aaa2;
    private boolean hitFlag =true,flag=true, countBackFlag =false,faceDetecFlag=false;
    public  CountDownTimer remainingTimeCounter;
    SharedPreferences sharedpreferences;
    scoremanager scoremanage1;
    // MediaPlayer mp2 ;
    // MediaPlayer mp1;
    int hitCounter=0;
    int lag_crash=0;
    int a = 0,b=0,c=0,d=0;
    List<Point> pointsDeque = new ArrayList<Point>();
    List<List<Point>> pointsDequeList=new ArrayList<>();
    ArrayList<Mat> frames1=new ArrayList<>();
    ArrayList<Mat> frames2=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_frameone);
        opencvcam = (CameraBridgeViewBase) findViewById(R.id.mycamera);
        opencvcam.setVisibility(SurfaceView.VISIBLE);
        opencvcam.setCvCameraViewListener(this);
        scoremanage1 = new scoremanager(this);

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
        rect1=new Rectangle();
        rect2=new Rectangle();
        aaa=new Rect();
        aaa2=new Rect();
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

    @Override
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
        //score.setText(String.valueOf(hitCounter));
        score.setText(String.valueOf(scoremanage1.get_score()));

//        TextView score   = (TextView) findViewById(R.id.score_counter_xml);
//        score.setText(String.valueOf(hitCounter));
        if(countBackFlag) {
            faceDetection();
        }

        if(colorselect) {
            if(!faceDetecFlag){
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
                Imgproc.putText(dst, "only 1 person allow", new Point(dst.rows() / 2, dst.rows() / 2), 1, 2, new Scalar(0, 0, 0));
                break;
            }
            if (flag) {
                c = facesArray[i].width * 2 / 3;
                d = facesArray[i].height * 2 / 3;
                flag = false;
            }
            a = (facesArray[i].x > 0) ? facesArray[i].x : 0;
            b = (facesArray[i].y - d > 0) ? facesArray[i].y - d : 0;
            rect1.setBounds(a - c, b, c, d);
            rect2.setBounds(a + c, b, c, d);
            faceDetecFlag=true;
            if (hitFlag) {
                Imgproc.rectangle(dst, new Point(a - c, b), new Point(a, b + d), new Scalar(0, 0, 255), 3);
            } else {
                Imgproc.rectangle(dst, new Point(a + c, b), new Point(a + 2 * c, b + d), new Scalar(0, 0, 255), 3);
            }
        }
    }
    public void runGame(){


        List<MatOfPoint> contours,contours2;

        aaa.set(rect1.x, rect1.y, rect1.width, rect1.height);
        aaa2.set(rect2.x, rect2.y, rect2.width, rect2.height);
        Mat roi = dst.submat(aaa);
        Mat roi2 = dst.submat(aaa2);
        contours=MovementDetection(roi,frames1);
        contours2=MovementDetection(roi2,frames2);
        if (contours.size()>0&&hitFlag) {
            //hitCounter++;
            scoremanage1.addscore(1);
            hitFlag=false;
            remainingTimeCounter.cancel();
            remainingTimeCounter.start();


        }
        if (contours2.size()>0&&!hitFlag) {
           // hitCounter++;
            scoremanage1.addscore(1);
            hitFlag=true;
            remainingTimeCounter.cancel();
            remainingTimeCounter.start();

        }

        //return res;
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



    public List<MatOfPoint> MovementDetection(Mat roi,List<Mat> frames) {
        Mat diff=new Mat();
        Mat gray = new Mat();
        Mat mask=new Mat();
        Mat res=new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.cvtColor(roi, gray, Imgproc.COLOR_RGB2HSV_FULL);
        Core.inRange(gray, ditaction.getLowBound(), ditaction.getUpBound(), mask);
        Core.bitwise_and(roi, roi, res, mask);
        frames.add(res);
        if (frames.size() != 2) return contours;

        Core.absdiff(frames.get(0), frames.get(1), diff);
        frames.remove(0);
        Imgproc.cvtColor(diff, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Imgproc.threshold(gray, gray, 20, 255, Imgproc.THRESH_BINARY);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(gray, gray, kernel);
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        frames.clear();
        return contours;

    }


    public void show_vid_one(View view) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                findViewById(R.id.videoView2).setVisibility(View.VISIBLE);
                findViewById(R.id.scanbtn).setVisibility(View.INVISIBLE);

                VideoView videoView = (VideoView)findViewById(R.id.videoView2);
                videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.test);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        findViewById(R.id.videoView2).setVisibility(View.INVISIBLE);
                        findViewById(R.id.scanbtn).setVisibility(View.VISIBLE);

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
                remainingTimeCounter.start();

            }

        }.start();

         remainingTimeCounter =  new CountDownTimer(3000, 1000) {
            TextView timerscore   = (TextView) findViewById(R.id.timescoretest);
            public void onTick(long millisUntilFinished) {
                timerscore.setText( String.valueOf((int)+(millisUntilFinished / 1000)));
            }

            public void onFinish() {
                scoremanage1.addscore(-1);
                this.start(); //start again the CountDownTimer
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