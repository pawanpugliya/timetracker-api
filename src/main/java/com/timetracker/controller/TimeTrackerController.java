package com.timetracker.controller;

import com.timetracker.model.Record;
import com.timetracker.service.TimeTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class TimeTrackerController {

    @Autowired
    private TimeTrackerService timeTrackerService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveTrackingDetails(@RequestBody Record record) {
        timeTrackerService.saveTrackingDetails(record);
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public List<Record> getTrackingDetails(@RequestParam(defaultValue = "") String emailAddress) {
        return timeTrackerService.getTrackingDetails(emailAddress);
    }
}
