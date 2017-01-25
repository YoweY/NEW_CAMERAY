package com.jamieyao.cameray.Camera.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.jamieyao.cameray.R;

/** 
 * @ClassName: TempImageView 
 * @Description:   ��ʱImageView����ִ����һ���������Զ�����
 * @author LinJ
 * @date 2014-12-31 ����9:45:34 
 *  
 */
public class AnimTmpView extends ImageView implements AnimationListener{

	public final static String TAG="AnimTmpView";

	/**
	 * �����ڵĶ���ID
	 */
	public static final int NO_ID=-1;
	/**
	 * ���õĶ���Ч��ID
	 */
	private int mAnimationID = NO_ID;

	/** ���ն��������ӿ�  */ 
	private AnimtionEndListener mListener;

	public static interface AnimtionEndListener{
		public void onAnimtionEnd(Bitmap bm,boolean isVideo);
	}

	private boolean mIsVideo;
	public AnimTmpView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}

	public AnimTmpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TempImageView);
		mAnimationID = a.getResourceId(R.styleable.TempImageView_animat_id, NO_ID);
		a.recycle();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		setVisibility(View.GONE);
		Drawable drawable =  getDrawable();
		Bitmap bm = null;
		if(drawable!=null&&drawable instanceof BitmapDrawable)
			bm = ((BitmapDrawable)drawable).getBitmap();
		//��������ӿڲ�Ϊ�գ���ִ�����ս�������
		if (mListener!=null) mListener.onAnimtionEnd(bm,mIsVideo);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	/** 
	 * @Description: ��ʼ����
	 * @param @param resourceID ������Դ��ID  
	 * @return void    
	 * @throws 
	 */
	public void startAnimation(int resourceID){
		mAnimationID=resourceID;
		startAnimation(null);
	}

	public void startAnimation(Animation animation){
		if(animation!=null){
			animation.setAnimationListener(this);
			super.startAnimation(animation);
			return;
		}
		if(mAnimationID!=NO_ID){
			animation=AnimationUtils.loadAnimation(getContext(), mAnimationID);
			animation.setAnimationListener(this);
			super.startAnimation(animation);
		}
	}
	public void isVideo(boolean isVideo){
		mIsVideo=isVideo;
	}


	public void setListener(AnimtionEndListener mListener) {
		this.mListener = mListener;
	}
}