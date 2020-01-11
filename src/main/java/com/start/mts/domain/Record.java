package com.start.mts.domain;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "records", schema = "mts")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;
    private String userName;
    private String referenceEnv;
    private String ticketNumber;
    private String objectType;
    private String objectName;
    private String action;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private List<EnvDeploy> envs;

    public Record() {
    }

    public Record(String userName, String referenceEnv, String ticketNumber, String objectType, String objectName, String action, List<EnvDeploy> envs) {
        this.userName = userName;
        this.referenceEnv = referenceEnv;
        this.ticketNumber = ticketNumber;
        this.objectType = objectType;
        this.objectName = objectName;
        this.action = action;
        this.envs = envs;
    }

    public void setEnvs(List<EnvDeploy> envs) {
        this.envs = envs;
    }

    public List<EnvDeploy> getEvironments() {
        return envs;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getUserName() {
        return userName;
    }

    public String getReferenceEnv() {
        return referenceEnv;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getAction() {
        return action;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setReferenceEnv(String referenceEnv) {
        this.referenceEnv = referenceEnv;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setAction(String action) {
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
            List<EnvDeploy> envs = this.getEvironments();
            for (EnvDeploy env : envs) {
                if (envName.equals(env.getEnv())) {
                    return true;
                }
            }
        }
        return false;
    }
}
