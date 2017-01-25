package com.jamieyao.cameray.Camera.TextureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.widget.Toast;
import com.jamieyao.cameray.Util.StorageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie.yao_cp on 2017/1/13.
 */

public class MyTextureView extends TextureView implements TextureView.SurfaceTextureListener{

    public final static String TAG = "MyTextureView";

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private boolean mIsFrontCamera;
    private int mOrientation=0;
    private MediaRecorder mMediaRecorder;
    /** 当前闪光灯类型，默认为关闭 */
    private FlashMode mFlashMode=FlashMode.ON;
    /** 当前缩放级别  默认为0*/
    private int mZoom=0;
    /**  相机配置，在录像前记录，用以录像结束后恢复原配置 */
    private Camera.Parameters mParameters;
    /**  录像存放路径 ，用以生成缩略图*/
    private String mRecordPath=null;

    public MyTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
        //openCamera();
        mIsFrontCamera=false;
    }

    public MyTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
        //openCamera();
        mIsFrontCamera=false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int var2, int var3) {
        Log.i(TAG, "camera_one  available");
        try {
            if (mCamera == null) {
                openCamera();
            }
            mSurfaceTexture = surface;
            setCameraParameters();
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            Toast.makeText(getContext(), "open camera error", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
        mCamera.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int var2, int var3) {
        Log.i(TAG, "camera_one  onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "camera_one  onSurfaceTextureDestroyed");
        if (mCamera != null) {
            mCamera.release();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //Log.i(TAG,"camera_one  onSurfaceTextureUpdated");
    }


    /**
     *   根据当前照相机状态(前置或后置)，打开对应相机
     */
    private boolean openCamera()  {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mIsFrontCamera){
            Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
            /*
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if(cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                    try {
                        mCamera=Camera.open(1);
                    } catch (Exception e) {
                        mCamera =null;
                        return false;
                    }
                }
            }*/
            mCamera=Camera.open(1);
        }else {
            try {
                mCamera=Camera.open();
            } catch (Exception e) {
                mCamera =null;
                return false;
            }
        }
        return true;
    }

    public void takePicture(Camera.ShutterCallback  shutterCallback ,Camera.PictureCallback callback){
        mCamera.takePicture(shutterCallback, null, callback);
    }


    public boolean isRecording(){
        return mMediaRecorder!=null;
    }

    /**
     *  开始录像
     *  @return 开始录像是否成功
     */
    public boolean startRecord(){
        if(mCamera==null)
            openCamera();
        if (mCamera==null) {
            return false;
        }
        if(mMediaRecorder==null)
            mMediaRecorder = new MediaRecorder();
        else
            mMediaRecorder.reset();
        mParameters=mCamera.getParameters();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder
                .setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder
                .setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置录像参数，由于应用需要此处取一个较小格式的视频
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        //设置输出视频朝向，便于播放器识别。由于是竖屏录制，需要正转90°
        mMediaRecorder.setOrientationHint(90);
        String path = StorageUtil.getFolderPath(getContext(), StorageUtil.TYPE_VIDEO ,"tmp");

        File directory = new File(path);
        if(!directory.exists())
            directory.mkdirs();
        if(!directory.exists())
            Log.e(TAG, "YYYYYY_" + path);
        try {
            String name="video"+StorageUtil.createFileNmae(".3gp");
            mRecordPath = path + File.separator + name;
            File mRecAudioFile = new File(mRecordPath);
            mMediaRecorder.setOutputFile(mRecAudioFile
                    .getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Bitmap stopRecord(){
        Bitmap bitmap=null;
        try {
            if(mMediaRecorder!=null){
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder=null;
                //保存视频的缩略图
                //bitmap = saveThumbnail();
            }
            if(mParameters!=null&&mCamera!=null){
                //重新连接相机
                mCamera.reconnect();
                //停止预览，注意这里必须先调用停止预览再设置参数才有效
                mCamera.stopPreview();
                //设置参数为录像前的参数，不然如果录像是低配，结束录制后预览效果还是低配画面
                mCamera.setParameters(mParameters);
                //重新打开
                mCamera.startPreview();
                mParameters=null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     *   转换前置和后置照相机
     */
    public void switchCamera(){
        mIsFrontCamera=!mIsFrontCamera;
        openCamera();
        if(mCamera!=null){
            //setCameraParameters();
            //updateCameraOrientation();
            try {
                setCameraParameters();
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置照相机参数
     */
    private void setCameraParameters(){
        Camera.Parameters parameters = mCamera.getParameters();
        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (sizeList.size()>0) {
            Camera.Size cameraSize=sizeList.get(0);
            //预览图片大小
            parameters.setPreviewSize(cameraSize.width, cameraSize.height);
        }

        //设置生成的图片大小
        sizeList = parameters.getSupportedPictureSizes();
        if (sizeList.size()>0) {
            Camera.Size cameraSize=sizeList.get(0);
            for (Camera.Size size : sizeList) {
                //小于100W像素
                if (size.width*size.height<100*10000) {
                    cameraSize=size;
                    break;
                }
            }
            parameters.setPictureSize(cameraSize.width, cameraSize.height);
        }
        //设置图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setJpegThumbnailQuality(100);
        //自动聚焦模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        //设置闪光灯模式。此处主要是用于在相机摧毁后又重建，保持之前的状态
        setFlashMode(mFlashMode);
        //设置缩放级别
        setZoom(mZoom);
        //开启屏幕朝向监听
        startOrientationChangeListener();
    }

    /**
     * 手动聚焦
     *  @param point 触屏坐标
     */
    public void onFocus(Point point,Camera.AutoFocusCallback callback){
        Log.i(TAG, "YYYYYYY_onFocus.");
        Camera.Parameters parameters=mCamera.getParameters();
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas()<=0) {
            mCamera.autoFocus(callback);
            return;
        }
        List<Camera.Area> areas=new ArrayList<Camera.Area>();
        int left=point.x-300;
        int top=point.y-300;
        int right=point.x+300;
        int bottom=point.y+300;
        left=left<-1000?-1000:left;
        top=top<-1000?-1000:top;
        right=right>1000?1000:right;
        bottom=bottom>1000?1000:bottom;
        areas.add(new Camera.Area(new Rect(left,top,right,bottom), 100));
        parameters.setFocusAreas(areas);
        try {
            //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
            //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        mCamera.autoFocus(callback);
    }

    /**
     *  获取最大缩放级别，最大为40
     *  @return
     */
    public int getMaxZoom(){
        if(mCamera==null) return -1;
        Camera.Parameters parameters=mCamera.getParameters();
        if(!parameters.isZoomSupported()) return -1;
        return parameters.getMaxZoom()>40?40:parameters.getMaxZoom();
    }
    /**
     *  设置相机缩放级别
     *  @param zoom
     */
    public void setZoom(int zoom){
        if(mCamera==null) return;
        Camera.Parameters parameters;
        //注意此处为录像模式下的setZoom方式。在Camera.unlock之后，调用getParameters方法会引起android框架底层的异常
        //stackoverflow上看到的解释是由于多线程同时访问Camera导致的冲突，所以在此使用录像前保存的mParameters。
        if(mParameters!=null)
            parameters=mParameters;
        else {
            parameters=mCamera.getParameters();
        }

        if(!parameters.isZoomSupported()) return;
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
        mZoom=zoom;
    }
    public int getZoom(){
        return mZoom;
    }




    public FlashMode getFlashMode() {
        return mFlashMode;
    }

    public void setFlashMode(FlashMode flashMode) {
        if(mCamera==null) return;
        mFlashMode = flashMode;
        Camera.Parameters parameters=mCamera.getParameters();
        switch (flashMode) {
            case ON:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                break;
            case AUTO:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case TORCH:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            default:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
        }
        mCamera.setParameters(parameters);
    }

    /**
     *   启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
     */
    private  void startOrientationChangeListener() {
        OrientationEventListener mOrEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)){
                    rotation=0;
                }else if ((rotation > 45) && (rotation <= 135))  {
                    rotation=90;
                }
                else if ((rotation > 135) && (rotation <= 225)) {
                    rotation=180;
                }
                else if((rotation > 225) && (rotation <= 315)) {
                    rotation=270;
                }else {
                    rotation=0;
                }
                if(rotation==mOrientation)
                    return;
                mOrientation=rotation;
                updateCameraOrientation();
            }
        };
        mOrEventListener.enable();
    }

    /**
     *   根据当前朝向修改保存图片的旋转角度
     */
    private void updateCameraOrientation(){
        if(mCamera!=null){
            Camera.Parameters parameters = mCamera.getParameters();
            //rotation参数为 0、90、180、270。水平方向为0。
            int rotation=90+mOrientation==360?0:90+mOrientation;
            //前置摄像头需要对垂直方向做变换，否则照片是颠倒的
            if(mIsFrontCamera){
                if(rotation==90) rotation=270;
                else if (rotation==270) rotation=90;
            }
            parameters.setRotation(rotation);//生成的图片转90°
            //预览图片旋转90°
            mCamera.setDisplayOrientation(90);//预览转90°
            mCamera.setParameters(parameters);
        }
    }

    /**
     * @Description: 闪光灯类型枚举 默认为关闭
     */
    public enum FlashMode{
        /** ON:拍照时打开闪光灯   */
        ON,
        /** OFF：不打开闪光灯  */
        OFF,
        /** AUTO：系统决定是否打开闪光灯  */
        AUTO,
        /** TORCH：一直打开闪光灯  */
        TORCH
    }

}
