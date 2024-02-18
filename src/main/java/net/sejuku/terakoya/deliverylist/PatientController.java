package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.sejuku.terakoya.deliverylist.PrescriptionDao.*;

@Controller
@RequestMapping(value = "/patient")
public class PatientController {
    Logger logger = LoggerFactory.getLogger(PatientController.class);
    private PrescriptionDao prescriptionDao;
    private PatientDao patientDao;
    private EnteralNutrientDao enteralNutrientDao;
    record PrescriptionEditForm(String id, String patientId, Boolean isEdit, String enteralNutrientId,
                                String dosage, Date dt, Integer days, Date deliveryDt, Integer doneDays, Boolean done) {}

    @Autowired
    PatientController(PrescriptionDao prescriptionDao, PatientDao patientDao, EnteralNutrientDao enteralNutrientDao) {
        this.prescriptionDao = prescriptionDao;
        this.patientDao = patientDao;
        this.enteralNutrientDao = enteralNutrientDao;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("prescriptionList", prescriptionDao.findAll());
        return "/patient/index";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String id) {
        logger.info("delete id is {}", id);
        patientDao.delete(id);
        return "redirect:/patient/";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam String id, Model model) {
        logger.debug("edit in");
        model.addAttribute("isEdit", true);
        model.addAttribute("prescription", prescriptionDao.find(id));
        model.addAttribute("patient", patientDao.findAll());
        model.addAttribute("enteralNutrient", enteralNutrientDao.findAll());
        return "/patient/edit";
    }

    @GetMapping("/new")
    public String new_entry(Model model) throws ParseException {
        logger.debug("new in");
        model.addAttribute("isEdit", false);
        model.addAttribute("prescription", new PrescriptionInfo(-1,-1,"",-1,"","",
                new SimpleDateFormat("MM/dd").parse(""),-1, new SimpleDateFormat("MM/dd").parse(""),-1,false));
        model.addAttribute("patientList", patientDao.findAll());
        return "/patient/edit";
    }

    @PostMapping("/register")
    public String registry(@ModelAttribute PrescriptionEditForm form) {
        logger.debug("registry in {}", form.id);
        if (form.isEdit) {
            prescriptionDao.update(new PrescriptionRecord(
                    Integer.parseInt(form.id),
                    form.patientId,
                    form.enteralNutrientId,
                    form.dosage,
                    form.dt,
                    form.days,
                    form.deliveryDt,
                    form.doneDays,
                    form.done
            ));
        } else {
            prescriptionDao.insert(new PrescriptionRecord(
                    Integer.parseInt(form.id),
                    form.patientId,
                    form.enteralNutrientId,
                    form.dosage,
                    form.dt,
                    form.days,
                    form.deliveryDt,
                    form.doneDays,
                    form.done
            ));
        }
        return "redirect:/patient/";
    }
}
