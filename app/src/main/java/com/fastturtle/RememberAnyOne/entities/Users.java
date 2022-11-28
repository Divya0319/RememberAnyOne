package com.fastturtle.RememberAnyOne.entities;

public class Users {

    private int Id;
    private String Name;
    private String Email;
    private String MobileNo;
    private String Age;
    private String dob;
    private String imagePathUri;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getImagePathUri() {
        return imagePathUri;
    }

    public void setImagePathUri(String imagePathUri) {
        this.imagePathUri = imagePathUri;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
