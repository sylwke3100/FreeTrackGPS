package com.example.freetrackgps;


public interface DatabaseFilter {
    public abstract void clearFilters();
    public abstract String getGeneratedFilterString();
}
