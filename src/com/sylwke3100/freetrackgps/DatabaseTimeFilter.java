package com.sylwke3100.freetrackgps;

public class DatabaseTimeFilter implements DatabaseFilter {
    private long startTimeFilter, endTimeFilter;

    public DatabaseTimeFilter(){
        startTimeFilter = 0;
        endTimeFilter = 0;
    }
    public void setViewFilter(long startTime){
        startTimeFilter = startTime;
    }

    public void setViewFilter(long startTime,
                              long endTime){
        startTimeFilter = startTime;
        endTimeFilter = endTime;
    }

    public void clearFilters(){
        startTimeFilter = 0;
        endTimeFilter = 0;
    }

    public String getGeneratedFilterString(){
        if (startTimeFilter > 0 && endTimeFilter == 0)
            return "timeStart>= " + Long.toString(startTimeFilter);
        else{
            if (startTimeFilter > 0 && endTimeFilter > 0)
                return "timeStart>= " + Long.toString(startTimeFilter) + "AND timeStart<= " + Long.toString(endTimeFilter);
            else
                return "";
        }
    }

    public boolean isActive(){
        if ((startTimeFilter > 0 && endTimeFilter == 0) || (startTimeFilter > 0 && endTimeFilter > 0))
            return true;
        else
            return false;
    }
}
