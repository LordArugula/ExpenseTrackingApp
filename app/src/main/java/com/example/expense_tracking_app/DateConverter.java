package com.example.expense_tracking_app;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class DateConverter {
    @TypeConverter
    public static LocalDate fromEpochDay(String value) {
        return LocalDate.parse(value);
    }

    @TypeConverter
    public static String dateToEpochDay(LocalDate date) {
        return date.toString();
//        return date.toEpochDay();
    }
}
