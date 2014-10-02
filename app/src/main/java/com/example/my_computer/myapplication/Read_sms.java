package com.example.my_computer.myapplication;

public class Read_sms {

    String name_or_number;
    String msg;
    int type;

    Read_sms(String name_or_number, String msg, int type) {
        this.name_or_number = name_or_number;
        this.msg = msg;
        this.type = type;


    }

    public String getName() {


        return name_or_number;
    }

    public void setName(String name_or_number) {
        this.name_or_number = name_or_number;


    }

    public String getNumber() {
        return msg;
    }

    public void setNumber(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
