package com.sylwke3100.freetrackgps;


public interface DatabaseFilter {
    public abstract void clearFilters();
    public boolean isActive();
    public abstract String getGeneratedFilterString();
}
