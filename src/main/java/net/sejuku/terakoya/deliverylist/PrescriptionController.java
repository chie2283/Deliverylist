package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static net.sejuku.terakoya.deliverylist.PrescriptionDao.*;
@Controller
@RequestMapping(value = "/prescription")
public class PrescriptionController {
    Logger logger = LoggerFactory.getLogger(PrescriptionController.class);
    private PrescriptionDao prescriptionDao;
    private DestinationDao destinationDao;
    private PatientDao patientDao;

    private EnteralNutrientDao enteralNutrientDao;
    record PrescriptionEditForm(String id, String destinationId, String patientId, String enteralNutrientId,
                                String dosage, Date dt, Integer days, Date deliveryDt, Integer doneDays, Boolean done, Boolean isEdit) {}

    @Autowired
    PrescriptionController(PrescriptionDao prescriptionDao, DestinationDao destinationDao, PatientDao patientDao, EnteralNutrientDao enteralNutrientDao) {
        this.prescriptionDao = prescriptionDao;
        this.destinationDao = destinationDao;
        this.patientDao = patientDao;
        this.enteralNutrientDao = enteralNutrientDao;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("destinationList", destinationDao.findAll());
        model.addAttribute("prescriptionList", prescriptionDao.findAll());
        return "/prescription/index";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String id) {
        logger.info("delete id is {}", id);
        prescriptionDao.delete(id);
        return "redirect:/prescription/";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam String id, Model model) {
        logger.debug("edit in");
        model.addAttribute("isEdit", true);
        model.addAttribute("prescription", prescriptionDao.find(id));
        model.addAttribute("destination", destinationDao.findAll());
        model.addAttribute("patient", patientDao.findAll());
        model.addAttribute("enteralNutrient", enteralNutrientDao.findAll());
        return "/prescription/edit";
    }

    @GetMapping("/new")
    public String new_entry(Model model) {
        logger.debug("new in");
        model.addAttribute("isEdit", false);
        model.addAttribute("prescription", new PrescriptionInfo(-1,0,"",0,"",0,
                "","", null,14,null,14,false));
        model.addAttribute("destinationList", destinationDao.findAll());
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("enteralNutrientList", enteralNutrientDao.findAll());
        return "/prescription/edit";
    }

    @PostMapping("/register")
    public String registry(@ModelAttribute PrescriptionEditForm form) {
        logger.debug("registry in {}", form.id);
        if (form.isEdit) {
            prescriptionDao.update(new PrescriptionRecord(
                    Integer.parseInt(form.id),
                    form.destinationId,
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
                    form.destinationId,
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
        return "redirect:/prescription/";
    }
}