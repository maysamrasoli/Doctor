package ir.medxhub.doctor;

import java.io.Serializable;

import ir.medxhub.doctor.util.picker.PersianCalendar;

/**
 * Created by mohammad on 1/3/2016.
 */
@SuppressWarnings("serial")
public class MyDate implements Serializable {
    int year;
    int month;
    int day;

    public MyDate() {
        final PersianCalendar persianCalendar = new PersianCalendar();
        year = persianCalendar.getPersianYear();
        month = persianCalendar.getPersianMonth();
        day = persianCalendar.getPersianDay();
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
