package net.sejuku.terakoya.deliverylist;

import net.sejuku.terakoya.deliverylist.PrescriptionDao.PrescriptionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class CalendarDay {
    record Days(List<Integer> dateList, List<String> weekList) {};
    record RpDays(String rp, Integer day) {};
    record Done(String done, Integer day) {};
    record Schedule(String patientName, List<RpDays> rp, List<Done> done) {}
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

    public Map<Integer, List<PrescriptionInfo>> grpByPatient(String destinationId) {
        List<PrescriptionInfo> days = this.prescriptionDao.findDays(destinationId);
        Map<Integer, List<PrescriptionInfo>> grpByPatient = days.stream().collect(Collectors.groupingBy(PrescriptionInfo::patientId));
        return grpByPatient;
    }

    public List<List<Schedule>> getSchedule(String destinationId, LocalDate firstDay, LocalDate lastDay) {
        var getSchedule = new ArrayList<List<Schedule>>();
        for(Map.Entry<Integer, List<PrescriptionInfo>> element : grpByPatient(destinationId).entrySet()) {
            String id = String.valueOf(element.getKey());
            String patientName = this.patientDao.find(id).name();
            var scheduleList = new ArrayList<Schedule>();
            var rpColorDay = new ArrayList<RpDays>();
            var doneColorDay = new ArrayList<Done>();
            for(PrescriptionInfo elementValue : element.getValue()) {
                LocalDate startDate = elementValue.startDate();
                LocalDate endDate = elementValue.endDate();
                if (startDate.isAfter(firstDay) && endDate.isBefore(lastDay)) {
                    if (rpColorDay.isEmpty() && doneColorDay.isEmpty()) {
                        long daysBetween = DAYS.between(firstDay, startDate);
                        rpColorDay.add(new RpDays("余白", (int) daysBetween));
                        doneColorDay.add(new Done("余白", (int) daysBetween));
                    }
                    rpColorDay.add(new RpDays("処方", elementValue.days()));
                    if (elementValue.done()) {
                        doneColorDay.add(new Done("配達済", elementValue.doneDays()));
                    }
                } else if (startDate.isBefore(firstDay) && endDate.isAfter(firstDay) && endDate.isBefore(lastDay)) {
                    long daysBetween = DAYS.between(startDate, firstDay);
                    rpColorDay.add(new RpDays("処方", elementValue.days() - (int) daysBetween));
                    if (elementValue.done()) {
                        doneColorDay.add(new Done("配達済", elementValue.doneDays() - (int) daysBetween));
                    } else {
                        doneColorDay.add(new Done("余白", elementValue.doneDays() - (int) daysBetween));
                    }
                }
            }
            scheduleList.add(new Schedule(patientName, rpColorDay, doneColorDay));
            getSchedule.add(scheduleList);
        }
        return getSchedule;
    }
}