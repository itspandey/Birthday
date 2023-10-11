package com.example.birthday.Module;

public class Friend {
    String name, dob;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Friend(String name, String dob) {
        this.name = name;
        this.dob = dob;
    }
}