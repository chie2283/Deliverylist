package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static net.sejuku.terakoya.deliverylist.DestinationDao.DestinationRecord;

@Controller
@RequestMapping(value = "/destination")
public class DestinationController {
    Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private DestinationDao destinationDao;
    record DestinationEditForm(String destinationId, String destinationName, Boolean isEdit) {}

    @Autowired
    DestinationController(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("destinationList", destinationDao.findAll());
        return "destination/index";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String id) {
        logger.info("delete id is {}", id);
        destinationDao.delete(id);
        return "redirect:/destination/";
    }

    @PostMapping("/register")
    public String registry(@ModelAttribute DestinationEditForm form) {
        logger.debug("registry in {}", form.destinationId);
        if (form.isEdit) {
            destinationDao.update(new DestinationRecord(
                    Integer.parseInt(form.destinationId),
                    form.destinationName
            ));
        } else {
            destinationDao.insert(new DestinationRecord(
                    Integer.parseInt(form.destinationId),
                    form.destinationName
            ));
        }
        return "redirect:/destination/";
    }
}
