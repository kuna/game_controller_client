package com.swmaestro.phonecontroller.ui;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;

public interface ComponentBuilder {
	public View buildLayout(Context context, List<HashMap<String, String>> components, UIResourceManager uiResManager) throws Exception;
}
