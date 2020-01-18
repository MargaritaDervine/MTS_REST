package com.start.mts.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "env_deploy", schema = "mts")
public class EnvDeploy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int envDeployId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private Record record;
    @ManyToOne
    private Environment environment;
    private Date deployDate;

    public int getEnvDeployId() {
        return envDeployId;
    }

    public Record getRecord() {
        return record;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Date getDeployDate() {
        return deployDate;
    }

    public void setEnvDeployId(int envDeployId) {
        this.envDeployId = envDeployId;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setDeployDate(Date deployDate) {
        this.deployDate = deployDate;
    }

    public EnvDeploy() {
    }

    public EnvDeploy(int envDeployId, Record record, Environment env, Date date) {
        this.envDeployId = envDeployId;
        this.record = record;
        this.environment = env;
        this.deployDate = date;
    }

    public EnvDeploy(Environment env, Date date, Record recordId) {
        this.environment = env;
        this.deployDate = date;
        this.record = recordId;
    }

    @Override
    public String toString() {
        return environment + ": " + deployDate;
    }
}
