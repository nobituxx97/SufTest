package com.hit.nam.suftest;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Dell ALL on 30/12/2017.
 */

public class OcrManager {
    TessBaseAPI baseAPI = null;
    public  void initAPI(){
         baseAPI = new TessBaseAPI();

        String dataPath = MainApplication.instance.getTessDataParentDirectory();
        baseAPI.init(dataPath,"eng");
    }
    public String startRecongnize(Bitmap bm){
        if (baseAPI == null){
            initAPI();
        }
        baseAPI.setImage(bm);
        return baseAPI.getUTF8Text();

    }
}
