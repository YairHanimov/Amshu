package com.yair.amshu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.camera2.params.Face;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.Rectangle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
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

import static org.opencv.core.Core.flip;

public class MainActivity extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "MainActivity";

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
    private Rectangle rect1;
    private boolean flag=true;
    SharedPreferences sharedpreferences;
    MediaPlayer mp2 ;
    MediaPlayer mp1;
    int hitCounter=0;
    private int y=0;
    private boolean hitFlag =true;
    List<Point> pointsDeque = new ArrayList<Point>();
    List<Mat> frames=new ArrayList<>();
    private BaseLoaderCallback theLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    initializeOpenCVDependencies();

                    Log.i(TAG, "OpenCV loaded successfully");
//                    opencvcam.enableView();
//                    opencvcam.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.firstscreen);
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String channel = (shared.getString("key", ""));
        int i;
        if (channel != null) {
           i = Integer.parseInt(channel);
        }
        else {
             i = 0;
        }
        RatingBar simpleRatingBar1 = (RatingBar) findViewById(R.id.ratingBar);
        simpleRatingBar1.setRating(i);
         mp2 = MediaPlayer.create(this, R.raw.butten_finger_speach);
         mp1 = MediaPlayer.create(this, R.raw.speach_press_ball);



        //       setContentView(R.layout.activity_main);

