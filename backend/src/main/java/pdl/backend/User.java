package com.zetcode.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String user_name;
    private String password;
    private String email;

    public User() {
    }

    public User(String user_name, String password, String email) {
        this.user_name = user_name;
        this.password = password;
        this.email = email;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}