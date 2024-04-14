package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static net.sejuku.terakoya.deliverylist.PatientDao.*;

@Controller
@RequestMapping(value = "/patient")
public class PatientController {
    Logger logger = LoggerFactory.getLogger(PatientController.class);
    private PatientDao patientDao;
    record PatientEditForm(String patientId, String patientName, LocalDate patientBirthday, Boolean isEdit, String destinationId) {}

    @Autowired
    PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @GetMapping("/{destinationId}")
    public String getIndex(@PathVariable(name="destinationId") String destinationId, Model model) {
        logger.debug("index get in");
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("destinationId", destinationId);
        return "patient/index";
    }

    @PostMapping("/")
    public String postIndex(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("index post in");
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("destinationId", destinationId);
        return "patient/index";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String id, String destinationId) {
        logger.info("delete id is {}", id);
        patientDao.delete(id);
        return "redirect:patient/" + destinationId;
    }

    @GetMapping("/edit")
    public String edit(@RequestParam String id, String destinationId, Model model) {
        logger.debug("edit in");
        model.addAttribute("isEdit", true);
        model.addAttribute("patient", patientDao.find(id));
        model.addAttribute("destinationId", destinationId);
        return "patient/edit";
    }

    @GetMapping("/new")
    public String new_entry(@RequestParam String destinationId, Model model) {
        logger.debug("new in");
        model.addAttribute("isEdit", false);
        model.addAttribute("patient", new PatientInfo(-1,"",null));
        model.addAttribute("destinationId", destinationId);
        return "patient/edit";
    }

    @PostMapping("/register")
    public String registry(@ModelAttribute PatientEditForm form) {
        logger.debug("registry in {}", form.patientId);
        if (form.isEdit) {
            patientDao.update(new PatientRecord(
                    Integer.parseInt(form.patientId),
                    form.patientName,
                    form.patientBirthday
            ));
        } else {
            patientDao.insert(new PatientRecord(
                    Integer.parseInt(form.patientId),
                    form.patientName,
                    form.patientBirthday
            ));
        }
        return "redirect:patient/" + form.destinationId;
    }
}
