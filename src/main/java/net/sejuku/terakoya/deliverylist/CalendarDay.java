package net.sejuku.terakoya.deliverylist;

import net.sejuku.terakoya.deliverylist.PrescriptionDao.PrescriptionInfo;
import net.sejuku.terakoya.deliverylist.PatientDao.PatientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class CalendarDay {
    private Year year;
    LocalDate ld;
    record Days(List<Integer> dateList, List<String> weekList) {};
    record RpDays(String rpDays, Integer day) {};
    record Done(String done, Integer day) {};
    private PrescriptionDao prescriptionDao;
    private PatientDao patientDao;

    @Autowired
    CalendarDay(PrescriptionDao prescriptionDao, PatientDao patientDao) {
        this.prescriptionDao = prescriptionDao;
        this.patientDao = patientDao;
    }

    public String yearMonth() {
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

    public Map<Integer, List<PrescriptionInfo>> grpByPatient() {
        List<PrescriptionInfo> days = this.prescriptionDao.findDays();
        Map<Integer, List<PrescriptionInfo>> grpByPatient = days.stream().collect(Collectors.groupingBy(PrescriptionInfo::patientId));
        return grpByPatient;
    }

    public ArrayList<PatientInfo> patientList() {
        var patientList = new ArrayList<PatientInfo>();
        for(Map.Entry<Integer, List<PrescriptionInfo>> element : grpByPatient().entrySet()) {
                String id = String.valueOf(element.getKey());
                patientList.add(this.patientDao.find(id));
        }
        return patientList;
    }

    public ArrayList<List<RpDays>> rpColor(LocalDate firstDay, LocalDate lastDay) {
        var rpColorDay = new ArrayList<RpDays>();
        var rpColorDays = new ArrayList<List<RpDays>>();

        for(Map.Entry<Integer, List<PrescriptionInfo>> element : grpByPatient().entrySet()) {
            for(PrescriptionInfo elementValue : element.getValue()) {
                LocalDate startDate = elementValue.startDate();
                LocalDate endDate = elementValue.endDate();
                if (startDate.isAfter(firstDay) && endDate.isBefore(lastDay)){
                    long daysBetween = DAYS.between(firstDay, startDate);
                    rpColorDay.add(new RpDays("余白", (int)daysBetween));
                    rpColorDay.add(new RpDays("処方", elementValue.days()));
                }else if(startDate.isBefore(firstDay) && endDate.isAfter(firstDay)){
                    long daysBetween = DAYS.between(startDate, firstDay);
                    rpColorDay.add(new RpDays("処方", elementValue.days() - (int)daysBetween));
                }
                rpColorDays.add(rpColorDay);
            }
        }
        return rpColorDays;
    }

    public ArrayList<List<Done>> doneColor(LocalDate firstDay) {
        var doneColorDay = new ArrayList<Done>();
        var doneColorDays = new ArrayList<List<Done>>();

        for(Map.Entry<Integer, List<PrescriptionInfo>> element : grpByPatient().entrySet()) {
            for(PrescriptionInfo elementValue : element.getValue()) {
                LocalDate startDate = elementValue.startDate();
                if (startDate.isAfter(firstDay)){
                    long daysBetween = DAYS.between(firstDay, startDate);
                    doneColorDay.add(new Done("余白", (int)daysBetween));
                    doneColorDay.add(new Done("配達済", elementValue.doneDays()));
                }else if(startDate.isBefore(firstDay)){
                    long daysBetween = DAYS.between(startDate, firstDay);
                    doneColorDay.add(new Done("配達済", elementValue.doneDays() - (int)daysBetween));
                }
                doneColorDays.add(doneColorDay);
            }
        }
        return doneColorDays;
    }
}
