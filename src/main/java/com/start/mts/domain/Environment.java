package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "environments", schema = "mts")
public class Environment {
    @Id
    String envName;
    boolean isReferenceEnvironment;

    public Environment(String envName, boolean isReferenceEnvironment) {
        this.envName = envName;
        this.isReferenceEnvironment = isReferenceEnvironment;
    }

    public Environment() {
    }


    public String getEnvName() {
        return envName;
    }

    public boolean isReferenceEnvironment() {
        return isReferenceEnvironment;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public void setReferenceEnvironment(boolean referenceEnvironment) {
        isReferenceEnvironment = referenceEnvironment;
    }

    @Override
    public String toString() {
        return envName;
    }
}

