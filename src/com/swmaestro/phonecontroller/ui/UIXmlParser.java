package com.swmaestro.phonecontroller.ui;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

public interface UIXmlParser {
	public List<HashMap<String, String>> parse(Reader reader) throws XmlPullParserException, IOException;
}
