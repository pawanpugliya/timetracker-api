package com.timetracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetracker.model.Record;
import com.timetracker.service.TimeTrackerService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeTrackerServiceImpl implements TimeTrackerService {

    private List<Record> records;
    private RestTemplate restTemplate;
    private final String DOCKER_SERVICE_URL = "http://192.168.99.100:8080/";
    private ObjectMapper mapper;

    public TimeTrackerServiceImpl() {
        records = new ArrayList<>();
        restTemplate = new RestTemplate();
        mapper = new ObjectMapper();
    }

    @Override
    public void saveTrackingDetails(Record record) {
        // Replace the - in date with . AND replace the T in time with space separator.
        record.setStart(record.getStart().replaceAll("-", "."));
        record.setStart(record.getStart().replaceAll("T", " "));
        record.setEnd(record.getEnd().replaceAll("-", "."));
        record.setEnd(record.getEnd().replaceAll("T", " "));

        try {
            this.postTrackingDetailsToExternalService(DOCKER_SERVICE_URL + "/records", record);
        } catch(Exception e) {
            records.add(record);
        }
    }

    public List<Record> getTrackingDetails(String emailAddress) {
        System.out.println("Email: "+ emailAddress);
        if(emailAddress.length() == 0) {
            System.out.println("No email address provided. Fetching top 10 results");
            try {
                return this.getTrackingDetailsFromExternalService(DOCKER_SERVICE_URL + "/records?offset=0&length=10");
            }
            catch(Exception e) {
                return records;
            }
        }
        try {
            return this.getTrackingDetailsFromExternalService(DOCKER_SERVICE_URL + "/records?email=" + emailAddress);
        } catch(Exception e) {
            return records.stream().filter(item -> item.getEmail().equals(emailAddress)).collect(Collectors.toList());
        }
    }

    private List<Record> getTrackingDetailsFromExternalService(String url) {
        List<Record> records = new ArrayList<>();
        ResponseEntity<Object[]> recordsEntity = restTemplate.getForEntity(url, Object[].class);

        if(recordsEntity.getStatusCode() == HttpStatus.OK) {
            records = Arrays.stream(recordsEntity.getBody())
                    .map(object -> mapper.convertValue(object, Record.class))
                    .collect(Collectors.toList());
        }

        return records;
    }

    private void postTrackingDetailsToExternalService(String url, Record record) {
        HttpEntity<Record> recordEntity = new HttpEntity<>(record);
        restTemplate.postForEntity(url, recordEntity, Record.class);
    }
}
