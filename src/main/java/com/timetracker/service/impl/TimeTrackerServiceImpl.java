package com.timetracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetracker.model.Record;
import com.timetracker.repository.TimeTrackerRepository;
import com.timetracker.service.TimeTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TimeTrackerRepository repository;

    private final String DOCKER_SERVICE_URL = "http://192.168.99.100:8080/";
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    public TimeTrackerServiceImpl() {
        restTemplate = new RestTemplate();
        mapper = new ObjectMapper();
    }

    @Override
    public void saveTrackingDetails(Record record) {
        System.out.println(record.toString());
        // Replace the - in date with . AND replace the T in time with space separator.
        record.setStart(record.getStart().replaceAll("-", "."));
        record.setStart(record.getStart().replaceAll("T", " "));
        record.setEnd(record.getEnd().replaceAll("-", "."));
        record.setEnd(record.getEnd().replaceAll("T", " "));

        // NOTE: Saving a copy to local h2database as docker service is not reachable
        repository.save(record);
        try {
            this.postTrackingDetailsToExternalService(DOCKER_SERVICE_URL + "/records", record);
        } catch(Exception e) {
            System.out.println(e);
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
                // NOTE: Get the top 10 tracking details from local H2 database for given email if the docker service is not available
                return this.getTrackingDetailsFromLocalH2Database();
            }
        }
        try {
            return this.getTrackingDetailsFromExternalService(DOCKER_SERVICE_URL + "/records?email=" + emailAddress);
        } catch(Exception e) {
            // NOTE: Get top 10 tracking details from local H2 database for given email if the docker service is not available
            return this.getTrackingDetailsForEmailFromLocalH2Database(emailAddress);
        }
    }

    private List<Record> getTrackingDetailsFromExternalService(String url) {
        ResponseEntity<Object[]> recordsEntity = restTemplate.getForEntity(url, Object[].class);

        if(recordsEntity.getStatusCode() == HttpStatus.OK) {
            return Arrays.stream(recordsEntity.getBody())
                    .map(object -> mapper.convertValue(object, Record.class))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void postTrackingDetailsToExternalService(String url, Record record) {
        HttpEntity<Record> recordEntity = new HttpEntity<>(record);
        restTemplate.postForEntity(url, recordEntity, Record.class);
    }

    private List<Record> getTrackingDetailsForEmailFromLocalH2Database(String emailAddress) {
        ArrayList<Record> recordsForEmail = new ArrayList<>();
        repository.findAll().forEach(recordItem -> {
            if(recordItem.getEmail().equals(emailAddress)) {
                recordsForEmail.add(recordItem);
            }
        });
        return recordsForEmail.stream().limit(10).collect(Collectors.toList());
    }

    private List<Record> getTrackingDetailsFromLocalH2Database() {
        ArrayList<Record> allRecords = new ArrayList<>();
        repository.findAll().forEach(record -> allRecords.add(record));

        return allRecords.stream().limit(10).collect(Collectors.toList());
    }
}
