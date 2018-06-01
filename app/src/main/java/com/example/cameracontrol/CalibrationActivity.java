package com.example.cameracontrol;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static android.support.constraint.Constraints.TAG;
import static com.example.cameracontrol.BitmapHelper.BitmapBgra2MatGray;
import static com.example.cameracontrol.BitmapHelper.BitmapCanny;
import static com.example.cameracontrol.BitmapHelper.BitmapThreshold;
import static com.example.cameracontrol.BitmapHelper.ReadBitmap;
import static com.example.cameracontrol.BitmapHelper.ShowBitmap;

public class CalibrationActivity extends AppCompatActivity {

    private MenuItem mItemExit;
    private CameraCalibrator mCalibrator;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;

    TextView text_calibration_result;
    ProgressBar progressBar;

    private Bitmap bmpshow = null;
    private String strshow = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calibration);

        text_calibration_result = findViewById(R.id.text_calibration_result);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(7);//设置最大进度

        //标定器初始化
        String fileName11= Environment.getExternalStorageDirectory().getPath() + "/" + "1"+ ".jpg";
        Bitmap bitmap11;
        bitmap11 = ReadBitmap(fileName11);
        int mWidth = bitmap11.getWidth();
        int mHeight = bitmap11.getHeight();
        mCalibrator = new CameraCalibrator(mWidth, mHeight);

        //显示4张图片,显示前需要初始化标定器
        Show4Pic();
    }

    public void Show4Pic() {
        //读取图片
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".jpg";
        Bitmap bitmap;
        bitmap = ReadBitmap(fileName);

        //显示原图
        Bitmap bmp1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView1, bmp1);

        //显示二值化后的图
        Bitmap bmp2 = BitmapThreshold(bmp1, 100);
        ShowBitmap(imageView2, bmp2);

        //显示canny
        Bitmap bmp3 = BitmapCanny(bmp1, 80, 90);
        ShowBitmap(imageView3, bmp3);


        //找棋盘格并显示
        Mat MatSrc = new Mat();       //读取到的原图
        Mat MatProcessGray = new Mat();     //灰度图
        Utils.bitmapToMat(bmp1, MatSrc);    //原图
        Imgproc.cvtColor(MatSrc, MatProcessGray, Imgproc.COLOR_BGRA2GRAY, 4);

        //把角点绘制到图片
        mCalibrator.processFrame(MatProcessGray, MatSrc);
        Bitmap bmp4 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(MatSrc, bmp4);
        ShowBitmap(imageView4, bmp4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItemExit = menu.add("返回");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemExit) {
            Intent intent = new Intent(CalibrationActivity.this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x333:
                    progressBar.setProgress(msg.arg1);
                    break;
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable  runnableUi1 = new  Runnable(){
        @Override
        public void run() {
            //更新界面
            ShowBitmap(CalibrationActivity.this.imageView3, bmpshow);
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable  runnableUi2 = new  Runnable(){
        @Override
        public void run() {
            //更新界面
            text_calibration_result.setText(strshow);
        }
    };


    public void calibration(View view){
        if(CalibrationResult.tryLoad(this, mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients()))
        {
            Log.i(TAG, "读取标定参数...");
            double error =  mCalibrator.getAvgReprojectionError();

            double[] x00 =  mCalibrator.getCameraMatrix().get(0,0);
            double[] x01 =  mCalibrator.getCameraMatrix().get(0,1);
            double[] x02 =  mCalibrator.getCameraMatrix().get(0,2);

            double[] x10 =  mCalibrator.getCameraMatrix().get(1,0);
            double[] x11 =  mCalibrator.getCameraMatrix().get(1,1);
            double[] x12 =  mCalibrator.getCameraMatrix().get(1,2);

            double[] x20 =  mCalibrator.getCameraMatrix().get(2,0);
            double[] x21 =  mCalibrator.getCameraMatrix().get(2,1);
            double[] x22 =  mCalibrator.getCameraMatrix().get(2,2);

            double[] d0 = mCalibrator.getDistortionCoefficients().get(0,0);
            double[] d1 = mCalibrator.getDistortionCoefficients().get(1,0);
            double[] d2 = mCalibrator.getDistortionCoefficients().get(2,0);
            double[] d3 = mCalibrator.getDistortionCoefficients().get(3,0);
            double[] d4 = mCalibrator.getDistortionCoefficients().get(4,0);
            double[] d5 = mCalibrator.getDistortionCoefficients().get(5,0);
            double[] d6 = mCalibrator.getDistortionCoefficients().get(6,0);
            double[] d7 = mCalibrator.getDistortionCoefficients().get(7,0);

            String str11 = "标定参数：\n" + String.valueOf(x00[0]) + "\t\t" + String.valueOf(x01[0]) + "\t\t" +String.valueOf(x02[0]) + "\n"
                    + String.valueOf(x10[0]) + "\t\t" + String.valueOf(x11[0]) + "\t\t" +String.valueOf(x12[0]) + "\n"
                    + String.valueOf(x20[0]) + "\t\t" + String.valueOf(x21[0]) + "\t\t" +String.valueOf(x22[0]) + "\n"
                    + "畸变参数:" + "\n"
                    + String.valueOf(d0[0]) + "\t\t" + String.valueOf(d1[0]) + "\t\t"+ String.valueOf(d2[0])
                    + "\t\t"+ String.valueOf(d3[0]) + "\t\t"+ String.valueOf(d4[0]) + "\t\t"+ String.valueOf(d5[0])
                    + "\t\t"+ String.valueOf(d6[0]) + "\t\t"+ String.valueOf(d7[0]) + "\n"
                    + "平均重投影误差：" + String.valueOf(error);

            text_calibration_result.setText(str11);

            String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".jpg";
            Bitmap bitmap;
            bitmap = ReadBitmap(fileName);
            Mat MatSrc = new Mat();     //灰度图
            Mat MatDst = new Mat();     //灰度图
            Utils.bitmapToMat(bitmap, MatSrc);    //原图


            //Calib3d.undistortImage(MatSrc,MatDst,camera_matrix,distortion_coefficients_matrix);
            Imgproc.undistort(MatSrc, MatDst, mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());

            Bitmap bmp3 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.matToBitmap(MatDst, bmp3);
            ShowBitmap(imageView3, bmp3);

        }else {
            new Thread(new Runnable() {
                public void run() {

                    String[] str = {"1", "2", "3", "4", "5", "6", "7"};
                    for (int i = 0; i < 7; i++) {
                        Log.i(TAG, "读取第" + i + "张图片");
                        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + str[i] + ".jpg";
                        Bitmap bitmap;
                        bitmap = ReadBitmap(fileName);
                        Mat MatProcessGray;     //灰度图
                        MatProcessGray = BitmapBgra2MatGray(bitmap);

                        mCalibrator.findPattern(MatProcessGray);
                        Log.i(TAG, "角点已经找到，正在加入角点vector...");
                        mCalibrator.addCorners();

                        Message msg1 = new Message();
                        msg1.what = 0x333;
                        msg1.arg1 = i + 1;
                        handler.sendMessage(msg1);
                    }
                    Log.i(TAG, "所以角点已经找到，正在标定...");
                    mCalibrator.calibrate();

                    if (mCalibrator.isCalibrated()) {
                        Mat camera_matrix = mCalibrator.getCameraMatrix();
                        Mat distortion_coefficients_matrix = mCalibrator.getDistortionCoefficients();
                        CalibrationResult.save(CalibrationActivity.this,
                                mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());
                        Log.i(TAG, "保存标定参数...");

            double error =  mCalibrator.getAvgReprojectionError();

            double[] x00 =  camera_matrix.get(0,0);
            double[] x01 =  camera_matrix.get(0,1);
            double[] x02 =  camera_matrix.get(0,2);

            double[] x10 =  camera_matrix.get(1,0);
            double[] x11 =  camera_matrix.get(1,1);
            double[] x12 =  camera_matrix.get(1,2);

            double[] x20 =  camera_matrix.get(2,0);
            double[] x21 =  camera_matrix.get(2,1);
            double[] x22 =  camera_matrix.get(2,2);

            double[] d0 = distortion_coefficients_matrix.get(0,0);
            double[] d1 = distortion_coefficients_matrix.get(1,0);
            double[] d2 = distortion_coefficients_matrix.get(2,0);
            double[] d3 = distortion_coefficients_matrix.get(3,0);
            double[] d4 = distortion_coefficients_matrix.get(4,0);
            double[] d5 = distortion_coefficients_matrix.get(5,0);
            double[] d6 = distortion_coefficients_matrix.get(6,0);
            double[] d7 = distortion_coefficients_matrix.get(7,0);

            String str2 = "标定参数：\n" + String.valueOf(x00[0]) + "\t\t" + String.valueOf(x01[0]) + "\t\t" +String.valueOf(x02[0]) + "\n"
                    + String.valueOf(x10[0]) + "\t\t" + String.valueOf(x11[0]) + "\t\t" +String.valueOf(x12[0]) + "\n"
                    + String.valueOf(x20[0]) + "\t\t" + String.valueOf(x21[0]) + "\t\t" +String.valueOf(x22[0]) + "\n"
                    + "畸变参数:" + "\n"
                    + String.valueOf(d0[0]) + "\t\t" + String.valueOf(d1[0]) + "\t\t"+ String.valueOf(d2[0])
                    + "\t\t"+ String.valueOf(d3[0]) + "\t\t"+ String.valueOf(d4[0]) + "\t\t"+ String.valueOf(d5[0])
                    + "\t\t"+ String.valueOf(d6[0]) + "\t\t"+ String.valueOf(d7[0]) + "\n"
                    + "平均重投影误差：" + String.valueOf(error);

                        //String str2 = String.valueOf(camera_matrix.get(0, 0)[0]);// + distortion_coefficients_matrix.toString();
                        strshow = str2;
                        handler.post(runnableUi2);

                        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".jpg";
                        Bitmap bitmap;
                        bitmap = ReadBitmap(fileName);
                        Mat MatSrc = new Mat();     //灰度图
                        Mat MatDst = new Mat();     //灰度图
                        Utils.bitmapToMat(bitmap, MatSrc);    //原图


                        //Calib3d.undistortImage(MatSrc,MatDst,camera_matrix,distortion_coefficients_matrix);
                        Imgproc.undistort(MatSrc, MatDst, camera_matrix, distortion_coefficients_matrix);

                        Bitmap bmp3 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Utils.matToBitmap(MatDst, bmp3);
                        bmpshow = bmp3;
                        handler.post(runnableUi1);

                        //ShowBitmap(CalibrationActivity.this.imageView3, bmp3);
                    }
                }
            }).start();
        }
    }
}
