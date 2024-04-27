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

/**
 * カレンダーのデータ処理
 */
@Service
public class CalendarDay {
    record Days(List<Integer> dateList, List<String> weeksList) {};
    record RpDays(String rp, Integer day) {};
    record Done(String done, Integer day) {};
    record Schedule(String patientName, List<RpDays> rp, List<Done> done) {}
    private PrescriptionDao prescriptionDao;
    private PatientDao patientDao;

    /**
     * カレンダーで使用するクラスのインスタンス化
     * @param prescriptionDao　処方のデータベース関連の処理をまとめたクラス
     * @param patientDao　患者のデータベース関連の処理をまとめたクラス
     */
    @Autowired
    CalendarDay(PrescriptionDao prescriptionDao, PatientDao patientDao) {
        this.prescriptionDao = prescriptionDao;
        this.patientDao = patientDao;
    }

    /**
     * 現在の年月の取得
     * @return　年月の取得メソッド
     */
    public String yearMonth() {
        return yearMonth(LocalDate.now());
    }

    /**
     * 年月の取得
     * @param src　日付
     * @return　日付フォーマット(年/月)
     */
    public String yearMonth(LocalDate src) {
        return src.format(DateTimeFormatter.ofPattern("yyyy/MM"));
    }

    /**
     * 現在月の日付リスト
     * @return　月の日付リストメソッド
     */
    public  ArrayList<Integer> getDateList() {
        return getDateList(LocalDate.now());
    }

    /**
     * 月の日付リスト
     * @param src　日付
     * @return　月の日付リスト
     */
    public ArrayList<Integer> getDateList(LocalDate src) {
        var date = src.with(TemporalAdjusters.lastDayOfMonth());
        var dateList = new ArrayList<Integer>();
        for(int i = 1; i <= date.getDayOfMonth(); i++) {
            dateList.add(i);
        }
        return dateList;
    }

    /**
     * 現在月の曜日リスト
     * @return　月の曜日リストメソッド
     */
    public ArrayList<String> getWeeksList() {
        return getWeeksList(LocalDate.now());
    }

    /**
     * 月の曜日リスト
     * @param src　日付
     * @return　月の曜日リスト
     */
    public ArrayList<String> getWeeksList(LocalDate src) {
        var date = src.with(TemporalAdjusters.lastDayOfMonth());
        var weeksList = new ArrayList<String>();
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
            weeksList.add(weekStr);
        }
        return weeksList;
    }

    /**
     * カレンダー表示する年月のリスト
     * @return　3か月分（現在の年月～2か月後）の年月のリスト
     */
    public ArrayList<String> ymList() {
        var ymList = new ArrayList<String>();
        ymList.add(yearMonth());
        ymList.add(yearMonth(LocalDate.now().plusMonths(1)));
        ymList.add(yearMonth(LocalDate.now().plusMonths(2)));
        return ymList;
    }

    /**
     * カレンダー表示する日付・曜日のリスト
     * @return　3か月分（現在の年月～2か月後）の日付・曜日のリスト
     */
    public ArrayList<Days> list() {
        var list = new ArrayList<Days>();
        list.add(new Days(getDateList(), getWeeksList()));
        list.add(new Days(getDateList(LocalDate.now().plusMonths(1)), getWeeksList(LocalDate.now().plusMonths(1))));
        list.add(new Days(getDateList(LocalDate.now().plusMonths(2)), getWeeksList(LocalDate.now().plusMonths(2))));
        return list;
    }

    /**
     * 処方記録を患者毎でグループ化
     * @param destinationId　配達先のid
     * @return　患者毎でグループ化した処方記録
     */
    public Map<Integer, List<PrescriptionInfo>> grpByPatient(String destinationId) {
        List<PrescriptionInfo> days = this.prescriptionDao.findDays(destinationId);
        Map<Integer, List<PrescriptionInfo>> grpByPatient = days.stream().collect(Collectors.groupingBy(PrescriptionInfo::patientId));
        return grpByPatient;
    }

    /**
     * 処方記録をカレンダー表示
     * @param destinationId　配達先のid
     * @param firstDay　現在の年月の月初
     * @param lastDay　2か月後の月末
     * @return　カレンダーに患者毎の処方済と配達済の色付け
     */
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
                    } else {
                        doneColorDay.add(new Done("余白", elementValue.doneDays()));
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
