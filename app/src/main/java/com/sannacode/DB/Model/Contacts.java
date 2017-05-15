package com.sannacode.DB.Model;

import java.util.Comparator;

public class Contacts {

    private int id;
    private String name;
    private String number;
    private String email;

    public Contacts() {
    }

    public Contacts(String name, String number) {
        setName(name);
        setNumber(number);
    }

    public Contacts(String name, String number, String email) {
        setName(name);
        setNumber(number);
        setEmail(email);
    }

    public Contacts(int id, String name, String number, String email) {
        setId(id);
        setName(name);
        setNumber(number);
        setEmail(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class SortBeName implements Comparator<Contacts> {
        @Override
        public int compare(Contacts o1, Contacts o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
