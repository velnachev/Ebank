package com.softuni.ebank.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rate {
    private String name;
    private double rate;

    public Rate(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public Rate() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
