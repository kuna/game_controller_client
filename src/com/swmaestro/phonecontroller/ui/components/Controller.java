package com.swmaestro.phonecontroller.ui.components;

///import com.swmaestro.phonecontroller.R;
import com.swmaestro.phonecontroller.ui.ControllerEventListener;
import com.swmaestro.phonecontroller.ui.UIManager;
import com.swmaestro.phonecontroller.ui.model.ControllerEvent;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class Controller extends Activity implements ControllerEventListener{
	public static Activity ConActivity = null;
	
	private UIManager uiManager = UIManager.getInstance();	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiManager.setControllerEventListener(this);
		View view = uiManager.getLayout(this);
		if (view == null) {
			Toast.makeText(this, "Invalid Layout", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
			
		setContentView(view);
		
		ConActivity = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean OnControllerEvent(ControllerEvent event) {
		return true;
	}
	
}
