package com.example.christopher.a3cpocompatibility;

import java.io.Serializable;

public class Customer implements Serializable {

    private String customerId;
    private String firstName;
    private String lastName;
    private String driversLicense;

    public Customer() {

    }

    public Customer(String customerId, String firstName, String lastName, String driversLicense) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.driversLicense = driversLicense;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(String driversLicense) {
        this.driversLicense = driversLicense;
    }

    @Override
    public String toString() {
        return firstName+" "+lastName;
    }
}
