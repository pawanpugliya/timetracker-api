package com.timetracker.repository;

import org.springframework.data.repository.CrudRepository;
import com.timetracker.model.Record;

public interface TimeTrackerRepository extends CrudRepository<Record, Integer> {
}
