package com.dasayantist.msacco.model;

public class Loan {
    private String id, name, phone, location, area;

    public Loan() {
    }

    public Loan(String name, String phone, String location, String area) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.area = area;
    }

    public Loan(String id, String name, String phone, String location, String area) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}

