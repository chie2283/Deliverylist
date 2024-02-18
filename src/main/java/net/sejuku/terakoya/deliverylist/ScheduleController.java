package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/schedule")
public class ScheduleController {
    Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private DestinationDao destinationDao;
    private PatientDao patientDao;

    @Autowired
    ScheduleController(DestinationDao destinationDao, PatientDao patientDao) {
        this.destinationDao = destinationDao;
        this.patientDao = patientDao;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("destinationList", destinationDao.findAll());
        model.addAttribute("patient", patientDao.findAll());
        return "/schedule/";
    }
}
