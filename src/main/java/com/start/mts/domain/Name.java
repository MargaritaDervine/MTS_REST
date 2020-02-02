package com.start.mts.domain;

import javax.persistence.*;

@Entity
@Table(name = "names", schema = "mts")
public class Name {
    @Id
    String userName;

    public Name(String name) {
        this.userName = name;
    }
    public Name() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
