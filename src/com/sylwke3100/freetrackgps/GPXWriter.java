package com.sylwke3100.freetrackgps;

import android.os.Environment;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


public class GPXWriter {
    private static final SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private SimpleDateFormat fileGpxFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
    private Boolean isOpen = false;
    private FileOutputStream gpxOutputStream;
    private XmlSerializer gpxSerializer;
    private StringBuffer fileNameBuffer;

    public GPXWriter(long startTime, String nameWorkout) {
        String preparedFilename = prepareFilename(startTime);
        try {
            gpxOutputStream = new FileOutputStream(preparedFilename);
            isOpen = true;
        } catch (FileNotFoundException fnfe) {
            isOpen = false;
        }
        gpxSerializer = Xml.newSerializer();
        try {
            if (isOpen) {
                gpxSerializer.setOutput(gpxOutputStream, "UTF-8");
                gpxSerializer
                    .setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                gpxSerializer.startDocument("UTF-8", true);
            }
        } catch (IOException e) {
            isOpen = false;
        }
        createHeader();
        createMetadata(startTime, nameWorkout);
    }

    private String prepareFilename(long startTime){
        fileNameBuffer = new StringBuffer();
        File dir = new File(
            Environment.getExternalStorageDirectory() + DefaultValues.defaultFolderWithWorkout);
        if (!(dir.exists() && dir.isDirectory()))
            dir.mkdir();
        fileNameBuffer.append(
            Environment.getExternalStorageDirectory() + DefaultValues.defaultFolderWithWorkout);
        fileNameBuffer.append(fileGpxFormat.format(new Date(startTime)) + "."
            + DefaultValues.defaultFileFormat);
        return fileNameBuffer.toString();
    }

    public String getFilename(){
        return fileNameBuffer.toString();
    }


    private void createHeader() {
        if (isOpen)
            try {
                gpxSerializer.startTag("", "gpx");
                gpxSerializer.attribute("", "xmlns", "http://www.topografix.com/GPX/1/1");
                gpxSerializer.attribute("", "version", "1.1");
                gpxSerializer
                    .attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                gpxSerializer.attribute("", "xsi:schemaLocation",
                    "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            } catch (IOException e) {
                isOpen = false;
            }
    }

    private String generateName(String name) {
        if (name == null)
            return "GPS Workout";
        else if (name.isEmpty())
            return "GPS workout";
        else
            return name;
    }

    private void createMetadata(long startTime, String name) {
        if (isOpen)
            try {
                gpxSerializer.startTag("", "metadata");
                gpxSerializer.startTag("", "author");
                gpxSerializer.text("FreeTrackGPS");
                gpxSerializer.endTag("", "author");
                gpxSerializer.endTag("", "metadata");
                gpxSerializer.startTag("", "trk");
                gpxSerializer.startTag("", "name");
                gpxSerializer.text(generateName(name));
                gpxSerializer.endTag("", "name");
                gpxSerializer.startTag("", "time");
                gpxSerializer.text(dateFormat.format(startTime));
                gpxSerializer.endTag("", "time");
                gpxSerializer.startTag("", "trkseg");
            } catch (IOException e) {
                isOpen = false;
            }
    }

    public void addPoint(RouteElement point) {
        if (isOpen)
            try {
                gpxSerializer.startTag("", "trkpt");
                gpxSerializer.attribute("", "lat", point.getLatitude());
                gpxSerializer.attribute("", "lon", point.getLongitude());
                gpxSerializer.startTag("", "ele");
                gpxSerializer.text(point.getAltitude());
                gpxSerializer.endTag("", "ele");
                gpxSerializer.startTag("", "time");
                gpxSerializer.text(point.getPointTime());
                gpxSerializer.endTag("", "time");
                gpxSerializer.endTag("", "trkpt");
            } catch (IOException e) {
                isOpen = false;
            }
    }

    public Boolean save() {
        if (isOpen)
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
