package com.example.ehsueh.appygolucky;

import java.util.Collection;

/**
 * Created by Maxwell on 2016-10-11.
 */
public class Ride {
    private String rideName;
    private String startLocation;
    private String endLocation;
    private Collection<Driver> drivers;
    Boolean finalAcceptance;

    public Ride(String rideName, String startLocation, String endLocation) {
        this.rideName = rideName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;

    }

    public String getRideName() { return this.rideName; }
    public String getStartLocation() { return this.startLocation; }
    public String getEndLocation() { return this.endLocation; }
    public Collection<Driver> getDrivers() {return null;}
    public Boolean acceptedByRider() {return null;}
    public Boolean acceptedByDriver(Driver driver) {return Boolean.FALSE;}


    public void addDriver(Driver driver) {}
    public void riderAccepts() {}
}