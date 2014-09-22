package com.example.my_computer.myapplication;

public class Read_call_log {


    String Name;
    String Number;
    int type;

    Read_call_log(String name, String number, int type) {

        Number = number;
        if (name != null) {

            Name = name;
        } else {

            Name = number;

        }
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
