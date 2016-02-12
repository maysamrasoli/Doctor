package ir.medxhub.doctor.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.medxhub.doctor.R;

/**
 * Created by Alireza Eslamifar on 31/05/2015.
 */
public class PersianCalendar extends ActionBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        myDate myDate[];
        myDate = pre(getApplication(), CurrentCalendar());
        for (int i = 0; i < 7; i++) {
            Log.d("d", myDate[i].getDay() + "");
        }
        myDate = current(getApplication(), CurrentCalendar());
        for (int i = 0; i < 7; i++) {
            Log.d("d", myDate[i].getDay() + "");
        }
        myDate = next(getApplication(), CurrentCalendar());
        for (int i = 0; i < 7; i++) {
            Log.d("d", myDate[i].getDay() + "");
        }
    }


    public class SolarCalendar {

        public String strWeekDay = "";
        public String strMonth = "";

        public int date;
        public int month;
        public int year;
        Context context;

        public SolarCalendar(Context context) {
            this.context = context;
            Date MiladiDate = new Date();
            calcSolarCalendar(MiladiDate);
        }

        public SolarCalendar(Date MiladiDate, Context context) {
            this.context = context;
            calcSolarCalendar(MiladiDate);
        }

        private void calcSolarCalendar(Date MiladiDate) {

            int ld;

            int miladiYear = MiladiDate.getYear() + 1900;
            int miladiMonth = MiladiDate.getMonth() + 1;
            int miladiDate = MiladiDate.getDate();
            int WeekDay = MiladiDate.getDay();

            int[] buf1 = new int[12];
            int[] buf2 = new int[12];

            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;

            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;

            if ((miladiYear % 4) != 0) {
                date = buf1[miladiMonth - 1] + miladiDate;

                if (date > 79) {
                    date = date - 79;
                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = date / 31;
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    if ((miladiYear > 1996) && (miladiYear % 4) == 1) {
                        ld = 11;
                    } else {
                        ld = 10;
                    }
                    date = date + ld;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }
            } else {
                date = buf2[miladiMonth - 1] + miladiDate;

                if (miladiYear >= 1996) {
                    ld = 79;
                } else {
                    ld = 80;
                }
                if (date > ld) {
                    date = date - ld;

                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = (date / 31);
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    date = date + 10;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }

            }
            switch (month) {
                case 1:
                    strMonth = context.getString(R.string.m1);
                    break;
                case 2:
                    strMonth = context.getString(R.string.m2);
                    break;
                case 3:
                    strMonth = context.getString(R.string.m3);
                    break;
                case 4:
                    strMonth = context.getResources().getString(R.string.m4);
                    break;
                case 5:
                    strMonth = context.getResources().getString(R.string.m5);
                    break;
                case 6:
                    strMonth = context.getResources().getString(R.string.m6);
                    break;
                case 7:
                    strMonth = context.getResources().getString(R.string.m7);
                    break;
                case 8:
                    strMonth = context.getResources().getString(R.string.m8);
                    break;
                case 9:
                    strMonth = context.getResources().getString(R.string.m9);
                    break;
                case 10:
                    strMonth = context.getResources().getString(R.string.m10);
                    break;
                case 11:
                    strMonth = context.getResources().getString(R.string.m11);
                    break;
                case 12:
                    strMonth = context.getResources().getString(R.string.m12);
                    break;
            }

            switch (WeekDay) {

                case 0:
                    strWeekDay = context.getResources().getString(R.string.d2);
                    break;
                case 1:
                    strWeekDay = context.getResources().getString(R.string.d3);
                    break;
                case 2:
                    strWeekDay = context.getResources().getString(R.string.d4);
                    break;
                case 3:
                    strWeekDay = context.getResources().getString(R.string.d5);
                    break;
                case 4:
                    strWeekDay = context.getResources().getString(R.string.d6);
                    break;
                case 5:
                    strWeekDay = context.getResources().getString(R.string.d7);
                    break;
                case 6:
                    strWeekDay = context.getResources().getString(R.string.d1);
                    break;
            }

        }

    }

    public String getCurrentPersianDate(Context c) {
        Locale loc = new Locale("en_US");
        SolarCalendar sc = new SolarCalendar(c);
        return String.valueOf(sc.year) + "-" + String.format(loc, "%02d",
                sc.month) + "-" + String.format(loc, "%02d", sc.date);
    }

    public String[] getCurrentPersianDate(Context c, int i) {
        Locale loc = new Locale("en_US");
        SolarCalendar sc = new SolarCalendar(c);
        return new String[]{String.valueOf(sc.year), String.format(loc, "%02d",
                sc.month), String.format(loc, "%02d", sc.date)};
    }

    public myDate getStartDateOfWeek(Context context) {
        PersianCalendar util = new PersianCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.add(Calendar.DATE, -7);
        SolarCalendar sc = util.new SolarCalendar(calendar.getTime(), context);
        myDate myDate = new myDate();
        return myDate;
    }

    public Calendar CurrentCalendar() {
        Calendar calendar = Calendar.getInstance();
        return calendar;
    }

    public Calendar CurrentCalendarStartWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.add(Calendar.DATE, -7);
        return calendar;
    }

    public myDate[] current(Context context, Calendar holder) {
        PersianCalendar util = new PersianCalendar();
        Calendar calendar = holder;
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//        calendar.add(Calendar.DATE, -7);
        myDate myDate[] = new myDate[7];
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, i);
            SolarCalendar sc = util.new SolarCalendar(calendar.getTime(), context);
            myDate[i] = new myDate(sc);
            calendar.add(Calendar.DATE, -i);
        }
        return myDate;
    }

    public myDate[] next(Context context, Calendar holder) {
        PersianCalendar util = new PersianCalendar();
        Calendar calendar = holder;
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        myDate myDate[] = new myDate[7];
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, i);
            SolarCalendar sc = util.new SolarCalendar(calendar.getTime(), context);
            calendar.add(Calendar.DATE, -i);
            myDate[i] = new myDate(sc);
        }
        return myDate;
    }

    public myDate[] pre(Context context, Calendar holder) {
        PersianCalendar util = new PersianCalendar();
        Calendar calendar = holder;
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.add(Calendar.DATE, -14);
        myDate myDate[] = new myDate[7];
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, i);
            SolarCalendar sc = util.new SolarCalendar(calendar.getTime(), context);
            calendar.add(Calendar.DATE, -i);
            myDate[i] = new myDate(sc);
        }
        return myDate;
    }

    public class myDate {
        int year;
        int mount;
        int day;
        String strDay;
        String strMount;

        myDate() {
        }

        myDate(SolarCalendar sc) {
            this.year = sc.year;
            this.mount = sc.month;
            this.day = sc.date;
            this.strDay = sc.strWeekDay;
            this.strMount = sc.strMonth;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMount() {
            return mount;
        }

        public void setMount(int mount) {
            this.mount = mount;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public String getStrDay() {
            return strDay;
        }

        public void setStrDay(String strDay) {
            this.strDay = strDay;
        }

        public String getStrMount() {
            return strMount;
        }

        public void setStrMount(String strMount) {
            this.strMount = strMount;
        }
    }
}