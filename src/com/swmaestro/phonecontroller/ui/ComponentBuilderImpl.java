package com.swmaestro.phonecontroller.ui;

import java.util.HashMap;
import java.util.List;

import com.swmaestro.phonecontroller.ui.components.GButton;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.view.View;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class ComponentBuilderImpl implements ComponentBuilder{
	private Context context;
	private AbsoluteLayout layout;
	private UIResourceManager uiResManager;
	public View buildLayout(Context context, List<HashMap<String, String>> components, UIResourceManager uiResManager) throws Exception {
		this.context = context;
		this.uiResManager = uiResManager;
		this.layout = new AbsoluteLayout(context);
		
		for(HashMap<String, String> component : components) {
			if (component.get("component").equals("Layout")) {
				setLayoutAttributes(component);
			}
			if (component.get("component").equals("Button")) {
				GButton btn = new GButton(context);
				setButtonAttributes(btn, component);
			}
		}
		return layout;
	}
	
	private void setLayoutAttributes(HashMap<String, String> component) {
		((Activity)context).setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		((Activity)context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if (component.get("orientation").equals("LANDSCAPE")) {
			((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	private void setButtonAttributes(final GButton btn, HashMap<String, String> component) throws Exception {
		if (component.get("id") == null) {
			throw new Exception("Button ID must be described!");
		}
		btn.setId(Integer.parseInt(component.get("id")));
		
		if (component.get("height") == null) {
			throw new Exception("Height of button must be described!");
		}
		int height = Integer.parseInt(component.get("height"));
		
		if (component.get("width") == null) {
			throw new Exception("Width of button must be described!");
		}
		int width = Integer.parseInt(component.get("width"));
		
		if (component.get("x") == null) {
			throw new Exception("x must be described!");
		}
		int x = Integer.parseInt(component.get("x"));
		
		if (component.get("y") == null) {
			throw new Exception("y must be described!");
		}
		int y = Integer.parseInt(component.get("y"));
		
		
		if (component.get("text") != null)
			btn.setText((CharSequence)component.get("text"));
		
		if (component.get("background") != null) {
			String resPath = uiResManager.getResourceAbsolutePath(component.get("background"));
			if (resPath == null)
				throw new Exception(component.get("background")  + " does not exist");
			BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeFile(resPath));
			btn.setBackgroundImg(bd);
		}
		
		if (component.get("pressed") != null) {
			String resPath = uiResManager.getResourceAbsolutePath(component.get("pressed"));
			if (resPath == null)
				throw new Exception(component.get("background")  + " does not exist");
			BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeFile(resPath));
			btn.setPressedImg(bd);
		}
		
		if (component.get("sound") != null) {
			String resPath = uiResManager.getResourceAbsolutePath(component.get("sound"));
			if (resPath == null)
				throw new Exception(component.get("background")  + " does not exist");
			btn.setSound(resPath);
		}
		
		if (component.get("key") != null) {
			btn.setKey(component.get("key"));
		}
		
		layout.addView(btn, new AbsoluteLayout.LayoutParams(width, height, x, y));
	}

}
