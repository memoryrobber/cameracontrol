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

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.List;
import java.util.ListIterator;

import static android.support.constraint.Constraints.TAG;
import static org.opencv.core.CvType.CV_8UC1;

public class MeasureActivity extends AppCompatActivity {

    private MenuItem             mItemExit;
    private CameraCalibrator mCalibrator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_measure);
        TextView txt2  = findViewById(R.id.textView);
        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);

        Bundle bundle = this.getIntent().getExtras();
        String str = bundle.getString("text");
        txt2.setText(str);

        //读取图片处理并显示
        //读取图片，在框中显示原图
        Mat temp1 = new Mat();
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + "t" + ".jpg";
        Bitmap bitmap, bitmapResult;
        bitmap = BitmapFactory.decodeFile(fileName);
        bitmapResult = bitmap.copy(bitmap.getConfig(), true);
        Utils.bitmapToMat(bitmapResult, temp1);
        int mWidth = bitmapResult.getWidth();
        int mHeight = bitmapResult.getHeight();

        //标定器初始化
        mCalibrator = new CameraCalibrator(mWidth, mHeight);

        imageView1.setImageBitmap( bitmapResult );
        imageView1.invalidate();
        imageView1.setVisibility(View.VISIBLE);

        //显示二值化后的图
        Mat temp2 = new Mat();
        Mat temp21 = new Mat();
        Bitmap bmp2 = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Utils.bitmapToMat(bitmapResult, temp2);
        Imgproc.threshold(temp2,temp21,100,255,0);
        Utils.matToBitmap(temp21, bmp2);

        //其他的显示
        imageView2.setImageBitmap( bmp2 );
        imageView2.invalidate();
        imageView2.setVisibility(View.VISIBLE);

        //显示canny
        Mat temp3 = new Mat();
        Mat temp31 = new Mat();
        Bitmap bmp3 = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Utils.bitmapToMat(bitmapResult, temp3);
        Imgproc.Canny(temp3, temp31, 80, 90);
        Utils.matToBitmap(temp31, bmp3);



        //找棋盘格并显示
        Mat temp4 = new Mat();
        Mat temp41 = new Mat();
        Bitmap bmp4 = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Utils.bitmapToMat(bitmapResult, temp4);
        //把找到的角点绘到temp4.
        Mat temp22 = new Mat();
        Imgproc.cvtColor(temp21, temp22, Imgproc.COLOR_BGRA2GRAY, 4);
        mCalibrator.processFrame(temp22,temp1);

        Utils.matToBitmap(temp1, bmp4);

        //显示找角点前图片
        Utils.matToBitmap(temp22, bmp3);

        imageView3.setImageBitmap( bmp3 );
        imageView3.invalidate();
        imageView3.setVisibility(View.VISIBLE);

        //找角点后图片
        imageView4.setImageBitmap( bmp4);
        imageView4.invalidate();
        imageView4.setVisibility(View.VISIBLE);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItemExit = menu.add("返回");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemExit){
            Intent intent = new Intent(MeasureActivity.this,MainActivity.class);
            startActivityForResult(intent,0);
        }
        return true;
    }
    public void switch_page(View view){
        Log.i(TAG, "按钮被按下！" );
//        Intent intent = new Intent(MeasureActivity.this,MainActivity.class);
//        startActivityForResult(intent,0);
    }
}
