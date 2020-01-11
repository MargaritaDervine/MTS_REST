package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "environments", schema = "mts")
public class Environment {
    @Id
    String EnvName;
    boolean isReferenceEnvironment;

    public Environment(String envName, boolean isReferenceEnvironment) {
        EnvName = envName;
        this.isReferenceEnvironment = isReferenceEnvironment;
    }

    public Environment() {
    }


    public String getEnvName() {
        return EnvName;
    }

    public boolean isReferenceEnvironment() {
        return isReferenceEnvironment;
    }


    public void setEnvName(String envName) {
        EnvName = envName;
    }

    public void setReferenceEnvironment(boolean referenceEnvironment) {
        isReferenceEnvironment = referenceEnvironment;
    }
}
