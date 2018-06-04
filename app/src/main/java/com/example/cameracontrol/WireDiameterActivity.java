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
import static com.example.cameracontrol.BitmapHelper.BitmapDilation;
import static com.example.cameracontrol.BitmapHelper.BitmapErosion;
import static com.example.cameracontrol.BitmapHelper.BitmapGaussianBlur;
import static com.example.cameracontrol.BitmapHelper.BitmapThreshold;
import static com.example.cameracontrol.BitmapHelper.ReadBitmap;
import static com.example.cameracontrol.BitmapHelper.ShowBitmap;
import static com.example.cameracontrol.WireAlgorithm.findCircle1;
import static org.opencv.core.CvType.CV_8UC3;

public class WireDiameterActivity extends AppCompatActivity {

    private MenuItem mItemExit;
    ImageView imageView;
    TextView textView;

    Bitmap bmp_result;

    List<MatOfPoint> contours_all_pre = new ArrayList<>();    //原始轮廓
    List<MatOfPoint> contours_all = new ArrayList<>();    //处理后轮廓

    List<PointPair> point_pair_List = new ArrayList<>();    //找到的点对


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wirediameter);

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
            Intent intent = new Intent(WireDiameterActivity.this, MainActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    public void read_pic(View view) {
        long startTime = System.currentTimeMillis();
        Toast.makeText(this, "读取图片按钮被按下", Toast.LENGTH_SHORT).show();

        String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/wirediameter/" + 1 + ".jpg";
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

        //从图像找到两段轮廓
        long startTime = System.currentTimeMillis();

        Bitmap bitmap = bmp_result.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bmp1 = BitmapBgra2BitmapGray(bitmap);         //3通道变1通道
        Bitmap bmp2 = BitmapGaussianBlur(bmp1,9);
        Bitmap bmp3 = BitmapThreshold(bmp2, 100);    //阈值

        Bitmap bmp4 = BitmapErosion(bmp3);
        Bitmap bmp5 = BitmapDilation(bmp4);

        //找轮廓
        Mat matSrc = new Mat();
        Utils.bitmapToMat(bmp5,matSrc);
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(matSrc,matSrc,Imgproc.COLOR_BGRA2GRAY);
        Imgproc.findContours(matSrc,contours_all_pre ,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE, new Point(0,0));


        //轮廓截断，选择
        double top = matSrc.size().height / 8;
        double left = matSrc.size().width / 8;
        double height = 3 * matSrc.size().height / 4;
        double width = 3 * matSrc.size().width / 4;

        //从轮廓中选择最长的两条，并且x，y应该在范围内
        MatOfPoint temp_contour = new MatOfPoint();
        for(int i = 0;i<contours_all_pre.get(1).size().height;i++){
            Mat temp_point = new Mat();
            Mat.eye(1, 1, CvType.CV_32SC2).copyTo(temp_point);
            double[] x = contours_all_pre.get(1).get(i,0);
            if (contours_all_pre.get(1).get(i,0)[0] > left &&
                    contours_all_pre.get(1).get(i,0)[0] < left+width &&
                    contours_all_pre.get(1).get(i,0)[1] > top &&
                    contours_all_pre.get(1).get(i,0)[1] < top+height) {
                temp_point.put(0, 0, x);
                temp_contour.push_back(temp_point);
            }
        }
        MatOfPoint aa1 = new MatOfPoint(temp_contour);
        contours_all.add(aa1);

        temp_contour.release();
        temp_contour = new MatOfPoint();
        for(int i = 0;i<contours_all_pre.get(2).size().height;i++){
            Mat temp_point = new Mat();
            Mat.eye(1, 1, CvType.CV_32SC2).copyTo(temp_point);
            double[] x = contours_all_pre.get(2).get(i,0);
            if (contours_all_pre.get(2).get(i,0)[0] > left &&
                    contours_all_pre.get(2).get(i,0)[0] < left+width &&
                    contours_all_pre.get(2).get(i,0)[1] > top &&
                    contours_all_pre.get(2).get(i,0)[1] < top+height) {
                temp_point.put(0, 0, x);
                temp_contour.push_back(temp_point);
            }
        }
        MatOfPoint aa2 = new MatOfPoint(temp_contour);
        contours_all.add(aa2);

        //画出处理后的轮廓
        Mat matDst = Mat.zeros(matSrc.size(),CV_8UC3);
        Imgproc.drawContours(matDst,contours_all,-1,new Scalar(255,0,0),10);
        Utils.matToBitmap(matDst,bitmap);
        bmp_result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView, bitmap);

        //找到距离最近的点对
        for (int i = 0; i < contours_all.get(0).size().height; i += 100)
        {
            int minindex = 0;
            double mindis = 100000;
            for (int j = 0; j < contours_all.get(1).size().height; j++)
            {
                double dis = 0;
                double x0 = contours_all.get(0).get(i,0)[0];
                double y0 = contours_all.get(0).get(i,0)[1];
                double x = contours_all.get(1).get(j,0)[0];
                double y = contours_all.get(1).get(j,0)[1];
                dis =Math.sqrt((x0 - x)*(x0 - x) + (y0 - y)*(y0 - y));
                if (dis < mindis)
                {
                    minindex = j;
                    mindis = dis;
                }
            }

            PointPair temp_point_pair = new PointPair();
            temp_point_pair.setP1(new Point(contours_all.get(0).get(i,0)[0], contours_all.get(0).get(i,0)[1]));
            temp_point_pair.setP2(new Point(contours_all.get(1).get(minindex,0)[0],contours_all.get(1).get(minindex,0)[1]));
            temp_point_pair.setDis(mindis);
            point_pair_List.add(temp_point_pair);
        }


        //显示运行时间
        long endTime = System.currentTimeMillis();
        String str = "运行时间:" + (endTime - startTime) + "ms";
        textView.setText(str);
    }

    //半径结果
    public void match_result(View view) {
        Bitmap bitmap = bmp_result.copy(Bitmap.Config.ARGB_8888, true);
        Mat matSrc = new Mat();
        Utils.bitmapToMat(bitmap,matSrc);

        //画点对
        Mat matDst = Mat.zeros(matSrc.size(),CV_8UC3);
        for(int i = 0;i<point_pair_List.size();i++){
            Imgproc.line(matDst,point_pair_List.get(i).getP1(),point_pair_List.get(i).getP2(),new Scalar(0,255,255),4);
        }
        //画轮廓
        Imgproc.drawContours(matDst,contours_all,-1,new Scalar(255,0,0),10);
        Utils.matToBitmap(matDst,bitmap);
        bmp_result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        ShowBitmap(imageView, bitmap);

        double sum = 0;
        for (int i = 0; i < point_pair_List.size(); i++) {
            sum += point_pair_List.get(i).getDis();
        }
        String str = "平均直径是:" + (int)(sum / point_pair_List.size()) + "像素";
        textView.setText(str);
    }

}
