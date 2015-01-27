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
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final String gpxInit = "<gpx"
             + " xmlns=\"http://www.topografix.com/GPX/1/1\""
             + " version=\"1.1\""
             + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
             + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";
	public GPXWriter(String fileName, long startTime) {
        calendar = Calendar.getInstance();
        content = new StringBuilder();
		filepath = fileName;
		content.append(XMLHEADER + gpxInit+ "\n");
		content.append("<metadata>\n<author>GPX Track</author>\n</metadata>");
		content.append("<trk>\n<name>GPX Workout</name>\n<time>" + dateFormat.format(startTime) +"</time>\n");
        content.append("<trkseg>\n");
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filepath));
			write(content.toString());
            content.delete(0, content.length());
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
	public void addPoint(RouteElement point){
		content.append("<trkpt lat=\""+point.lat+"\" lon=\""+point.lon+"\">\n");
		content.append("<ele>"+point.alt+"</ele>\n");
		content.append("<time>" + dateFormat.format(new Date(point.time)) + "</time>\n");
		content.append("</trkpt>\n");
		write(content.toString());
        content.delete(0, content.length());
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
