package com.example.freetrackgps;

import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;


public class GPXWriter {
	private Boolean isOpen = false;
    private FileOutputStream gpxOutputStream;
    private XmlSerializer gpxSerializer;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public GPXWriter(String fileName,
                     long startTime) {
        try {
            gpxOutputStream = new FileOutputStream(fileName);
            isOpen = true;
        }catch (FileNotFoundException fnfe){
            isOpen = false;
        }
        gpxSerializer = Xml.newSerializer();
        try{
            if (isOpen) {
                gpxSerializer.setOutput(gpxOutputStream, "UTF-8");
                gpxSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                gpxSerializer.startDocument("UTF-8", true);
            }
        }catch (IOException e){
            isOpen = false;
        }
        createHeader();
        createMetadata(startTime);
    }

    private void createHeader(){
        if(isOpen)
            try {
                gpxSerializer.startTag("", "gpx");
                gpxSerializer.attribute("", "xmlns", "http://www.topografix.com/GPX/1/1");
                gpxSerializer.attribute("", "version", "1.1");
                gpxSerializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                gpxSerializer.attribute("", "xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            } catch (IOException e) {
                isOpen = false;
            }
    }

    private void createMetadata(long startTime) {
        if (isOpen)
            try {
                gpxSerializer.startTag("", "metadata");
                gpxSerializer.startTag("", "author");
                gpxSerializer.text("FreeTrackGPS");
                gpxSerializer.endTag("", "author");
                gpxSerializer.endTag("", "metadata");
                gpxSerializer.startTag("", "trk");
                gpxSerializer.startTag("", "name");
                gpxSerializer.text("GPS workout");
                gpxSerializer.endTag("", "name");
                gpxSerializer.startTag("", "time");
                gpxSerializer.text(dateFormat.format(startTime));
                gpxSerializer.endTag("", "time");
                gpxSerializer.startTag("", "trkseg");
            } catch (IOException e) {
                isOpen = false;
            }
    }

	public void addPoint(RouteElement point){
		if(isOpen)
            try{
                gpxSerializer.startTag("", "trkpt");
                gpxSerializer.attribute("", "lat", Double.toString(point.latitude));
                gpxSerializer.attribute("", "lon", Double.toString(point.longitude));
                gpxSerializer.startTag("", "ele");
                gpxSerializer.text(Double.toString(point.altitude));
                gpxSerializer.endTag("", "ele");
                gpxSerializer.startTag("", "time");
                gpxSerializer.text(dateFormat.format(point.time));
                gpxSerializer.endTag("", "time");
                gpxSerializer.endTag("", "trkpt");
            }catch (IOException e){
                isOpen = false;
            }
	}
	public Boolean save(){
        if(isOpen)
            try {
                gpxSerializer.endTag("", "trkseg");
                gpxSerializer.endTag("", "trk");
                gpxSerializer.endTag("", "gpx");
                gpxSerializer.endDocument();
                gpxSerializer.flush();
                gpxOutputStream.close();

            } catch (IOException e) {
                isOpen = false;
            }
		return isOpen;
    }
}
