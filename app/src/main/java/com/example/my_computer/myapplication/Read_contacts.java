package com.example.my_computer.myapplication;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class Read_contacts {
    String Name;
    String Number;
    private String photo;

    public Read_contacts(String name, String phoneNumber, String photo) {

        this.Name = name;
        this.photo = photo;
        try {
            if (phoneNumber.charAt(0) == '+') {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "");

                String a = "0" + String.valueOf(numberProto.getNationalNumber());
                Number = a;
            } else {
                Number = phoneNumber;
            }
        } catch (Exception e) {
            Number = null;
        }
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
