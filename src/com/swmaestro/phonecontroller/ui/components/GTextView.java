package com.swmaestro.phonecontroller.ui.components;

import com.swmaestro.phonecontroller.ui.model.ControllerEvent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.widget.TextView;

public class GTextView extends TextView {
	private Bitmap backgroundImg;
	private ControllerEvent controllerEvent;
	public GTextView(Context context) {
		super(context);
		controllerEvent = new ControllerEvent();
	}
	
	public Bitmap getBackgroundImg() {
		return backgroundImg;
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundImg(Bitmap backgroundImg) {
		if (this.backgroundImg != null) {
			this.backgroundImg.recycle();
			this.backgroundImg = null;
		}
		
		this.backgroundImg = backgroundImg;
		setBackgroundDrawable(new BitmapDrawable(this.backgroundImg));
	}
	
	@Override
	protected void onDetachedFromWindow() {
		finalize();
		super.onDetachedFromWindow();
	}
	
	public void finalize() {
		if (backgroundImg != null) {
			backgroundImg.recycle();
			backgroundImg = null;
		}
	}
}
