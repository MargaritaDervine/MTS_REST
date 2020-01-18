package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "names", schema = "mts")
public class Name {
    @Id
    String name;

    public Name(String name) {
        this.name = name;
    }
    public Name() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
