package com.jamieyao.cameray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jamieyao.cameray.Album.Thumbnail;
import com.jamieyao.cameray.Camera.TextureView.MyTextureView;
import com.jamieyao.cameray.Camera.ui.TextureContainer;
import com.jamieyao.cameray.Camera.TextureView.MyTextureView.FlashMode;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener,
        TextureContainer.PictureTakedListener {

    public final static String TAG = "CameraActivity";

    private TextureContainer tvContainer;
    private ImageButton mShutterButton;
    private ImageButton mRecordButton;
    private ImageView mSwitchModeButton;
    private ImageButton mToggleCamera;
    private ImageView mFlashView;
    private ImageView mSettingView;
    private Thumbnail mThumbView;
    private ImageView mVideoIconView;

    private boolean isRecording=false;
    private boolean mIsRecordMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        initUI();
    }

    public void initUI()
    {
        tvContainer = (TextureContainer)findViewById(R.id.texture_container);

        mShutterButton = (ImageButton)findViewById(R.id.btn_shutter_camera);
        mToggleCamera = (ImageButton)findViewById(R.id.btn_toggle_camera);
        mFlashView=(ImageView)findViewById(R.id.btn_flash_mode);
        mSettingView=(ImageView)findViewById(R.id.btn_setting);
        mThumbView = (Thumbnail)findViewById(R.id.btn_thumbnail);
        mRecordButton = (ImageButton)findViewById(R.id.btn_record_camera);
        mSwitchModeButton = (ImageView)findViewById(R.id.btn_switch_mode);
        mVideoIconView = (ImageView)findViewById(R.id.videoicon);

        mShutterButton.setOnClickListener(this);
        mToggleCamera.setOnClickListener(this);
        mFlashView.setOnClickListener(this);
        mSettingView.setOnClickListener(this);
        mThumbView.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
        mSwitchModeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.btn_shutter_camera:
                mShutterButton.setClickable(false);
                tvContainer.takePicture(this);
                break;
            case R.id.btn_record_camera:
                if(!isRecording){
                    isRecording = tvContainer.startRecord();
                    if (isRecording) {
                        mRecordButton.setBackgroundResource(R.drawable.btn_shutter_recording);
                    }
                }else {
                    tvContainer.stopRecord();
                    isRecording = false;
                    mRecordButton.setBackgroundResource(R.drawable.btn_shutter_record);
                }
                break;
            case R.id.btn_switch_mode:
                if(mIsRecordMode){
                    mSwitchModeButton.setImageResource(R.drawable.ic_switch_camera);
                    mShutterButton.setVisibility(View.VISIBLE);
                    mRecordButton.setVisibility(View.GONE);
                    //拍照模式下显示顶部菜单
                    //mHeaderBar.setVisibility(View.VISIBLE);
                    mIsRecordMode=false;
                    //tvContainer.switchMode(0);
                    tvContainer.stopRecord();
                    isRecording=false;
                    mRecordButton.setBackgroundResource(R.drawable.btn_shutter_record);
                }
                else {
                    mSwitchModeButton.setImageResource(R.drawable.ic_switch_video);
                    mShutterButton.setVisibility(View.GONE);
                    mRecordButton.setVisibility(View.VISIBLE);
                    //录像模式下隐藏顶部菜单
                    //mHeaderBar.setVisibility(View.GONE);
                    mIsRecordMode = true;
                    //tvContainer.switchMode(5);
                }
                break;
            case R.id.btn_thumbnail:
                startActivity(new Intent(this,AlbumActivity.class));
                break;
            case R.id.btn_flash_mode:
                if(tvContainer.getFlashMode()== MyTextureView.FlashMode.ON){
                    tvContainer.setFlashMode(FlashMode.OFF);
                    mFlashView.setImageResource(R.drawable.btn_flash_off);
                }else if (tvContainer.getFlashMode()==FlashMode.OFF) {
                    tvContainer.setFlashMode(FlashMode.AUTO);
                    mFlashView.setImageResource(R.drawable.btn_flash_auto);
                }
                else if (tvContainer.getFlashMode()==FlashMode.AUTO) {
                    tvContainer.setFlashMode(FlashMode.TORCH);
                    mFlashView.setImageResource(R.drawable.btn_flash_torch);
                }
                else if (tvContainer.getFlashMode()==FlashMode.TORCH) {
                    tvContainer.setFlashMode(FlashMode.ON);
                    mFlashView.setImageResource(R.drawable.btn_flash_on);
                }
                break;
            case R.id.btn_toggle_camera:
                tvContainer.switchCamera();
                break;
            case R.id.btn_setting:
                //tvContainer.setWaterMark();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTakePictureEnd() {
        mShutterButton.setClickable(true);
    }


}
