package com.company.iendo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shuyu on 2016/11/11.
 */

public class CommonUtil {

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    /**
     * seekbar数值范围1-2.5
     * socket传值是0-15
     * 获取需要设置到bar里面去的数值   更具获取到的设备参数
     * /显示是1到2.5倍,传值是0--15
     *
     * @param str 0--15  socket获取到的设备参数
     * @return RangeBar 需要设置的值
     */
    public static float getRangeBarData(String str) {
        switch (str) {
            case "0":
                return 1;
            case "1":
                return (float) 1.1;
            case "2":
                return (float) 1.2;
            case "3":
                return (float) 1.3;
            case "4":
                return (float) 1.4;
            case "5":
                return (float) 1.5;
            case "6":
                return (float) 1.6;
            case "7":
                return (float) 1.7;
            case "8":
                return (float) 1.8;
            case "9":
                return (float) 1.9;
            case "10":
                return 2.0f;
            case "11":
                return 2.1f;
            case "12":
                return 2.2f;
            case "13":
                return 2.3f;
            case "14":
                return 2.4f;
            case "15":
                return 2.5f;


        }

        return 5;
    }


    /**
     *      * seekbar数值范围1-2.5
     *      * socket传值是0-15
     * 获取需要设置到bar里面去的数值   更具获取到的设备参数
     * /显示是1到2.5倍,传值是0--15
     *
     * @param str 0--15  1-2.5
     * @return 发送socket需要传输的数据0--15
     */
    public static int getSocketToSendData(String str) {
        switch (str) {
            case "0":
                return 0;
            case "1":
                return 0;
            case "1.0":
                return 0;
            case "1.1":
                return 1;
            case "1.2":
                return 2;
            case "1.3":
                return 3;
            case "1.4":
                return 4;
            case "1.5":
                return 5;
            case "1.6":
                return 6;
            case "1.7":
                return 7;
            case "1.8":
                return 8;
            case "1.9":
                return 9;
            case "2.0":
                return 10;
            case "2.1":
                return 11;
            case "2.2":
                return 12;
            case "2.3":
                return 13;
            case "2.4":
                return 14;
            case "2.5":
                return 15;


        }

        return 5;
    }

    public synchronized static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 将长度转换为时间
     *
     * @param timeMs
     * @return
     */
    public static String stringForTime(int timeMs) {  //18565
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }


    }

    public static String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static void setViewHeight(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (null == layoutParams)
            return;
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        if (bitmap != null) {
            File file = new File(getPath(), "GSY-" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream;
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmap.recycle();
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        //显示软键盘
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static String getPath() {
        String path = getAppPath(NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String NAME = "GSYVideo";

    public static String getAppPath(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(SD_PATH);
        sb.append(File.separator);
        sb.append(name);
        sb.append(File.separator);
        return sb.toString();
    }


    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(final EditText mEditText, final Context mContext) {

        //必须要等UI绘制完成之后，打开软键盘的代码才能生效，所以要设置一个延时
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 500);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
