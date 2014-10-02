package com.example.my_computer.myapplication;

public class Read_contacts {
    String Name;
    String Number;
    private String photo;
    private int type;

    Read_contacts(String name, String number, String photo) {

        Number = number;
        Name = name;

        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;

    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }


}