//        opencvcam = (CameraBridgeViewBase) findViewById(R.id.mycamera);
//        opencvcam.setVisibility(SurfaceView.VISIBLE);
//        opencvcam.setCvCameraViewListener(this);
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

    public void onCameraViewStarted(int width, int height) {
        dst = new Mat();

        ditaction = new Coloralgo();
        thespectrum = new Mat();
        ballcolorrgb = new Scalar(255);
        ballcolorhsv = new Scalar(255);
        spectorsize = new Size(200, 64);
        counter = new Scalar(255,0,0,255);

    }

    public void onCameraViewStopped() {
        dst.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = dst.cols();
        int rows = dst.rows();

        int xOffset = (opencvcam.getWidth() - cols) / 2;
        int yOffset = (opencvcam.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

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

        return false; // don't need subsequent touch events
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        int minX=175;
        int maxX=625;
        int minY=0;
        int maxY=600;

        Mat rgba=inputFrame.rgba();

        Core.flip(rgba,rgba,0);
        int cols = rgba.cols(); //800
        int rows = rgba.rows();//600
        Mat m=Imgproc.getRotationMatrix2D(new Point(cols/2,rows/2),90,0.75);
        Imgproc.warpAffine(rgba, dst,m,rgba.size());
        //Imgproc.resize(dst, dst, dst.size());
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGBA2RGB);
        //Imgproc.medianBlur(dst,dst,5);

        frames.add(inputFrame.rgba());
        Mat diff=new Mat();
        if(frames.size()>1) {
            Core.absdiff(frames.get(0), frames.get(1), diff);
            Core.flip(diff,diff,0);
            Imgproc.warpAffine(diff, diff,m,diff.size());

            frames.remove(0);
            Mat gray = new Mat();
            Imgproc.cvtColor(diff, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.medianBlur(gray,gray,5);
            Imgproc.threshold(gray,gray,20,255,Imgproc.THRESH_BINARY);
            Mat kernel =Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(3,3));
            Imgproc.dilate(gray,gray,kernel);
            List<MatOfPoint> contours2=new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(gray,contours2,hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.rectangle(dst,new Point(600,200),new Point(500,100),new Scalar(255,5,255),1);

            Imgproc.rectangle(dst,new Point(600,200),new Point(500,200-y),new Scalar(255,5,255),-1);
            for(MatOfPoint cont:contours2) {
                Rect aa=Imgproc.boundingRect(cont);
                if(aa.x> 500 && aa.x<600 &&
                        aa.y > 100&& aa.y < 200){
                    if(y<100)
                        y+=5;
                    //Imgproc.putText(dst,"aaa",new Point(400,400),3,3,new Scalar(1,1,1));
                }
                if (Imgproc.contourArea(cont) > 700) {
                    //Imgproc.rectangle(dst,new Point(aa.x,aa.y),new Point(aa.x+aa.width,aa.y+aa.height),new Scalar(22,22,44));
                }
            }
        }



        if (colorselect) {
            afterballt();

            ditaction.process(dst);
            final List<MatOfPoint> contours = ditaction.getContours();
            Point center = new Point();
            for(MatOfPoint list:contours){
                center=Kmeans(list);
            }
            Imgproc.drawMarker(dst,center,new Scalar(255, 255, 0, 255));
            pointsDeque.add(center);
            if(pointsDeque.size()>=10)
                pointsDeque.remove(0);
            for(int i=0;i<pointsDeque.size()-1;i++){
                if(pointsDeque.get(i).x>0&&pointsDeque.get(i).y>0&&
                        pointsDeque.get(i+1).x>0&&pointsDeque.get(i+1).y>0)
                    Imgproc.line(dst,pointsDeque.get(i),pointsDeque.get(i+1),
                            new Scalar(141,222,23),2);
            }
            if(center.x> rect1.x && center.x<rect1.x+rect1.width &&
                    center.y > rect1.y&& center.y < rect1.y+rect1.height) {
                hitFlag =!hitFlag;
                hitCounter++;
            }
//            else if(hitFlag &&center.x>400){
////                hitFlag =!hitFlag;
////            }else if(!hitFlag &&center.x<400){
////                hitFlag =!hitFlag;
////            }

            Imgproc.putText(dst,String.valueOf(hitCounter),new Point(minX,100),
                    1,3,new Scalar(44,44,44));
            Imgproc.drawContours(dst, contours, -1, counter);

            Mat colorLabel = dst.submat(4, 68, 4, 68);
            colorLabel.setTo(ballcolorrgb);

            Mat spectrumLabel = dst.submat(4, 4 + thespectrum.rows(), 70, 70 + thespectrum.cols());
            thespectrum.copyTo(spectrumLabel);
        }



        MatOfRect faces = new MatOfRect();
        //absoluteFaceSize=Math.round(rows * 0.2f);

        // Use the classifier to detect faces
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(dst, faces, 1.1, 3, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

//        Imgproc.rectangle(thergba, new Point(minX,  minY),
//                new Point( maxX,  maxY), new Scalar(0, 255, 0, 255), 2);
        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i <facesArray.length; i++) {
            Rectangle rect=new Rectangle();
            if (hitFlag) {
                Imgproc.rectangle(dst, new Point(facesArray[i].x-100, facesArray[i].y+100),
                        new Point(facesArray[i].x, facesArray[i].y + 225)
                        , new Scalar(0, 255, 0, 255), 3);
                rect.setBounds(facesArray[i].x-100,facesArray[i].y+100,100,125);

            }
            else {
                Imgproc.rectangle(dst, new Point(facesArray[i].x+100, facesArray[i].y+100),
                        new Point(facesArray[i].x + 200, facesArray[i].y + 225)
                        , new Scalar(0, 0, 0, 255), 3);
                rect.setBounds(facesArray[i].x+100,facesArray[i].y+100,100,125);
            }
            setRectangle(rect);

        }
        return dst;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
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

        // And we are ready to go
        //   cameraBridgeViewBase.enableView();
    }

    public void setRectangle(Rectangle rect){
        this.rect1=rect;
    }
    public Point Kmeans(MatOfPoint list){
        Point center=new Point(0,0);
        for(int i=0;i<list.toList().size();i++){
            center.set(new double[]{list.toList().get(i).x+center.x, list.toList().get(i).y+center.y});
        }
        center.set(new double[]{center.x/list.toList().size(),center.y/list.toList().size()});
        return center;
    }

    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    setContentView(R.layout.activity_main);
                    opencvcam = (CameraBridgeViewBase) findViewById(R.id.mycamera);
                    opencvcam.setVisibility(SurfaceView.VISIBLE);
                    opencvcam.setCvCameraViewListener(this);
                    opencvcam.enableView();
                    opencvcam.setOnTouchListener(MainActivity.this);
                ((RadioButton) view).setChecked(false);
                //MediaPlayer mp = MediaPlayer.create(this, R.raw.speach_press_ball);
                mp1.start();
//                ImageButton person_image  = (ImageButton) findViewById(R.id.button);
//                person_image.setVisibility(View.INVISIBLE);
                break;
            case R.id.radioButton2:
                if (checked)
                    ((RadioButton) view).setChecked(false);
                    break;
            case R.id.radioButton3:
                if (checked)
                    ((RadioButton) view).setChecked(false);
                break;
        }
    }
    public  void  image_person_click(View c){
        ImageView ballvisbility  = (ImageView) findViewById(R.id.imageView8);

        if (ballvisbility.getVisibility() == View.VISIBLE) {
            // Its visible
        } else {
            ImageButton button = (ImageButton) c;
            SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String channel = (shared.getString("key", ""));
            button.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("key", "3");
            editor.commit();        }


    }
    public void  afterballt(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ImageView ballvisbility  = (ImageView) findViewById(R.id.imageView8);

                if (ballvisbility.getVisibility() == View.VISIBLE) {
                    makevisble();
                    ballvisbility.setVisibility(View.INVISIBLE);
                    mp2.start();



                }
            }
        });
    }


    public void makevisble(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ImageButton person_image  = (ImageButton) findViewById(R.id.button);
                person_image.setVisibility(View.VISIBLE);
            }
        });

    }
}
