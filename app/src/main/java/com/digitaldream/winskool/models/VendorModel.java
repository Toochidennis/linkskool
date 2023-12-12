package com.digitaldream.winskool.models;

public class VendorModel {

    private String mId;
    private String mCustomerName;
    private String mCustomerId;
    private String mCustomerAddress;
    private String mCustomerPhone;
    private String mCustomerEmail;

    public String getId() {
        return mId;
    }

    public void setId(String sId) {
        mId = sId;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String sCustomerName) {
        mCustomerName = sCustomerName;
    }

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String sCustomerId) {
        mCustomerId = sCustomerId;
    }

    public String getCustomerAddress() {
        return mCustomerAddress;
    }

    public void setCustomerAddress(String sCustomerAddress) {
        mCustomerAddress = sCustomerAddress;
    }

    public String getCustomerPhone() {
        return mCustomerPhone;
    }

    public void setCustomerPhone(String sCustomerPhone) {
        mCustomerPhone = sCustomerPhone;
    }

    public String getCustomerEmail() {
        return mCustomerEmail;
    }

    public void setCustomerEmail(String sCustomerEmail) {
        mCustomerEmail = sCustomerEmail;
    }
}
