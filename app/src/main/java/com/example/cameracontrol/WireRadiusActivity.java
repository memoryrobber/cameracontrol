package com.example.cameracontrol;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;
import static com.example.cameracontrol.BitmapHelper.BitmapBgra2BitmapGray;
import static com.example.cameracontrol.BitmapHelper.BitmapThreshold;
import static com.example.cameracontrol.BitmapHelper.ReadBitmap;
import static com.example.cameracontrol.BitmapHelper.ShowBitmap;
import static com.example.cameracontrol.BitmapHelper.BitmapGaussianBlur;
import static com.example.cameracontrol.WireAlgorithm.findCircle1;
import static org.opencv.core.CvType.CV_8UC3;


public class WireRadiusActivity extends AppCompatActivity {

    private MenuItem mItemExit;

    ImageView imageView;
    TextView textView;

    Bitmap bmp_result;

    List<CircleData> CDList = new ArrayList<>();

    List<MatOfPoint> contours_all = new ArrayList<>();    //原始轮廓
    int max_index = 0;                                     //原始最长轮廓编号
    List<MatOfPoint> contours = new ArrayList<>();        //分段后轮廓

    int min_index = 0;     //最小圆半径序号
    CircleData min_CD = new CircleData();    //最小圆结构体

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wireradius);

        textView = findViewById(R.id.textView);
        imageView= findViewById(R.id.imageView);

        //标定器初始化
        String fileName11= Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".bmp";
        Bitmap bitmap11;
        bitmap11 = ReadBitmap(fileName11);
        bmp_result = bitmap11.copy(Bitmap.Config.ARGB_8888, true);
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
            Intent intent = new Intent(WireRadiusActivity.this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    public void read_pic(View view) {
        long startTime = System.currentTimeMillis();
        Toast.makeText(this, "读取图片按钮被按下", Toast.LENGTH_SHORT).show();

        String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/wireradius/" + 2 + ".jpg";
        Bitmap bitmap;
        bitmap = ReadBitmap(fileName);

        //显示原图
        Bitmap bmp1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bmp_result = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView, bmp1);

        long endTime = System.currentTimeMillis();
        String str = "运行时间:" + (endTime - startTime) + "ms";
        textView.setText(str);
    }

    //轮廓结果
    public void contour_result(View view){
        contours_all.clear();
        contours.clear();

        long startTime = System.currentTimeMillis();
        textView.setText("二值化");
        Bitmap bitmap = bmp_result.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bmp1 = BitmapBgra2BitmapGray(bitmap);         //3通道变1通道
        Bitmap bmp2 = BitmapGaussianBlur(bmp1,3);
        Bitmap bmp3 = BitmapThreshold(bmp2, 70);    //阈值

        //找轮廓
        Mat matSrc = new Mat();
        Utils.bitmapToMat(bmp3,matSrc);

        Mat hierarchy = new Mat();
        Imgproc.cvtColor(matSrc,matSrc,Imgproc.COLOR_BGRA2GRAY);
        Imgproc.findContours(matSrc,contours_all ,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE, new Point(0,0));

        //轮廓截断，选择
        double top = matSrc.size().height / 8;
        double left = matSrc.size().width / 8;
        double height = 3 * matSrc.size().height / 4;
        double width = 3 * matSrc.size().width / 4;

        //选择最长的轮廓
        MatOfPoint contour = new MatOfPoint();

        double maxsize = 0;
        for (int i = 0; i < contours_all.size(); i++)
        {
            if(contours_all.get(i).size().height > maxsize)
            {
                maxsize = contours_all.get(i).size().height;
                max_index = i;
                contour = contours_all.get(i);
            }
        }

        //轮廓分段
        int wire_size = 200;       //半径小于10拟合不出圆
        MatOfPoint temp_contour = new MatOfPoint();
        for (int i = 0; i < contour.size().height; i += wire_size)
        {
            temp_contour.release();
            temp_contour = new MatOfPoint();

            for (int j = 0; j < wire_size; j++)
            {
                if (i + j < contour.size().height)
                {
                    Mat temp_point = new Mat();
                    Mat.eye(1, 1, CvType.CV_32SC2).copyTo(temp_point);
                    double[] x = contour.get(i+j,0);
                    temp_point.put(0,0,x);
                    temp_contour.push_back(temp_point);
                }
                else
                {
                    break;
                }
            }
            //contours.add(temp_contour);
            MatOfPoint aa = new MatOfPoint(temp_contour);
            contours.add(aa);
        }

        //每段找拟合圆
        Mat matDst = Mat.zeros(matSrc.size(),CV_8UC3);
        double minvalue = 100000;
        for (int i = 0; i < contours.size()-1; i++)
        {
            double[] a1 = contours.get(i).get(0,0);
            double[] a2 = contours.get(i).get(wire_size/2-1,0);
            double[] a3 = contours.get(i).get(wire_size-1,0);
            Point pt1 = new Point(a1[0],a1[1]);
            Point pt2 = new Point(a2[0],a2[1]);
            Point pt3 = new Point(a3[0],a3[1]);

            CircleData CD;
            CD = findCircle1(pt1, pt2, pt3);
            CDList.add(CD);
            double rec = 20000;

            if (CD.getRadius() > 0 && CD.getCenter().x < rec && CD.getCenter().x > -rec && CD.getCenter().y < rec && CD.getCenter().y > -rec
                    && pt1.x < left+width && pt1.x > left && pt1.y < top + height && pt1.y > top
                    && pt2.x < left + width && pt2.x > left && pt2.y < top + height && pt2.y > top
                    && pt3.x < left + width && pt3.x > left && pt3.y < top + height && pt3.y > top)
            {
                Random random = new Random((int) System.currentTimeMillis());
                Scalar scalar = new Scalar(random.nextInt() % 255, random.nextInt() % 255, random.nextInt() % 255);
                Imgproc.circle(matDst,CD.getCenter(),(int)CD.getRadius(),scalar,8);
                if(CD.getRadius()<minvalue) {
                    minvalue = CD.getRadius();
                    min_CD = CD;
                }
            }
            else
            {
                //cout << "半径不是正数或者三点在边缘,半径是" << CD.radius <<",第" <<i << "个轮廓" << endl;
            }
        }

        Utils.matToBitmap(matDst,bitmap);
        ShowBitmap(imageView, bitmap);

        //显示运行时间
        long endTime = System.currentTimeMillis();
        String str = "运行时间:" + (endTime - startTime) + "ms";
        textView.setText(str);
    }

    //半径结果
    public void radius_result(View view){

        String str = "The minimum radius is:" + ((int)min_CD.getRadius()) + "pixels";
        textView.setText(str);

        Mat matSrc = new Mat();
        Utils.bitmapToMat(bmp_result,matSrc);
        Mat matDst = Mat.zeros(matSrc.size(),CV_8UC3);

        Imgproc.drawContours(matDst,contours_all,max_index,new Scalar(255,0,0),10);

        Imgproc.circle(matDst,min_CD.getCenter(),(int)min_CD.getRadius(),new Scalar(255,255,0),10);

        Imgproc.putText(matDst,str,min_CD.getCenter(),1,5,new Scalar(0,255,0),2);

        Bitmap bitmap = bmp_result.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(matDst,bitmap);
        ShowBitmap(imageView, bitmap);
    }

}
