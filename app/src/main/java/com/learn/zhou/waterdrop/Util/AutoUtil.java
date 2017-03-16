package com.learn.zhou.waterdrop.Util;

import android.content.Context;
import android.view.WindowManager;

import com.fanyu.litelibrary.lite;
import com.learn.zhou.waterdrop.MyApplication;

//获取屏幕尺寸属性

public class AutoUtil {
    static int width = 0;
    static int height = 0;
    static int statusBar = -1;

    public static float px2x(float px) {
        getWH();
        return px * width / 1080;
    }

    public static float px2y(float px) {
        getWH();
        return px * height / 1920;
    }

    public static float px2yStatus(float px){
        getWH();
        return px * (height-statusBar) / 1920;
    }

    private static void getWH() {
        if (width == 0 || height == 0) {
            WindowManager wm = (WindowManager) lite.appContext()
                    .getSystemService(Context.WINDOW_SERVICE);

            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
  //          LogUtil.i("AutoUtil-", "width=" + width);
  //          LogUtil.i("AutoUtil-", "height=" + height);
        }
        if (statusBar == -1){
            //获取status_bar_height资源的ID
            int resourceId = lite.appContext().getResources().getIdentifier(
                    "status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBar = lite.appContext().getResources().getDimensionPixelSize(resourceId);
            }
   //         LogUtil.i("AutoUtil-", "statusBar=" + statusBar);
        }
    }
}
