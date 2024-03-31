package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Controller
@RequestMapping(value = "/calendar")
public class CalendarController {
    Logger logger = LoggerFactory.getLogger(CalendarController.class);
    private PrescriptionDao prescriptionDao;
    private DestinationDao destinationDao;
    private CalendarDay calendarDay;

    @Autowired
    CalendarController(PrescriptionDao prescriptionDao, DestinationDao destinationDao, CalendarDay calendarDay) {
        this.prescriptionDao = prescriptionDao;
        this.destinationDao = destinationDao;
        this.calendarDay = calendarDay;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("prescriptionList", prescriptionDao.findAll());
        model.addAttribute("yearMonth", calendarDay.ymList());
        model.addAttribute("days", calendarDay.list());
        var firstDay = LocalDate.parse(calendarDay.yearMonth() + "/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var lastDay = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
        model.addAttribute("schedules", calendarDay.getSchedule(firstDay, lastDay));
        return "/calendar";
    }

    @PostMapping("/")
    public String postIndex(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("index in");
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("prescriptionList", prescriptionDao.findDestinationId(destinationId));
        return "redirect:/calendar/";
    }

    @GetMapping("/calendar")
    public ModelAndView viewCalendarPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("calendarPage");
        return model;
    }
}
