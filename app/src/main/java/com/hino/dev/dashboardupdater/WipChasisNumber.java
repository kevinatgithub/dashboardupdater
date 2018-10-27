package com.hino.dev.dashboardupdater;

import java.util.ArrayList;

public class WipChasisNumber {

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

    public WipChasisNumber(String chassisNumber, String timeIn, Integer workTime, Boolean isPending, Boolean isMc, String moNumber, String moDate, String dealer, String customer, String chassisModel, Integer moQuantity, String[] fileAttachments) {
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
}
