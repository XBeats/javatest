package com.aitangba.test.calendar;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fhf11991 on 2017/4/10.
 */
public class CalendarTest {

    public static void main(String[] args) {
        init(new Date(), 30);
    }

    public static void init(Date selectedDate, int rang) {

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstCalendar.set(Calendar.HOUR_OF_DAY, 0);
        firstCalendar.set(Calendar.MINUTE, 0);
        firstCalendar.set(Calendar.SECOND, 0);
        firstCalendar.set(Calendar.MILLISECOND, 0);

        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.add(Calendar.DAY_OF_MONTH, rang);
        lastCalendar.set(Calendar.HOUR_OF_DAY, 0);
        lastCalendar.set(Calendar.MINUTE, 0);
        lastCalendar.set(Calendar.SECOND, 0);
        lastCalendar.set(Calendar.MILLISECOND, 0);
        lastCalendar.set(Calendar.DAY_OF_MONTH, lastCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        int firstDayInWeek = firstCalendar.get(Calendar.DAY_OF_WEEK);
        int lastDayInWeek = lastCalendar.get(Calendar.DAY_OF_WEEK);

        int headerDiffDays = firstDayInWeek - 1;
        int footerDiffDays = 7 - lastDayInWeek;
        int diffDays = differentDays(firstCalendar, lastCalendar) + 1;

        int totalDiffDays = headerDiffDays + footerDiffDays + diffDays;
        System.out.println("totalDiffDays = " + totalDiffDays);

        int weekCount = totalDiffDays / 7;
        System.out.println("weekCount = " + weekCount);
    }

    /**
     * date2比date1多的天数
     * @param cal1
     * @param cal2
     * @return
     */
    public static int differentDays(Calendar cal1, Calendar cal2) {
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) { //不同年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { //闰年
                    timeDistance += 366;
                } else {  //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else { //同一年
            return day2 - day1;
        }
    }
}
