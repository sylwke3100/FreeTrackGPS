package com.sylwke3100.freetrackgps;


public interface DatabaseFilter {
    public abstract void clearFilters();
    public abstract String getGeneratedFilterString();
}
