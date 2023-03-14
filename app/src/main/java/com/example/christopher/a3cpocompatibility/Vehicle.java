package com.example.christopher.a3cpocompatibility;

import java.io.Serializable;
import java.util.Date;

public class Vehicle implements Serializable {

    private String vehicleId;
    private Customer owner;
    private String address;
    private String brand;
    private String model;
    private String color;
    private String plate;
    private String registration;
    private String insurance;
    private Date permitStart;

    public Vehicle() {

    }

    public Vehicle(String vehicleId, Customer owner, String address, String brand, String model, String color, String plate, String registration, String insurance, Date permitStart) {
        this.vehicleId = vehicleId;
        this.owner = owner;
        this.address = address;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.plate = plate;
        this.registration = registration;
        this.insurance = insurance;
        this.permitStart = permitStart;
        //this.permitEnd = permitEnd;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public Date getPermitStart() {
        return permitStart;
    }

    public void setPermitStart(Date permitStart) {
        this.permitStart = permitStart;
    }

    @Override
    public String toString(){
        return owner.toString()+" "+address+" "+brand+" "+model+" "+color+" "+plate+" "+registration+" "+insurance+" "+permitStart;
    }
}
