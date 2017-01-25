package com.jamieyao.cameray.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jamie.yao_cp on 2017/1/4.
 */

public class StorageUtil {

    private static final  String TAG = "StorageUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "CameraY";


    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }

    public static void saveBitmap(Bitmap b){

        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake +".jpg";
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public final static int ROOT=0;//根目录
    public final static int TYPE_IMAGE=1;//图片
    public final static int TYPE_THUMBNAIL=2;//缩略图
    public final static int TYPE_VIDEO=3;//视频

//android/data/data/package_name
    public static String getFolderPath(Context context, int type ,String rootPath) {
        StringBuilder pathBuilder=new StringBuilder();
        pathBuilder.append(context.getExternalFilesDir(null).getAbsolutePath());
        pathBuilder.append(File.separator);

        pathBuilder.append(rootPath);
        pathBuilder.append(File.separator);
        switch (type) {
            case TYPE_IMAGE:
                pathBuilder.append("Image");
                break;
            case TYPE_VIDEO:
                pathBuilder.append("Video");
                break;
            case TYPE_THUMBNAIL:
                pathBuilder.append("Thumbnail");
                break;
            default:
                break;
        }
        return pathBuilder.toString();
    }

    /**
     *
     * @param extension 后缀名 如".jpg"
     * @return
     */
    public static String createFileNmae(String extension){
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        //查看是否带"."
        if(!extension.startsWith("."))
            extension="."+extension;
        return formatDate+extension;
    }
}
