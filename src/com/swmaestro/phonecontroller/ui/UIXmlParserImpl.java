package com.swmaestro.phonecontroller.ui;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class UIXmlParserImpl implements UIXmlParser{
	private static final String ns = null;

	public List<HashMap<String, String>> parse(Reader reader) 
			throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(reader);
			parser.nextTag();
			return readUIComponents(parser);
		} finally {
		}
	}
	
	private List<HashMap<String, String>> readUIComponents(XmlPullParser parser)
            throws XmlPullParserException,IOException{
 
        List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
 
        parser.require(XmlPullParser.START_TAG, ns, "ControllerLayout");
        
        list.add(readControllerLayoutComponent(parser));
        
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
 
            String name = parser.getName();
            if(name.equals("Button")){
                list.add(readButtonComponent(parser));
            }
            else if(name.equals("TextView")){
                list.add(readTextViewComponent(parser));
            }
            else{
                skip(parser);
            }
        }
        return list;
    }
	
	private HashMap<String, String> readControllerLayoutComponent(XmlPullParser parser)
            throws XmlPullParserException, IOException{
 
        parser.require(XmlPullParser.START_TAG, ns, "ControllerLayout");
        
        HashMap<String, String> hm = new HashMap<String, String>();
        
        hm.put("component", "Layout");
        hm.put("orientation", parser.getAttributeValue(ns, "orientation"));
        hm.put("background", parser.getAttributeValue(ns, "background"));
        hm.put("sound", parser.getAttributeValue(ns, "sound"));
        
        return hm;
	}
	
	private HashMap<String, String> readButtonComponent(XmlPullParser parser)
            throws XmlPullParserException, IOException{
 
        parser.require(XmlPullParser.START_TAG, ns, "Button");
        
        HashMap<String, String> hm = new HashMap<String, String>();
        
        hm.put("component", "Button");
        hm.put("id", parser.getAttributeValue(ns, "id"));
        hm.put("text", parser.getAttributeValue(ns, "text"));
        hm.put("width", parser.getAttributeValue(ns, "width"));
        hm.put("height", parser.getAttributeValue(ns, "height"));
        hm.put("x", parser.getAttributeValue(ns, "x"));
        hm.put("y", parser.getAttributeValue(ns, "y"));
        hm.put("background", parser.getAttributeValue(ns, "background"));
        hm.put("pressed", parser.getAttributeValue(ns, "pressed"));
        hm.put("sound", parser.getAttributeValue(ns, "sound"));
        hm.put("key", parser.getAttributeValue(ns, "key"));
        
        parser.nextTag();
        
        return hm;
    }
	
	private HashMap<String, String> readTextViewComponent(XmlPullParser parser)
            throws XmlPullParserException, IOException{
 
        parser.require(XmlPullParser.START_TAG, ns, "TextView");
        
        HashMap<String, String> hm = new HashMap<String, String>();
        
        hm.put("component", "TextView");
        hm.put("id", parser.getAttributeValue(ns, "id"));
        hm.put("text", parser.getAttributeValue(ns, "text"));
        hm.put("width", parser.getAttributeValue(ns, "width"));
        hm.put("height", parser.getAttributeValue(ns, "height"));
        hm.put("x", parser.getAttributeValue(ns, "x"));
        hm.put("y", parser.getAttributeValue(ns, "y"));
        hm.put("background", parser.getAttributeValue(ns, "background"));
        hm.put("color", parser.getAttributeValue(ns, "color"));
        hm.put("size", parser.getAttributeValue(ns, "size"));
        hm.put("center", parser.getAttributeValue(ns, "center"));
        
        parser.nextTag();
        
        return hm;
    }
	 
    private void skip(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
           throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
