package com.example.cameracontrol;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;
//import org.opencv.core.Size;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//不显示标题栏的原因，从Activity 改成 AppCompatActivity
//不再使用implements , OnTouchListener
//不再使用 implements CvCameraViewListener2
public class MainActivity extends AppCompatActivity{
    private static final String TAG = "OCVSample::Activity";

    String SavePath;

    //private MyJavaCameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    private boolean takepic;
    private static Mat save_mat;
    private static Mat show_mat;
    private static boolean PicSavedState;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    //mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        //setHasOptionsMenu(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

//        mOpenCvCameraView = (MyJavaCameraView) findViewById(R.id.my_java_surface_view);
//
//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//
//        mOpenCvCameraView.setCvCameraViewListener(this);
//
//        takepic = false;
//        PicSavedState = false;
    }


    @Override
    public void onPause() {
        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
        //mOpenCvCameraView.getResolution()
    }

//    public void onCameraViewStarted(int width, int height) {
//        //mOpenCvCameraView.setrotation();
//
//    }
//
//    public void onCameraViewStopped() {
//    }
//
//    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
////        Mat dst = new Mat();
////        Mat gray = inputFrame.gray();
////        Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(gray.rows()/2,gray.cols()/2), -90, 1);
////        Imgproc.warpAffine(gray, dst, rotateMat, dst.size());
//
//        if(this.takepic == true){
//            Toast.makeText(this," 123456", Toast.LENGTH_SHORT).show();
//            //保存inputFrame
//            save_mat = inputFrame.rgba();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//            String currentDateandTime = sdf.format(new Date());
////            String fileName = Environment.getExternalStorageDirectory().getPath() +
////                    "/sample_picture_" + currentDateandTime + ".jpg";
//
//            String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + SavePath + ".jpg";
//            Imgcodecs.imwrite(fileName, save_mat);
//            this.PicSavedState = false;
//            this.takepic = false;
//        }
//
////        show_mat = inputFrame.gray();
////
////        //清理内存，否则程序会报错
//////        dst.release();
//////        gray.release();
//////        rotateMat.release();
////
////        Mat rgbaInnerWindow;
////        Mat  mIntermediateMat = new Mat();
////        Mat rgba = show_mat;
////        int rows = (int) rgba.height();
////        int cols = (int) rgba.width();
////
////        int left = 0;
////        int top = 0;
////
////        int width = cols;
////        int height = rows;
////
////        //图像处理
////        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
////        Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
////        Imgproc.threshold(mIntermediateMat,rgbaInnerWindow,100,255,0);
////        Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
////        show_mat =  rgbaInnerWindow.clone();
////
////        //清理内存，否则程序会报错
////        rgbaInnerWindow.release();
////        mIntermediateMat.release();
////        rgba.release();
//
//        return inputFrame.rgba();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        List<String> effects = mOpenCvCameraView.getEffectList();
//
//        if (effects == null) {
//            Log.e(TAG, "Color effects are not supported by device!");
//            return true;
//        }
//
//        mColorEffectsMenu = menu.addSubMenu("Color Effect");
//        mEffectMenuItems = new MenuItem[effects.size()];
//
//        int idx = 0;
//        ListIterator<String> effectItr = effects.listIterator();
//        while (effectItr.hasNext()) {
//            String element = effectItr.next();
//            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
//            idx++;
//        }
//
//        mResolutionMenu = menu.addSubMenu("Resolution");
//        mResolutionList = mOpenCvCameraView.getResolutionList();
//        mResolutionMenuItems = new MenuItem[mResolutionList.size()];
//
//        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
//        idx = 0;
//        while (resolutionItr.hasNext()) {
//            Size element = resolutionItr.next();
//            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
//                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
//            idx++;
//        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//        if (item.getGroupId() == 1) {
//            mOpenCvCameraView.setEffect((String) item.getTitle());
//            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
//        } else if (item.getGroupId() == 2) {
//            int id = item.getItemId();
//            Size resolution = mResolutionList.get(id);
//            mOpenCvCameraView.setResolution(resolution);
//            resolution = mOpenCvCameraView.getResolution();
//            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
//            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
//        }

        return true;
    }

