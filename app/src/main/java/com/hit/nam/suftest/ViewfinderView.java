/*
 * Copyright (C) 2008 ZXing authors
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hit.nam.suftest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the result text.
 *
 *
 *Chế độ xem này được phủ lên trên đầu trang xem trước máy ảnh. Nó bổ sung hình chữ nhật ngắm và một phần
 *minh bạch bên ngoài, cũng như văn bản kết quả.
 *
 * The code for this class was adapted from the ZXing project: http://code.google.com/p/zxing
 */
public final class ViewfinderView extends View {

  private  MainActivity mainActivity;
  Display display;
  private Paint paint;
  private int maskColor;
  private int frameColor;
  private int cornerColor;
  //  Rect bounds;
  private Rect previewFrame;
  private Rect rect;
  private  FramingRect framingRect = null;
  @Override
  public Display getDisplay() {
    return display;
  }

  public ViewfinderView(Context context) {
    super(context);
  }

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    //Khởi tạo một lần cho hiệu suất hơn là gọi cho họ mỗi lần trong onDraw ()
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);//k có răng cưa
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.colorAccent);//màu nền ria ngoài
    frameColor = resources.getColor(R.color.colorPrimary);//màu ria khung
    cornerColor = resources.getColor(R.color.colorPrimaryDark);

    if (framingRect == null)
    framingRect = new FramingRect(context);

    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    display = manager.getDefaultDisplay();
   // Log.d("we",framingRect.getCameraRosolutionX() + "__");

    //    bounds = new Rect();
    previewFrame = new Rect();
    rect = new Rect();
  }
  public void setFramingRect(FramingRect framingRect) {
    this.framingRect = framingRect;
  }

  @SuppressWarnings("unused")
  @Override
  public void onDraw(Canvas canvas) {

    Rect frame = framingRect.getFramingRect();
    Log.d("frame",frame.toString());
    if (frame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);



    // Draw a two pixel solid border inside the framing rect
    //Vẽ 4 cạnh
    paint.setAlpha(0);
    paint.setStyle(Style.FILL);
    paint.setColor(frameColor);
    canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
    canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
    canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
    canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

    // Draw the framing rect corner UI elements
    //Vẽ 4 góc vuông màu trắng
    paint.setColor(cornerColor);
    canvas.drawRect(frame.left - 15, frame.top - 15, frame.left + 15, frame.top, paint);
    canvas.drawRect(frame.left - 15, frame.top, frame.left, frame.top + 15, paint);
    canvas.drawRect(frame.right - 15, frame.top - 15, frame.right + 15, frame.top, paint);
    canvas.drawRect(frame.right, frame.top - 15, frame.right + 15, frame.top + 15, paint);
    canvas.drawRect(frame.left - 15, frame.bottom, frame.left + 15, frame.bottom + 15, paint);
    canvas.drawRect(frame.left - 15, frame.bottom - 15, frame.left, frame.bottom, paint);
    canvas.drawRect(frame.right - 15, frame.bottom, frame.right + 15, frame.bottom + 15, paint);
    canvas.drawRect(frame.right, frame.bottom - 15, frame.right + 15, frame.bottom + 15, paint);

  }

  public void drawViewfinder() {
    invalidate();
  }

  /**
   * Adds the given OCR results for drawing to the view.
   *
   * @param text Object containing OCR-derived text and corresponding data.
   */

}
