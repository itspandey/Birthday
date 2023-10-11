package com.example.birthday;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarUtils {

    public static LocalDate selectedDate;

    public static ArrayList<LocalDate> getDaysInMonth(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        int daysInMonth = date.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(date.withDayOfMonth(i));
        }

        return daysInMonthArray;
    }

    public static ArrayList<LocalDate> getDaysInWeek(LocalDate date) {
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate current = sundayForDate(date);
        LocalDate endDate = current.plusDays(6); // Get a week's dates

        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            daysInWeekArray.add(current);
            current = current.plusDays(1);
        }

        return daysInWeekArray;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        // Logic to find the Sunday of the week containing the provided date
        return current.with(DayOfWeek.SUNDAY);
    }
}
