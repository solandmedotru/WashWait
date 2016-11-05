package ru.solandme.washwait.data;

public class Forecast {

    int weatherIds;
    double rainCounter;
    double snowCounter;
    double temperature;

    boolean isDirty() {
        return weatherIds < 600 || weatherIds < 700 && temperature > -10;
    }
}
