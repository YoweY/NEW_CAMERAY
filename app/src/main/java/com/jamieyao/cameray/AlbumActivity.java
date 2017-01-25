package com.jamieyao.cameray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jamie.yao_cp on 2017/1/17.
 */

public class AlbumActivity extends Activity {
    public final static String TAG="AlbumActivity";
    /**
     * 显示相册的View
     */

    private String mSaveRoot;

    private TextView mEnterView;
    private TextView mLeaveView;
    private TextView mSelectedCounterView;
    private TextView mSelectAllView;
    private Button mDeleteButton;
    private ImageView mBackView;
    private Button mCutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.album);

    }
}