//    @SuppressLint("SimpleDateFormat")
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        //如果路径还未设置，开始设置路径
//        if(this.PicSavedState == false) {
//
//            Log.i(TAG, "onTouch event show dialog");
//
//            //设置相机参数,不管用
//            Camera.Parameters x = mOpenCvCameraView.getpara();
//            x.setJpegQuality(100);
//            //x.setPictureSize(1920, 1080);
//            x.setPictureSize(4160,3120);
//            x.setPictureFormat(256);
//            x.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
//
////        //拍照
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
////        String currentDateandTime = sdf.format(new Date());
////        String fileName = Environment.getExternalStorageDirectory().getPath() +
////                "/sample_picture_" + currentDateandTime + ".jpg";
////        mOpenCvCameraView.takePicture(fileName);
////        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
//
//
////        //保存图片的分辨率不会变但是图像会变暗？？？
////        Size resolution = mOpenCvCameraView.getResolution();
////        resolution.height = 1080;
////        resolution.width = 1920;
////        mOpenCvCameraView.setResolution(resolution);
//
//
//            //弹出diglog设置路径
//            fireCustomDialog();
//        } else {
//            //如果路径已经设置，保存图片。
//            Log.i(TAG, "onTouch event save pic");
//            Toast.makeText(this, "save picture", Toast.LENGTH_SHORT).show();
//            this.takepic = true;
//        }
//        return false;
//    }

    //触摸弹出对话框输入文件名不会保存打开对话框前的图片。
    //保存文件时弹出对话框，不能弹出？
    //如何弹出对话框的demo
//    private void fireCustomDialog() {
//        // custom dialog
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_custom);
//        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
//        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
//        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
//        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
//        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
//        //final boolean isEditOperation = (reminder != null);
//        //this is for an edit
//        if (false) {
//            titleView.setText("Edit Reminder");
//            checkBox.setChecked(false);
//            editCustom.setText("text");
//            rootLayout.setBackgroundColor(getResources().getColor(R.color.green));
//        }
//        commitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SavePath = editCustom.getText().toString();
//                PicSavedState = true;
//                dialog.dismiss();
//            }
//
//        });
//        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    public void take_pic(View view){
        Log.i(TAG, "定焦拍照按钮被按下");
        Toast.makeText(this, "定焦拍照按钮被按下", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this,TakePIcActivity.class);
        startActivity(intent);
    }

    //在主页面进行拍照，现在已经取消
//    public void take_pic(View view){
//        //如果路径还未设置，开始设置路径
//        if(this.PicSavedState == false) {
//
//            Log.i(TAG, "onTouch event show dialog");
//
//            //设置相机参数,不管用
//            Camera.Parameters x = mOpenCvCameraView.getpara();
//            x.setJpegQuality(100);
//            //x.setPictureSize(1920, 1080);
//            x.setPictureSize(4160,3120);
//            x.setPictureFormat(256);
//            x.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
//
////        //拍照
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
////        String currentDateandTime = sdf.format(new Date());
////        String fileName = Environment.getExternalStorageDirectory().getPath() +
////                "/sample_picture_" + currentDateandTime + ".jpg";
////        mOpenCvCameraView.takePicture(fileName);
////        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
//
//
////        //保存图片的分辨率不会变但是图像会变暗？？？
////        Size resolution = mOpenCvCameraView.getResolution();
////        resolution.height = 1080;
////        resolution.width = 1920;
////        mOpenCvCameraView.setResolution(resolution);
//
//
//            //弹出diglog设置路径
//            fireCustomDialog();
//        } else {
//            //如果路径已经设置，保存图片。
//            Log.i(TAG, "onTouch event save pic");
//            Toast.makeText(this, "save picture", Toast.LENGTH_SHORT).show();
//            this.takepic = true;
//        }
//    }

    public void calibration_page(View view){
        Toast.makeText(this, "标定拍照按钮被按下", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,CalibrationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("text","要发送的消息");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void measure_page(View view){
        Toast.makeText(this, "测量拍照按钮被按下", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,MeasureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("text","要发送的消息");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

