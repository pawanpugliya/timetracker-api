package com.timetracker.service;

import com.timetracker.model.Record;

import java.util.ArrayList;
import java.util.List;

public interface TimeTrackerService {
    void saveTrackingDetails(Record record);

    List<Record> getTrackingDetails(String emailAddress);
}
