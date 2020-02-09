package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "object_types", schema = "mts")
public class ObjectType {
    @Id
    String type;

    public ObjectType(String type) {
        this.type = type;
    }

    public ObjectType() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
