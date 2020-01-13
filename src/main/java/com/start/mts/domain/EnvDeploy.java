package com.start.mts.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "env_deploy", schema = "mts")
public class EnvDeploy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private Record record;
    private String env;
    private Date date;

    public int getId() {
        return id;
    }

    public Record getRecord() {
        return record;
    }

    public String getEnv() {
        return env;
    }

    public Date getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EnvDeploy() {
    }

    public EnvDeploy(int id, Record record, String env, Date date) {
        this.id = id;
        this.record = record;
        this.env = env;
        this.date = date;
    }

    public EnvDeploy(String env, Date date, Record recordId) {
        this.env = env;
        this.date = date;
        this.record = recordId;
    }

    @Override
    public String toString() {
        return env + ": " + date;
    }
}
