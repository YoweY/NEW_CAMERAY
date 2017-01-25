package com.jamieyao.cameray.Camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jamieyao.cameray.Camera.TextureView.MyTextureView;
import com.jamieyao.cameray.R;
import com.jamieyao.cameray.Util.StorageUtil;
import com.jamieyao.cameray.Camera.ui.AnimTmpView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by jamie.yao_cp on 2017/1/6.
 */

public class TextureContainer extends RelativeLayout implements AnimTmpView.AnimtionEndListener{
    public final static String TAG = "TextureContainer";

    public MyTextureView mTextureView;
    //private TempImageView mTempImageView;

    /** 触摸屏幕时显示的聚焦图案  */
    private FocusView mFocusImageView;
    private TextView mRecordingTimeTextView;
    private SimpleDateFormat mTimeFormat;
    private AnimTmpView mTmpView;
    public PictureTakedListener mPictureTakedListener;
    public AnimTmpView.AnimtionEndListener mAnimtionEndListener;
    private Handler mHandler;

    public static interface PictureTakedListener{
        public void onTakePictureEnd();
    }

   public TextureContainer(Context context) {
        super(context);
        inflate(context, R.layout.texture_container, this);
        mTextureView=(MyTextureView) findViewById(R.id.texture_view);
        mFocusImageView = (FocusView)findViewById(R.id.focusImageView);
       setOnTouchListener(new OnTouchListener()
       {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               switch (event.getAction() & MotionEvent.ACTION_MASK) {
                   case MotionEvent.ACTION_DOWN:
                       Log.e(TAG, "YYYY_ACTION_DOWN");
                       Point point = new Point((int) event.getX(), (int) event.getY());
                       //mTextureView.onFocus(point, autoFocusCallback);//focus
                       mFocusImageView.startFocus(point);//focusview
                       break;
                   case MotionEvent.ACTION_UP:
                       Log.e(TAG, "YYYY_ACTION_UP2");
                       //设置聚焦
                       //Point point = new Point((int) event.getX(), (int) event.getY());
                       //mTextureView.onFocus(point, autoFocusCallback);
                       //mFocusImageView.startFocus(point);
                       break;
               }
               return false;
           }
       });
       //setOnTouchListener(new TouchListener());
        //mTempImageView=(TempImageView) findViewById(R.id.temp_image);
    }

    public TextureContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.texture_container, this);
        mHandler=new Handler();
        mTimeFormat=new SimpleDateFormat("mm:ss", Locale.getDefault());

        mTextureView=(MyTextureView) findViewById(R.id.texture_view);
        mFocusImageView = (FocusView)findViewById(R.id.focusImageView);
        mRecordingTimeTextView = (TextView) findViewById(R.id.recordTime);
        mTmpView = (AnimTmpView) findViewById(R.id.anim_image);

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "YYYY_ACTION_DOWN");
                        Point point = new Point((int) event.getX(), (int) event.getY());
                        mTextureView.onFocus(point, autoFocusCallback);//focus
                        mFocusImageView.startFocus(point);//focusview
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "YYYY_ACTION_UP2");
                        //设置聚焦
                        //Point point = new Point((int) event.getX(), (int) event.getY());
                        //mTextureView.onFocus(point, autoFocusCallback);
                        //mFocusImageView.startFocus(point);
                        break;
                }
                return false;
            }
        });
        //mTempImageView=(TempImageView) findViewById(R.id.temp_image);
    }

    public void takePicture(PictureTakedListener listener)
    {
        this.mPictureTakedListener = listener;
        mTextureView.takePicture(mShutterCallback, mJpegPictureCallback);
    }

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                //camera.stopPreview();
                //isPreviewing = false;
            }
            Bitmap rotaBitmap = getRotateBitmap(b, 90.0f);
            StorageUtil.saveBitmap(rotaBitmap);


            mTmpView.setListener(mAnimtionEndListener);
            mTmpView.isVideo(true);
            mTmpView.setImageBitmap(rotaBitmap);
            mTmpView.startAnimation(R.anim.tempview_show);
            camera.startPreview();
            if(mPictureTakedListener!=null) mPictureTakedListener.onTakePictureEnd();
            //isPreviewing = true;
        }
    };

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    /**
     *   前置、后置摄像头转换
     */
    public void switchCamera(){
        mTextureView.switchCamera();
    }

    private final Camera.AutoFocusCallback autoFocusCallback=new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            if (success) {
                mFocusImageView.onFocusSuccess();
            }else {
                //聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
                mFocusImageView.onFocusFailed();
            }
        }
    };


    public MyTextureView.FlashMode getFlashMode() {
        return mTextureView.getFlashMode();
    }

    public void setFlashMode(MyTextureView.FlashMode flashMode) {
        mTextureView.setFlashMode(flashMode);
    }



    private long mRecordStartTime;

    public boolean startRecord(){
        mRecordStartTime = SystemClock.uptimeMillis();
        mRecordingTimeTextView.setVisibility(View.VISIBLE);
        mRecordingTimeTextView.setText("00:00");
        if(mTextureView.startRecord()){
            mHandler.postAtTime(recordRunnable, mRecordingTimeTextView, SystemClock.uptimeMillis()+1000);
            return true;
        }else {
            return false;
        }
    }

    Runnable recordRunnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(mTextureView.isRecording()){
                long recordTime=SystemClock.uptimeMillis()-mRecordStartTime;
                mRecordingTimeTextView.setText(mTimeFormat.format(new Date(recordTime)));
                mHandler.postAtTime(this,mRecordingTimeTextView, SystemClock.uptimeMillis()+500);
            }else {
                mRecordingTimeTextView.setVisibility(View.GONE);
            }
        }
    };

    public Bitmap stopRecord(){
        mRecordingTimeTextView.setVisibility(View.GONE);
        Bitmap thumbnailBitmap = mTextureView.stopRecord();
        if(thumbnailBitmap!=null){
            mTmpView.setListener(mAnimtionEndListener);
            mTmpView.isVideo(true);
            mTmpView.setImageBitmap(thumbnailBitmap);
            mTmpView.startAnimation(R.anim.tempview_show);
        }
        return thumbnailBitmap;
    }

    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
        if(bm!=null){
            //生成缩略图
            Bitmap thumbnail= ThumbnailUtils.extractThumbnail(bm, 213, 213);
            mThumbView.setImageBitmap(thumbnail);
            if(isVideo)
                mVideoIconView.setVisibility(View.VISIBLE);
            else {
                mVideoIconView.setVisibility(View.GONE);
            }
        }
    }

}