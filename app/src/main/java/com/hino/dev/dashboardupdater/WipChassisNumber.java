package com.hino.dev.dashboardupdater;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class WipChassisNumber {

    public String chassisNumber = "";
    public String timeIn = "";
    public Integer workTime = 0;
    public Boolean isPending = false;
    public Boolean isMc = false;
    public String moNumber = "";
    public String moDate = "";
    public String dealer = "";
    public String customer = "";
    public String chassisModel = "";
    public Integer moQuantity = 0;
    public String[] fileAttachments;

    public WipChassisNumber(String chassisNumber, String timeIn, Integer workTime, Boolean isPending, Boolean isMc, String moNumber, String moDate, String dealer, String customer, String chassisModel, Integer moQuantity, String[] fileAttachments) {
        this.chassisNumber = chassisNumber;
        this.timeIn = timeIn;
        this.workTime = workTime;
        this.isPending = isPending;
        this.isMc = isMc;
        this.moNumber = moNumber;
        this.moDate = moDate;
        this.dealer = dealer;
        this.customer = customer;
        this.chassisModel = chassisModel;
        this.moQuantity = moQuantity;
        this.fileAttachments = fileAttachments;
    }

    private Date dateValueOfTimeIn(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        if(this.timeIn != null){
            try {
                date =format.parse(this.timeIn);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public long checkInTimeInMinutes(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date timeIn = this.dateValueOfTimeIn();
        if(timeIn != null){
            Date now = new Date();
            
            long diff = now.getTime() - timeIn.getTime();
            long seconds = diff/1000;
            return seconds/ 60;
        }
        return 0;
    }
}
