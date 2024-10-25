package com.example.hotrovieclam.Model;

public class CompanyInfo {
    private String recruiterName;
    private String phoneNumber;
    private String companyMail;
    private String companyName;
    private String location;
    private String website;
    private String frontCCCDUrl;
    private String backCCCDUrl;
    private String companyCertUrl;

    // Constructor mặc định (bắt buộc cho Firebase)
    public CompanyInfo() {
    }

    // Constructor có tham số
    public CompanyInfo(String recruiterName, String phoneNumber, String companyMail,
                       String companyName, String location, String website,
                       String frontCCCDUrl, String backCCCDUrl, String companyCertUrl) {
        this.recruiterName = recruiterName;
        this.phoneNumber = phoneNumber;
        this.companyMail = companyMail;
        this.companyName = companyName;
        this.location = location;
        this.website = website;
        this.frontCCCDUrl = frontCCCDUrl;
        this.backCCCDUrl = backCCCDUrl;
        this.companyCertUrl = companyCertUrl;
    }

    // Getter và Setter cho các thuộc tính
    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFrontCCCDUrl() {
        return frontCCCDUrl;
    }

    public void setFrontCCCDUrl(String frontCCCDUrl) {
        this.frontCCCDUrl = frontCCCDUrl;
    }

    public String getBackCCCDUrl() {
        return backCCCDUrl;
    }

    public void setBackCCCDUrl(String backCCCDUrl) {
        this.backCCCDUrl = backCCCDUrl;
    }

    public String getCompanyCertUrl() {
        return companyCertUrl;
    }

    public void setCompanyCertUrl(String companyCertUrl) {
        this.companyCertUrl = companyCertUrl;
    }
}
