package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "environments", schema = "mts")
public class Environment {
    @Id
    String environmentName;
    boolean isReferenceEnvironment;

    public Environment(String envName, boolean isReferenceEnvironment) {
        this.environmentName = envName;
        this.isReferenceEnvironment = isReferenceEnvironment;
    }

    public Environment() {
    }


    public String getEnvironmentName() {
        return environmentName;
    }

    public boolean isReferenceEnvironment() {
        return isReferenceEnvironment;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public void setReferenceEnvironment(boolean referenceEnvironment) {
        isReferenceEnvironment = referenceEnvironment;
    }

    @Override
    public String toString() {
        return environmentName;
    }
}

