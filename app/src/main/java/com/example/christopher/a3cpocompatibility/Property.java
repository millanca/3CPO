package com.example.christopher.a3cpocompatibility;

import java.io.Serializable;
import java.util.ArrayList;

public class Property implements Serializable {

    private String propertyId;
    private String name;
    private String address;
    private int numParkingSpaces;
    private boolean customDecal;
    private String customerCode;
    private double lat;
    private double lng;
    private ArrayList<Vehicle> vehicleArrayList = new ArrayList<>();

    public Property() {

    }
    public Property(String propertyId, String name, String address, int numParkingSpaces, boolean customDecal, String customerCode) {
        this.propertyId = propertyId;
        this.name = name;
        this.address = address;
        this.numParkingSpaces = numParkingSpaces;
        this.customDecal = customDecal;
        this.customerCode = customerCode;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumParkingSpaces() {
        return numParkingSpaces;
    }

    public void setNumParkingSpaces(int numParkingSpaces) {
        this.numParkingSpaces = numParkingSpaces;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        return vehicleArrayList;
    }

    public void setVehicleArrayList(ArrayList<Vehicle> vehicleArrayList) {
        this.vehicleArrayList = vehicleArrayList;
    }

    public void addVehicle(Vehicle vehicle){
        vehicleArrayList.add(vehicle);
    }

    public boolean isCustomDecal() {
        return customDecal;
    }

    public void setCustomDecal(boolean customDecal) {
        this.customDecal = customDecal;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void setLatLng(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
}
