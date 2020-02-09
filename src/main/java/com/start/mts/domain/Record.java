package com.start.mts.domain;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "records", schema = "mts")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;
    @ManyToOne
    private Name userName;
    @ManyToOne
    private Environment referenceEnvironment;
    private String ticketNumber;
    @ManyToOne
    private ObjectType objectType;
    private String objectName;
    private Enum<Actions> action;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private List<EnvDeploy> environments;

    public Record() {
    }

    public Record(Name userName, Environment referenceEnv, String ticketNumber, ObjectType objectType, String objectName, Enum<Actions> action, List<EnvDeploy> environmentsDeployed) {
        this.userName = userName;
        this.referenceEnvironment = referenceEnv;
        this.ticketNumber = ticketNumber;
        this.objectType = objectType;
        this.objectName = objectName;
        this.action = action;
        this.environments = environmentsDeployed;
    }

    public void setEnvironments(List<EnvDeploy> environments) {
        this.environments = environments;
    }

    public List<EnvDeploy> getEnvironments() {
        return environments;
    }

    public int getRecordId() {
        return recordId;
    }

    public Name getUserName() {
        return userName;
    }

    public Environment getReferenceEnvironment() {
        return referenceEnvironment;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getObjectName() {
        return objectName;
    }

    public Enum<Actions> getAction() {
        return action;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setUserName(Name userName) {
        this.userName = userName;
    }

    public void setReferenceEnvironment(Environment referenceEnvironment) {
        this.referenceEnvironment = referenceEnvironment;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public void setAction(Enum<Actions> action) {
        this.action = action;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public boolean isSystestDeployed() {
        return isEnvDeployed("SYSTEST");
    }

    public boolean isAcceptanceDeployed() {
        return  isEnvDeployed("ACCTEST");
    }

    public boolean isProdDeployed() {
        return isEnvDeployed("PROD");
    }

    public boolean isEnvDeployed(String envName) {
        if (envName != null) {
            List<EnvDeploy> envs = this.getEnvironments();
            for (EnvDeploy env : envs) {
                if (envName.equals(env.getEnvironment().getEnvironmentName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getHighestEnvironmentDeployed() {
        if (this.isProdDeployed()) {
            return "PROD";
        } else if (this.isAcceptanceDeployed()) {
            return "ACCTEST";
        } else if (this.isSystestDeployed()) {
            return "SYSTEST";
        }
        return StringUtils.EMPTY;

    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", userName=" + userName +
                ", referenceEnvironment=" + referenceEnvironment +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", objectType=" + objectType +
                ", objectName='" + objectName + '\'' +
                ", action=" + action +
                ", environments=" + environments +
                '}';
    }
}
