package net.sejuku.terakoya.deliverylist;

import net.sejuku.terakoya.deliverylist.PrescriptionDao.PrescriptionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarDay {
    private Year year;
    LocalDate ld;
    record Days(List<Integer> dateList, List<String> weekList) {};
    @Autowired
    private PrescriptionDao prescriptionDao;

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
        return yearMonth(LocalDate.now());
    }

    public String yearMonth(LocalDate src) {
        return src.format(DateTimeFormatter.ofPattern("yyyy/MM"));
    }

    public  ArrayList<Integer> getDaysList() {
        return getDaysList(LocalDate.now());
    }
    public ArrayList<Integer> getDaysList(LocalDate src) {
        var date = src.with(TemporalAdjusters.lastDayOfMonth());
        var dayList = new ArrayList<Integer>();
        for(int i = 1; i <= date.getDayOfMonth(); i++) {
            dayList.add(i);
        }
        return dayList;
    }

    public ArrayList<String> getWeekList() {
        return getWeekList(LocalDate.now());
    }
    public ArrayList<String> getWeekList(LocalDate src) {
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

    public ArrayList<String> ymList() {
        var ymList = new ArrayList<String>();
        ymList.add(yearMonth());
        ymList.add(yearMonth(LocalDate.now().plusMonths(1)));
        ymList.add(yearMonth(LocalDate.now().plusMonths(2)));
        return ymList;
    }

    public ArrayList<Days> list() {
        var list = new ArrayList<Days>();
        list.add(new Days(getDaysList(), getWeekList()));
        list.add(new Days(getDaysList(LocalDate.now().plusMonths(1)), getWeekList(LocalDate.now().plusMonths(1))));
        list.add(new Days(getDaysList(LocalDate.now().plusMonths(2)), getWeekList(LocalDate.now().plusMonths(2))));
        return list;
    }

    public ArrayList<List<Integer>> colorDays(LocalDate firstDay, LocalDate lastDay) {
        List<PrescriptionInfo> days = prescriptionDao.findDays();
        var colorDay = new ArrayList<Integer>();
        var colorDays = new ArrayList<List<Integer>>();
        for(int i = 0; i <= days.size(); i++) {
            LocalDate startDate = days.get(i).startDate();
            LocalDate endDate = days.get(i).endDate();
            if(startDate.isAfter(firstDay) && endDate.isBefore(lastDay)){
                 colorDay.add(days.get(i).days());
            }
        }
        colorDays.add(colorDay);
        return colorDays;
    }
}
