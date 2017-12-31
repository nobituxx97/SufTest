package com.hit.nam.suftest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    Rect rectPre;
    SurfaceView surfaceView;
    TextView textView;
    Camera camera;
    SurfaceHolder surfaceHolder;
    Camera.PreviewCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    ImageView imgTest;
    TextView txtKQ;
    FramingRect framingRect;
    ViewfinderView viewfinderView;
    private int Measuredwidth = 0;
    private int Measuredheight = 0;

    OcrManager ocrManager;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        Point size = new Point();
        WindowManager w = getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            Measuredwidth = size.x;
            Measuredheight = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            Measuredwidth = d.getWidth();
            Measuredheight = d.getHeight();
        }

        framingRect = new FramingRect(MainActivity.this);


        viewfinderView = findViewById(R.id.viewfinderView);
        viewfinderView.setFramingRect(framingRect);

        txtKQ = findViewById(R.id.txtKQ);
        imgTest = findViewById(R.id.imgTest);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder =surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        jpegCallback = new Camera.PictureCallback() {
            @SuppressLint("WrongConstant")
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                FileOutputStream outStream = null;

                rectPre = framingRect.getRect(); //framingRect.getFramingRect();
                Log.d("rectPre",rectPre.toString());

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                    Log.d("bmw",bitmap.getWidth()  + "");


                android.hardware.Camera.Parameters parameters = camera.getParameters();
                android.hardware.Camera.Size size = parameters.getPictureSize();
                int height = size.height;
                int width = size.width;

                float scaleX = (float)(rectPre.right - rectPre.left)/Measuredwidth;
                float scaleY = (float)(rectPre.bottom - rectPre.top)/Measuredheight;
                Log.d("bmw",height  + "__" + width +"  "+ Measuredheight + "__" + Measuredwidth +"   " + scaleY  );

                    bitmap = Bitmap.createBitmap(bitmap,(int)(width*(1-scaleX)/2),(int)(height*(1-scaleY)/2) ,
                            (int)(width * (scaleX)),(int)(height * (scaleY)));
                   // bitmap  = renderCroppedGreyscaleBitmap(bytes);
                    //bytes = RGBLuminanceSource(bytes,bitmap);
                   //bitmap = ConvertToGrayscale(bitmap);
                   bitmap  = createBlackAndWhite(bitmap);
                    //bitmap = createBlackAndWhite(bitmap);
                    //Log.d("hihi",viewfinderView.getDisplay().getWidth() + "");

                    String KQ = ocrManager.startRecongnize(bitmap);
                    txtKQ.setText(KQ);
                    imgTest.setImageBitmap(bitmap);

                   // outStream = new  FileOutputStream(String.format("/sdcard/%d.jpg",System.currentTimeMillis()));
                    //outStream.write(bytes);
                    //outStream.close();



                //Toast.makeText(getApplicationContext(),"Picutr Saved",2000).show();
                refreshCamera();
            }

        };



    viewfinderView.setOnTouchListener(new View.OnTouchListener() {

        int lastX = -1;
        int lastY = -1;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.d("kkkkkk","aaaaaaa");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = -1;
                    lastY = -1;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int currentX = (int) event.getX();
                    int currentY = (int) event.getY();

                    try {
                        Rect rect = framingRect.getFramingRect();

                        final int BUFFER = 50;
                        final int BIG_BUFFER = 60;
                        if (lastX >= 0) {
                            // Adjust the size of the viewfinder rectangle. Check if the touch event occurs in the corner areas first, because the regions overlap.
                            if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left + BIG_BUFFER) || (lastX >= rect.left - BIG_BUFFER && lastX <= rect.left + BIG_BUFFER))
                                    && ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top - BIG_BUFFER) || (lastY <= rect.top + BIG_BUFFER && lastY >= rect.top - BIG_BUFFER))) {
                                // Top left corner: adjust both top and left sides
                                framingRect.adjustFramingRect( 2 * (lastX - currentX), 2 * (lastY - currentY));

                            } else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right + BIG_BUFFER) || (lastX >= rect.right - BIG_BUFFER && lastX <= rect.right + BIG_BUFFER))
                                    && ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top - BIG_BUFFER) || (lastY <= rect.top + BIG_BUFFER && lastY >= rect.top - BIG_BUFFER))) {
                                // Top right corner: adjust both top and right sides
                                framingRect.adjustFramingRect( 2 * (currentX - lastX), 2 * (lastY - currentY));

                            } else if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left + BIG_BUFFER) || (lastX >= rect.left - BIG_BUFFER && lastX <= rect.left + BIG_BUFFER))
                                    && ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom - BIG_BUFFER) || (lastY <= rect.bottom + BIG_BUFFER && lastY >= rect.bottom - BIG_BUFFER))) {
                                // Bottom left corner: adjust both bottom and left sides
                                framingRect.adjustFramingRect(2 * (lastX - currentX), 2 * (currentY - lastY));

                            } else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right + BIG_BUFFER) || (lastX >= rect.right - BIG_BUFFER && lastX <= rect.right + BIG_BUFFER))
                                    && ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom - BIG_BUFFER) || (lastY <= rect.bottom + BIG_BUFFER && lastY >= rect.bottom - BIG_BUFFER))) {
                                // Bottom right corner: adjust both bottom and right sides
                                framingRect.adjustFramingRect(2 * (currentX - lastX), 2 * (currentY - lastY));

                            } else if (((currentX >= rect.left - BUFFER && currentX <= rect.left + BUFFER) || (lastX >= rect.left - BUFFER && lastX <= rect.left + BUFFER))
                                    && ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
                                // Adjusting left side: event falls within BUFFER pixels of left side, and between top and bottom side limits
                                framingRect.adjustFramingRect(2 * (lastX - currentX), 0);

                            } else if (((currentX >= rect.right - BUFFER && currentX <= rect.right + BUFFER) || (lastX >= rect.right - BUFFER && lastX <= rect.right + BUFFER))
                                    && ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
                                // Adjusting right side: event falls within BUFFER pixels of right side, and between top and bottom side limits
                                framingRect.adjustFramingRect(2 * (currentX - lastX), 0);

                            } else if (((currentY <= rect.top + BUFFER && currentY >= rect.top - BUFFER) || (lastY <= rect.top + BUFFER && lastY >= rect.top - BUFFER))
                                    && ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
                                // Adjusting top side: event falls within BUFFER pixels of top side, and between left and right side limits
                                framingRect.adjustFramingRect(0, 2 * (lastY - currentY));

                            } else if (((currentY <= rect.bottom + BUFFER && currentY >= rect.bottom - BUFFER) || (lastY <= rect.bottom + BUFFER && lastY >= rect.bottom - BUFFER))
                                    && ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
                                // Adjusting bottom side: event falls within BUFFER pixels of bottom side, and between left and right side limits
                                framingRect.adjustFramingRect(0, 2 * (currentY - lastY));

                            }
                        }
                    } catch (NullPointerException e) {
                        Log.e("kkkk", "Framing rect not available", e);
                    }
                    view.invalidate();
                    lastX = currentX;
                    lastY = currentY;
                    return true;
                case MotionEvent.ACTION_UP:
                    lastX = -1;
                    lastY = -1;
                    return true;
            }
            return false;
        }
    });








        ocrManager = new OcrManager();
        ocrManager.initAPI();


    }
        private void refreshCamera() {
        if (surfaceHolder.getSurface() == null){
            return;
        }
        try {
            camera.stopPreview();
        }
        catch (Exception e){

        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e){

        }

    }




    public void captureImage(View view) {
        camera.takePicture(null,null,jpegCallback);
    }

    public void surfaceChanged(){

    }
    public static Bitmap createBlackAndWhite(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 150)
                    gray = 255;
                else
                    gray = 0;
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }
        return bmOut;
    }


    //xám
    public Bitmap ConvertToGrayscale(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        float[] arrayForColorMatrix = new float[] {0, 0, 1, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0};

        Bitmap.Config config = bitmap.getConfig();
        Bitmap grayScaleBitmap = Bitmap.createBitmap(width, height, config);

        Canvas c = new Canvas(grayScaleBitmap);
        Paint paint = new Paint();

        ColorMatrix matrix = new ColorMatrix(arrayForColorMatrix);
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);

        c.drawBitmap(bitmap, 0, 0, paint);

        return grayScaleBitmap;
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
        }
        catch (RuntimeException e){
            return;
        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();
       // camera.setDisplayOrientation(90);

       // parameters.setPreviewSize(288,352);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e){

        }
    }






    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {





        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
