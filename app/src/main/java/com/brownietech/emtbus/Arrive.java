package com.brownietech.emtbus;

import java.text.SimpleDateFormat;
import java.util.Date;

class Arrive {
    String stopId;
    String lineId;
    String destination;
    String busId;
    int busTimeLeft;

    public Arrive(String stopId, String lineId, String destination, String busId, int busTimeLeft) {
        this.stopId = stopId;
        this.lineId = lineId;
        this.destination = destination;
        this.busId = busId;
        this.busTimeLeft = busTimeLeft;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public int getBusTimeLeft() {
        return busTimeLeft;
    }

    public void setBusTimeLeft(int busTimeLeft) {
        this.busTimeLeft = busTimeLeft;
    }

    public String getFormattedTime(){

        if(busTimeLeft == 999999){
            return "+20m";
        }else{ SimpleDateFormat df = new SimpleDateFormat("mm:ss");
          return  df.format(new Date(busTimeLeft*1000L));
        }

    }


    @Override
    public String toString() {
        return "Arrive{" +
                "stopId=" + stopId +
                ", lineId=" + lineId +
                ", destination='" + destination + '\'' +
                ", busId='" + busId + '\'' +
                ", busTimeLeft=" + busTimeLeft +
                '}';
    }
}
