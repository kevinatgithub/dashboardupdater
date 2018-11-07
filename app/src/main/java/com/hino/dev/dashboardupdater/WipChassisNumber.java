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
    public Integer remainingTime = 0;
    public Boolean isPending = false;
    public Boolean isMc = false;
    public String moNumber = "";
    public String moDate = "";
    public String dealer = "";
    public String customer = "";
    public String chassisModel = "";
    public Integer moQuantity = 0;
    public Boolean finishedNormalEntry = false;
    public Attachment[] fileAttachments;
    public MC mcs;

    public WipChassisNumber(String chassisNumber, String timeIn, Integer workTime, Integer remainingTime, Boolean isPending, Boolean isMc, String moNumber, String moDate, String dealer, String customer, String chassisModel, Integer moQuantity, Boolean finishedNormalEntry, Attachment[] fileAttachments, MC mcs) {
        this.chassisNumber = chassisNumber;
        this.timeIn = timeIn;
        this.workTime = workTime;
        this.remainingTime = remainingTime;
        this.isPending = isPending;
        this.isMc = isMc;
        this.moNumber = moNumber;
        this.moDate = moDate;
        this.dealer = dealer;
        this.customer = customer;
        this.chassisModel = chassisModel;
        this.moQuantity = moQuantity;
        this.finishedNormalEntry = finishedNormalEntry;
        this.fileAttachments = fileAttachments;
        this.mcs = mcs;
    }

    private Date dateValueOfTimeIn(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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

    public Date makeMoDateStringAsDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        if(this.moDate != null){
            try {
                date =format.parse(this.moDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static class Attachment{

        public String name;
        public String url;

        public Attachment(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }

    public static class MC{
        public String id;
        public String sectionId;
        public String chassisNumber;
        public String remarks;
        public Boolean isResolved;
        public String dateTimeCreated;
        public String createdBy;

        public MC(String id, String sectionId, String chassisNumber, String remarks, Boolean isResolved, String dateTimeCreated, String createdBy) {
            this.id = id;
            this.sectionId = sectionId;
            this.chassisNumber = chassisNumber;
            this.remarks = remarks;
            this.isResolved = isResolved;
            this.dateTimeCreated = dateTimeCreated;
            this.createdBy = createdBy;
        }
    }
}
