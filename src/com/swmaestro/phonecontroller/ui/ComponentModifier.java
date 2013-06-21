package com.swmaestro.phonecontroller.ui;

import com.swmaestro.phonecontroller.common.Util;
import com.swmaestro.phonecontroller.ui.components.GButton;
import com.swmaestro.phonecontroller.ui.components.GTextView;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class ComponentModifier {
	private static UIResourceManager uiResManager = new UIResourceManager();
	
	public static boolean ModifyComponent(Object component, String objtype, String objattr, String objval) {
		if (objtype.compareToIgnoreCase("button")==0) {
			return ModifyButton((GButton)component, objattr, objval);
		}
		
		if (objtype.compareToIgnoreCase("textview")==0) {
			return ModifyLabel((GTextView)component, objattr, objval);
		}
		
		return true;
	}
	
	public static boolean ModifyButton(GButton b, String objattr, String objval) {
		try {
			if (objattr.compareToIgnoreCase("x") ==0) {
				b.setX( (float)( Double.parseDouble(objval) * Util.SCREEN_RATIO ));
			}
			if (objattr.compareToIgnoreCase("y") ==0) {
				b.setY( (float)( Double.parseDouble(objval) * Util.SCREEN_RATIO ));
			}

			if (objattr.compareToIgnoreCase("width")==0) {
				b.getLayoutParams().width = (int) (Integer.parseInt(objval) * Util.SCREEN_RATIO);
			}
			if (objattr.compareToIgnoreCase("height")==0) {
				b.getLayoutParams().height = (int) (Integer.parseInt(objval) * Util.SCREEN_RATIO);
			}

			if (objattr.compareToIgnoreCase("visible")==0) {
				if (objval.compareToIgnoreCase("true") ==0) {
					b.setVisibility(Button.VISIBLE);
				} else {
					b.setVisibility(Button.INVISIBLE);
				}
			}
			if (objattr.compareToIgnoreCase("enable")==0) {
				if (objval.compareToIgnoreCase("true") ==0) {
					b.setEnabled(true);
				} else {
					b.setEnabled(false);
				}
			}

			if (objattr.compareToIgnoreCase("text")==0) {
				b.setText(objval);
			}
			if (objattr.compareToIgnoreCase("background")==0) {
				String resPath = uiResManager.getResourceAbsolutePath( objval );
				if (resPath == null)
					throw new Exception("background file not exist");
				b.setBackgroundImg(BitmapFactory.decodeFile(resPath));
			}
			if (objattr.compareToIgnoreCase("pressed")==0) {
				String resPath = uiResManager.getResourceAbsolutePath( objval );
				if (resPath == null)
					throw new Exception("background file not exist");
				b.setPressedImg(BitmapFactory.decodeFile(resPath));
			}
		} catch (Exception e) {
			Log.e("ComponentModifier", String.format("Something wrong value had been inputted ... %s - %s", objattr, objval));
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean ModifyLabel(GTextView b, String objattr, String objval) {
		try {
			if (objattr.compareToIgnoreCase("x") ==0) {
				b.setX( (float) Double.parseDouble(objval) );
			}
			if (objattr.compareToIgnoreCase("y") ==0) {
				b.setY( (float) Double.parseDouble(objval) );
			}

			if (objattr.compareToIgnoreCase("width")==0) {
				b.getLayoutParams().width = Integer.parseInt(objval);
			}
			if (objattr.compareToIgnoreCase("height")==0) {
				b.getLayoutParams().height = Integer.parseInt(objval);
			}

			if (objattr.compareToIgnoreCase("visible")==0) {
				if (objval.compareToIgnoreCase("true") ==0) {
					b.setVisibility(Button.VISIBLE);
				} else {
					b.setVisibility(Button.INVISIBLE);
				}
			}
			if (objattr.compareToIgnoreCase("enable")==0) {
				if (objval.compareToIgnoreCase("true") ==0) {
					b.setEnabled(true);
				} else {
					b.setEnabled(false);
				}
			}

			if (objattr.compareToIgnoreCase("text")==0) {
				b.setText(objval);
			}
			if (objattr.compareToIgnoreCase("background")==0) {
				String resPath = uiResManager.getResourceAbsolutePath( objval );
				if (resPath == null)
					throw new Exception("background file not exist");
				b.setBackgroundImg(BitmapFactory.decodeFile(resPath));
			}
		} catch (Exception e) {
			Log.e("ComponentModifier", String.format("Something wrong value had been inputted ... %s - %s", objattr, objval));
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
