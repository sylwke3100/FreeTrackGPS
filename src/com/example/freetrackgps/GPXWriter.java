package com.example.freetrackgps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.Pipe;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class GPXWriter {
	private String filepath;
	private StringBuilder content;
	private Boolean isOpen = false;
	private BufferedWriter bufferedWriter;
    private Calendar calendar;
	private String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
	private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final String GPXINIT = "<gpx"
             + " xmlns=\"http://www.topografix.com/GPX/1/1\""
             + " version=\"1.1\""
             + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
             + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";
	public GPXWriter(String fileName) {
        calendar = Calendar.getInstance();
        content = new StringBuilder();
		filepath = fileName;
		content.append(XMLHEADER + GPXINIT+ "\n");
		content.append("<metadata>\n<author>GPX Track</author>\n</metadata>");
		content.append("<trk>\n<name>GPX Workout</name>\n<time>" + POINT_DATE_FORMATTER.format(calendar.getTime()) +"</time>\n");
        content.append("<trkseg>\n");
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filepath));
			write(content.toString());
			isOpen = true;
		} catch (IOException e) {
			e.printStackTrace();
			isOpen = false;
		}
	}
	private void write(String data){
		try{
			bufferedWriter.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			isOpen = false;
		}
	}
	public void addPoint(double lat, double lon, double alt, long time){
		content.append("<trkpt lat=\""+lat+"\" lon=\""+lon+"\">\n");
		content.append("<ele>"+alt+"</ele>\n");
		content.append("<time>" + POINT_DATE_FORMATTER.format(new Date(time)) + "</time>\n");
		content.append("</trkpt>\n");
		write(content.toString());
	}
	public Boolean save(){
		content.append("</trkseg>\n</trk>\n</gpx>");
		write(content.toString());
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isOpen;
	}
}
