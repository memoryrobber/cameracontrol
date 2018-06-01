package com.example.cameracontrol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.example.cameracontrol.BitmapHelper.BitmapBgra2BitmapGray;
import static com.example.cameracontrol.BitmapHelper.BitmapBgra2MatGray;
import static com.example.cameracontrol.BitmapHelper.BitmapThreshold;
import static com.example.cameracontrol.BitmapHelper.ShowBitmap;
import static com.example.cameracontrol.BitmapHelper.ReadBitmap;
import static org.opencv.core.CvType.CV_8UC3;


public class MeasureActivity extends AppCompatActivity {

    private CameraCalibrator mCalibrator;
    private MenuItem mItemExit;

    ImageView imageView;
    TextView textView;

    Bitmap bmpresult;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_measure);

        textView = findViewById(R.id.textView);
        imageView= findViewById(R.id.imageView);

        //标定器初始化
        String fileName11= Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".bmp";
        Bitmap bitmap11;
        bitmap11 = ReadBitmap(fileName11);
        bmpresult = bitmap11.copy(Bitmap.Config.ARGB_8888, true);
        int mWidth = bitmap11.getWidth();
        int mHeight = bitmap11.getHeight();
        mCalibrator = new CameraCalibrator(mWidth, mHeight);
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
            Intent intent = new Intent(MeasureActivity.this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    public void read_pic(View view) {
        Toast.makeText(this, "读取图片按钮被按下", Toast.LENGTH_SHORT).show();
        textView.setText("读取图片");
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".jpg";
        Bitmap bitmap;
        bitmap = ReadBitmap(fileName);

        //显示原图
        Bitmap bmp1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bmpresult = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView, bmp1);
    }

    public void threshold(View view){
        textView.setText("二值化");
        Bitmap bitmap = bmpresult.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bmp1 = BitmapBgra2BitmapGray(bitmap);         //3通道变1通道
        Bitmap bmp2 = BitmapThreshold(bmp1, 100);    //阈值
        //平滑
        bmpresult = bmp2.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView, bmp2);
    }

    //找轮廓
    public void find_contours(View view){
        Bitmap bitmap = bmpresult.copy(Bitmap.Config.ARGB_8888, true);
        Mat matSrc = new Mat();
        Utils.bitmapToMat(bitmap,matSrc);

        Mat matDst = Mat.zeros(matSrc.size(),CV_8UC3);

        Imgproc.cvtColor(matSrc,matSrc,Imgproc.COLOR_BGRA2GRAY);
        //找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(matSrc,contours ,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE, new Point(0,0));

        Imgproc.drawContours(matDst,contours,-1,new Scalar(255,0,0),2);

        Utils.matToBitmap(matDst,bitmap);
        ShowBitmap(imageView, bitmap);
    }

}
