package com.sylwke3100.freetrackgps;


public interface DatabaseFilter {
    void clearFilters();

    boolean isActive();

    String getGeneratedFilterString();
}
