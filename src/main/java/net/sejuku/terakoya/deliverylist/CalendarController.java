package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Controller
/**
 * カレンダー画面のコントローラー
 */
@RequestMapping(value = "/calendar")
public class CalendarController {
    Logger logger = LoggerFactory.getLogger(CalendarController.class);
    private PrescriptionDao prescriptionDao;
    private DestinationDao destinationDao;
    private CalendarDay calendarDay;

    /**
     * カレンダーで使用するクラスのインスタンス化
     * @param prescriptionDao　処方のデータベース関連の処理をまとめたクラス
     * @param destinationDao　配達先のデータベース関連の処理をまとめたクラス
     * @param calendarDay　カレンダーに関する処理をまとめたクラス
     */
    @Autowired
    CalendarController(PrescriptionDao prescriptionDao, DestinationDao destinationDao, CalendarDay calendarDay) {
        this.prescriptionDao = prescriptionDao;
        this.destinationDao = destinationDao;
        this.calendarDay = calendarDay;
    }

    /**
     * 画面表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　表示画面のHTMLファイル
     */
    @GetMapping("/{destinationId}")
    public String getIndex(@PathVariable(name="destinationId") String destinationId, Model model) {
        logger.debug("index get in");
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("prescriptionList", prescriptionDao.findDestinationId(destinationId));
        model.addAttribute("yearMonth", calendarDay.ymList());
        model.addAttribute("days", calendarDay.list());
        var firstDay = LocalDate.parse(calendarDay.yearMonth() + "/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var lastDay = LocalDate.now().plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        model.addAttribute("schedules", calendarDay.getSchedule(destinationId, firstDay, lastDay));
        return "calendar";
    }

    /**
     * 処方記録画面からカレンダー画面への表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @PostMapping("/")
    public String postIndex(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("index post in");
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("prescriptionList", prescriptionDao.findDestinationId(destinationId));
        model.addAttribute("yearMonth", calendarDay.ymList());
        model.addAttribute("days", calendarDay.list());
        var firstDay = LocalDate.parse(calendarDay.yearMonth() + "/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var lastDay = LocalDate.now().plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        model.addAttribute("schedules", calendarDay.getSchedule(destinationId, firstDay, lastDay));
        return "calendar";
    }
}
