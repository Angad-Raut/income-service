package com.projectx.income_service.utils;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

@Component
public class IncomeUtils {
    public static final String INCOME_DETAILS_NOT_EXISTS="Income details not present in the system!!";
    public static final String INCOME_DETAILS_ALREADY_EXISTS="Income details already exists in the system!!";
    public static final String INCOME_TYPE_NOT_CHANGED="Income type can not be change!!";
    public static final String DASH="-";
    public static final String OPEN="Open";
    public static final String CLOSE="Closed";
    public static final String SALARY_TYPE="SALARY";
    public static final String OTHER_TYPE="OTHER";
    public static final String ISO_DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String VIEW_DATE_FORMAT="dd MMMM yyyy";
    public static final String toExpenseDate(Date expenseDate) {
        SimpleDateFormat format = new SimpleDateFormat(VIEW_DATE_FORMAT);
        return format.format(expenseDate);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getISODate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(VIEW_DATE_FORMAT);
        Date beforeDate = format.parse(date);
        SimpleDateFormat ISOFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        String convertedDate = ISOFormat.format(beforeDate);
        Date finalDate = ISOFormat.parse(convertedDate);
        return localDateTimeToDate(dateToLocalDateTime(finalDate));
    }
    public static String toINRFormat(Double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        format.setCurrency(java.util.Currency.getInstance("INR"));
        String formattedAmount = format.format(amount);
        return formattedAmount;
    }
}
