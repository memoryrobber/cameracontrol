package com.example.cameracontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BitmapHelper {
    public static void ShowBitmap(ImageView imageView, Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
        imageView.setVisibility(View.VISIBLE);
    }

    public static Bitmap ReadBitmap(String filepath){
        return BitmapFactory.decodeFile(filepath);
    }

    public  static Mat BitmapBgra2MatGray(Bitmap bitmap){
        Mat MatSrc = new Mat();       //读取到的原图
        Mat MatProcessGray = new Mat();     //灰度图
        Utils.bitmapToMat(bitmap, MatSrc);    //原图
        Imgproc.cvtColor(MatSrc, MatProcessGray, Imgproc.COLOR_BGRA2GRAY, 4);
        return MatProcessGray;
    }

    public  static Bitmap BitmapBgra2BitmapGray(Bitmap bitmap){
        Mat MatSrc = new Mat();       //读取到的原图
        Mat MatProcessGray = new Mat();     //灰度图
        Utils.bitmapToMat(bitmap, MatSrc);    //原图
        Imgproc.cvtColor(MatSrc, MatProcessGray, Imgproc.COLOR_BGRA2GRAY, 4);
        Bitmap bmp2 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.matToBitmap(MatProcessGray, bmp2);
        return bmp2;
    }


    public static Bitmap BitmapThreshold(Bitmap bmp1, int thresh) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Bitmap bmp2 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp1, mat1);
        Imgproc.threshold(mat1, mat2, thresh, 255, 0);
        Utils.matToBitmap(mat2, bmp2);
        return bmp2;
    }

    public static Bitmap BitmapCanny(Bitmap bmp1, int thresh1, int thresh2) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Bitmap bmp2 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp1, mat1);
        Imgproc.Canny(mat1, mat2, thresh1, thresh2);
        Utils.matToBitmap(mat2, bmp2);
        return bmp2;
    }
}
