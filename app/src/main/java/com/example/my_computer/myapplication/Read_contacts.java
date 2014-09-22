package com.example.my_computer.myapplication;

public class Read_contacts {
    String Name, Number;

    Read_contacts(String name, String number) {

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


}
