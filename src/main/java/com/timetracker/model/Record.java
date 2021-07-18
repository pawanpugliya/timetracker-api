package com.timetracker.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Record {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String email;

    @Column
    private String start;

    @Column
    private String end;
}
