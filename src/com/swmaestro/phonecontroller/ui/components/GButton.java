package com.swmaestro.phonecontroller.ui.components;

import com.swmaestro.phonecontroller.ui.model.ControllerEvent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class GButton extends Button {
	
	private Drawable backgroundImg;
	private Drawable pressedImg;
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

	public Drawable getBackgroundImg() {
		return backgroundImg;
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundImg(Drawable backgroundImg) {
		this.backgroundImg = backgroundImg;
		setBackgroundDrawable(backgroundImg);
	}

	public Drawable getPressedImg() {
		return pressedImg;
	}

	public void setPressedImg(Drawable pressedImg) {
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
				setBackgroundDrawable(getPressedImg());
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
				setBackgroundDrawable(getBackgroundImg());
			return true;
		}
		return false;
	}
}
