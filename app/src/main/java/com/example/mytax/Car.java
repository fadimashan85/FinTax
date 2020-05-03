
//Model class for Car activity

package com.example.mytax;

public class Car {

    private String distance;
    private String startDate;
    private String gpsDistance;
    private String origin;
    private String destination;
    private String purpose;
    private String amount;



    public Car(String mDistance, String mStartDate,String mOrigin,
               String mDestination, String mPurpose, String mAmount){


        this.distance = mDistance;
        this.startDate =mStartDate;
        this.origin=mOrigin;
        this.destination = mDestination;
        this.purpose =mPurpose;
        this.amount = mAmount;
    }

    public String getDistance() {
        return distance;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getGpsDistance() {
        return gpsDistance;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getAmount() {
        return amount;
    }
}
