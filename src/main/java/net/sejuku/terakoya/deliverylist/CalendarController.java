package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/calendar")
public class CalendarController {
    Logger logger = LoggerFactory.getLogger(CalendarController.class);
    private PrescriptionDao prescriptionDao;
    private DestinationDao destinationDao;
    private PatientDao patientDao;
    private CalendarDay calendarDay;

    @Autowired
    CalendarController(PrescriptionDao prescriptionDao, DestinationDao destinationDao, PatientDao patientDao, CalendarDay calendarDay) {
        this.prescriptionDao = prescriptionDao;
        this.destinationDao = destinationDao;
        this.patientDao = patientDao;
        this.calendarDay = calendarDay;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("destinationList", destinationDao.findAll());
        model.addAttribute("prescriptionList", prescriptionDao.findAll());
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("calendarYearMonth", calendarDay.yearMonth());
        model.addAttribute("calendarDate", calendarDay.date());
        model.addAttribute("calendarWeek", calendarDay.week());
        return "/calendar";
    }
}
