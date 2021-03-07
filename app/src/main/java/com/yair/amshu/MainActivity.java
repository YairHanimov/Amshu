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
import java.util.List;

import static org.opencv.core.Core.flip;

public class MainActivity extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "MainActivity";

    private boolean colorselect = false;
    private Mat thergba, thespectrum;
    private Scalar ballcolorrgb, ballcolorhsv, counter;
    private Coloralgo ditaction;
    private Size spectorsize;
    private CascadeClassifier cascadeClassifier;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private CameraBridgeViewBase opencvcam;
    Mat mat1;
    private int absoluteFaceSize;
    private Rectangle R1;
    private boolean flag=true;
    SharedPreferences sharedpreferences;
    MediaPlayer mp2 ;
    MediaPlayer mp1;

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
        int i = Integer.parseInt(channel);
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
        thergba = new Mat(height, width, CvType.CV_8UC4);
        mat1=new Mat(width,height, CvType.CV_8UC4);

        ditaction = new Coloralgo();
        thespectrum = new Mat();
        ballcolorrgb = new Scalar(255);
        ballcolorhsv = new Scalar(255);
        spectorsize = new Size(200, 64);
        counter = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        thergba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = thergba.cols();
        int rows = thergba.rows();

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

        Mat touchedRegionRgba = thergba.submat(touchedRect);

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
        mat1=thergba;
        Mat mRgbaT = mat1.t();
        flip(mat1.t(), mRgbaT, 0);
        Imgproc.resize(mRgbaT, mRgbaT, mat1.size());

        Imgproc.cvtColor(mRgbaT, mRgbaT, Imgproc.COLOR_RGBA2RGB);
        //Point aa;
        thergba = inputFrame.rgba();
        if (colorselect) {


           afterballt();



            ditaction.process(thergba);
            final List<MatOfPoint> contours = ditaction.getContours();
            Thread thread = new Thread() {

                @Override
                public void run() {
                    try {
                        Point aa=contours.get(0).toList().get(0);
                        Point center = new Point();
                        for(MatOfPoint list:contours){
                            center=Kmeans(list);
                        }
//                        Log.i("Creation", "aa"+String.valueOf(R1.x));
//                        Log.i("Creation", String.valueOf(R1.x-R1.width));
//                        Log.e(TAG, "Contours count: " + contours.size());
                        Imgproc.drawMarker(thergba,center,new Scalar(255, 255, 0, 255));
                        if(center.x<R1.y*8/6 && center.x>(R1.y-R1.height)*8/6 &&
                                center.y < R1.x*6/8&& center.y > (R1.x-R1.width)*6/8) {
                            Log.i("Creation","kkkkkkk" + aa);
                            flag=!flag;
                        }
                        }
                     catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(thergba, contours, -1, counter);

            Mat colorLabel = thergba.submat(4, 68, 4, 68);
            colorLabel.setTo(ballcolorrgb);

            Mat spectrumLabel = thergba.submat(4, 4 + thespectrum.rows(), 70, 70 + thespectrum.cols());
            thespectrum.copyTo(spectrumLabel);
        }



        MatOfRect faces = new MatOfRect();

        // Use the classifier to detect faces
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(mRgbaT, faces, 1.1, 3, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

//        Imgproc.rectangle(mRgbaT, new Point(550,  200),
//                new Point( 350,  300), new Scalar(0, 255, 0, 255), 3);
        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i <facesArray.length; i++) {
            Rectangle rect=new Rectangle();
            if (flag) {
                Imgproc.rectangle(mRgbaT, new Point(facesArray[i].x + 400, facesArray[i].y + 100),
                        new Point(facesArray[i].x + 200, facesArray[i].y + 250)
                        , new Scalar(0, 255, 0, 255), 3);
                rect.setBounds(facesArray[i].x+400,facesArray[i].y+100,200,150);
                setRectangle(rect);
            }
            else {
                Imgproc.rectangle(mRgbaT, new Point(facesArray[i].x - 50, facesArray[i].y + 100),
                        new Point(facesArray[i].x - 250, facesArray[i].y + 250),
                        new Scalar(255, 255, 0, 255), 3);
                rect.setBounds(facesArray[i].x - 50,facesArray[i].y+100,200,150);
                setRectangle(rect);
            }


        }
        Mat mRgbaT2 = mRgbaT.t();
        Imgproc.resize(mRgbaT2, mRgbaT2, mRgbaT.size());
        flip(mRgbaT2,mRgbaT2, 1);

        return mRgbaT2;


        // return thergba;
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
        this.R1=rect;
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
