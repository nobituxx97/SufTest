package com.hit.nam.suftest;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Dell ALL on 31/12/2017.
 */

public class FramingRect {



    private int screenResolutionX;
    private int screenResolutionY;

    private Rect rect = null;
    private Rect rectPreview;
    private Context context;
    private Display display;

    public int getscreenResolutionX() {
        return screenResolutionX;
    }

    public int getscreenResolutionY() {
        return screenResolutionY;
    }

    public Rect getRect() {
        return rect;
    }

    public Rect getRectPreview() {
        return rectPreview;
    }

    public FramingRect(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = manager.getDefaultDisplay();
        screenResolutionX = display.getWidth();
        screenResolutionY = display.getHeight();

    }


    public synchronized Rect getFramingRect() {

        int width = screenResolutionX * 3 / 5;
        int height = screenResolutionY * 2 / 5;
        int leftOffset = (screenResolutionX - width) / 2;
        int topOffset = (screenResolutionY - height) / 2;
        if (rect==null) {
            rect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d("rectbef111111", rect.toString());
        }
        Log.d("frame1",rect.toString());
        return rect;
    }

    public synchronized void adjustFramingRect(int deltaWidth, int deltaHeight) {

        // Set maximum and minimum sizes
        if ((rect.width() + deltaWidth > screenResolutionX - 4) || (rect.width() + deltaWidth < 50)) {
            deltaWidth = 0;
        }
        if ((rect.height() + deltaHeight > screenResolutionY - 4) || (rect.height() + deltaHeight < 50)) {
            deltaHeight = 0;
        }

        int newWidth = rect.width() + deltaWidth;
        int newHeight = rect.height() + deltaHeight;
        int leftOffset = (screenResolutionX - newWidth) / 2;
        int topOffset = (screenResolutionY - newHeight) / 2;
        rect = new Rect(leftOffset, topOffset, leftOffset + newWidth, topOffset + newHeight);
        Log.d("rectbef",rect.toString());
    }
}
