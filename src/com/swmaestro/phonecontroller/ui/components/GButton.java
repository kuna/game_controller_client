package com.swmaestro.phonecontroller.ui.components;

import com.swmaestro.phonecontroller.ui.model.ControllerEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class GButton extends Button {
	
	private Bitmap backgroundImg;
	private Bitmap pressedImg;
	private Integer soundId;
	private SoundPool sound;
	private ControllerEvent controllerEvent;
	private String Key;
	
	public GButton(Context context) {
		super(context);
		controllerEvent = new ControllerEvent();
	}

	public GButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		finalize();
		super.onDetachedFromWindow();
	}
	
	public void finalize() {
		if (sound != null)
			sound.release();
		if (backgroundImg != null) {
			backgroundImg.recycle();
			backgroundImg = null;
		}
		if (pressedImg != null) {
			pressedImg.recycle();
			pressedImg = null;
		}
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

	public Bitmap getPressedImg() {
		return pressedImg;
	}

	public void setPressedImg(Bitmap pressedImg) {
		this.pressedImg = pressedImg;
	}

	public void setSound(String soundFilePath) {
		sound = new  SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundId = sound.load(soundFilePath, 1);
		
	}
	
	public void setKey(String key) {
		this.Key = key;
	}
	
	public Integer getSoundId() {
		return soundId;
	}
	
	public void playSound() {
		if (soundId != null)
			sound.play(soundId, 2.0f, 2.0f, 0, 0, 1.0f);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (Key != null)
				controllerEvent.generateEvent(getId(), ControllerEvent.TOUCH_DOWN, Key);
			else
				controllerEvent.generateEvent(getId(), ControllerEvent.TOUCH_DOWN);
			if (getPressedImg() != null)
				setBackgroundDrawable(new BitmapDrawable(getPressedImg()));
			if (getSoundId() != null)
				playSound();
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (Key != null)
				controllerEvent.generateEvent(getId(), ControllerEvent.TOUCH_UP, Key);
			else
				controllerEvent.generateEvent(getId(), ControllerEvent.TOUCH_UP);
			if (getPressedImg() != null)
				setBackgroundDrawable(new BitmapDrawable(getBackgroundImg()));
			return true;
		}
		return false;
	}
}
