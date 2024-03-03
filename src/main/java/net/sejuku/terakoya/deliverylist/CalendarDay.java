package net.sejuku.terakoya.deliverylist;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

@Service
public class CalendarDay {
    private Year year;
    LocalDate ld;

    public CalendarDay() {
        this.setYear();
    }

    private void setYear() {
        this.year = Year.now();
    }

    private Year getYear() {
        return this.year;
    }

    private void setLd(int month) {
        this.ld = LocalDate.of(this.getYear().getValue(), month, 1);
    }

    private LocalDate getLd() {
        return this.ld;
    }

    public String yearMonth() {
        for(int n = 1 ; n <= 12; n++) {
            this.setLd(n);
        }
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    }
    public ArrayList<Integer> date() {
        var date = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        var dayList = new ArrayList<Integer>();
        for(int i = 1; i <= date.getDayOfMonth(); i++) {
            dayList.add(i);
        }
        return dayList;
    }


    public ArrayList<String> week() {
        var week = getLd().getDayOfWeek();
        var e = new ArrayList<DayOfWeek>();
        e.add(DayOfWeek.MONDAY);
        e.add(DayOfWeek.TUESDAY);
        e.add(DayOfWeek.WEDNESDAY);
        e.add(DayOfWeek.THURSDAY);
        e.add(DayOfWeek.FRIDAY);
        e.add(DayOfWeek.SATURDAY);
        e.add(DayOfWeek.SUNDAY);

        var list = new ArrayList<String>();
        list.add("月");
        list.add("火");
        list.add("水");
        list.add("木");
        list.add("金");
        list.add("土");
        list.add("日");

        String str = list.get(e.indexOf(week));

        var weekList = new ArrayList<String>();
        for(int date = 1; date <= getLd().getDayOfMonth(); date++ ) {
            weekList.add(str);
        }
        return weekList;
    }
}
