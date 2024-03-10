package net.sejuku.terakoya.deliverylist;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarDay {
    private Year year;
    LocalDate ld;
    record Days(List<Integer> dateList, List<String> weekList) {};

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

    public  ArrayList<Integer> date() {
        return date(LocalDate.now());
    }
    public ArrayList<Integer> date(LocalDate src) {
        var date = src.with(TemporalAdjusters.lastDayOfMonth());
        var dayList = new ArrayList<Integer>();
        for(int i = 1; i <= date.getDayOfMonth(); i++) {
            dayList.add(i);
        }
        return dayList;
    }

    public ArrayList<String> week() {
        return week(LocalDate.now());
    }
    public ArrayList<String> week(LocalDate src) {
        var date = src.with(TemporalAdjusters.lastDayOfMonth());
        var weekList = new ArrayList<String>();
        for(int i = 1; i <= date.getDayOfMonth(); i++ ) {
            var week = LocalDate.of(date.getYear(), date.getMonth(), i).getDayOfWeek();
            var weekStr = switch (week) {
                case MONDAY -> "月";
                case TUESDAY -> "火";
                case WEDNESDAY -> "水";
                case THURSDAY -> "木";
                case FRIDAY -> "金";
                case SATURDAY -> "土";
                case SUNDAY -> "日";
            };
            weekList.add(weekStr);
        }
        return weekList;
    }

    public ArrayList<Days> list(){
        var list = new ArrayList<Days>();
        list.add(new Days(date(), week()));
        list.add(new Days(date(LocalDate.now().plusMonths(1)), week(LocalDate.now().plusMonths(1))));
        list.add(new Days(date(LocalDate.now().plusMonths(2)), week(LocalDate.now().plusMonths(2))));
        return list;
    }
}
