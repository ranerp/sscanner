package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class OrderHeaderObject {

    String title;
    String company;
    String address;
    String city;

    public OrderHeaderObject(String title, String company, String address, String city) {
        this.title = title;
        this.company = company;
        this.address = address;
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
