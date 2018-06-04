package com.example.cameracontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

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

    public static Bitmap BitmapGaussianBlur(Bitmap bmp1, int size) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Bitmap bmp2 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp1, mat1);
        Imgproc.GaussianBlur(mat1,mat2,new Size(size,size),0);
        Utils.matToBitmap(mat2, bmp2);
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

    public static Bitmap BitmapErosion(Bitmap bmp1){
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Bitmap bmp2 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp1, mat1);
        int erosion_size = 5;
        Mat element = Imgproc.getStructuringElement(MORPH_RECT,
                new Size(2 * erosion_size + 1, 2 * erosion_size + 1),
                new Point(erosion_size, erosion_size));
        Imgproc.erode(mat1,mat2,element);

        Utils.matToBitmap(mat2, bmp2);
        return bmp2;
    }

    public static Bitmap BitmapDilation(Bitmap bmp1){
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Bitmap bmp2 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp1, mat1);
        int dilation_size = 5;
        Mat element = Imgproc.getStructuringElement(MORPH_RECT,
                new Size(2 * dilation_size + 1, 2 * dilation_size + 1),
                new Point(dilation_size, dilation_size));
        Imgproc.dilate(mat1,mat2,element);

        Utils.matToBitmap(mat2, bmp2);
        return bmp2;
    }
}
