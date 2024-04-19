package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DestinationRestController {
    Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private DestinationDao destinationDao;

    record DestinationEditForm(String destinationId, String destinationName, Boolean isEdit) {}

    @Autowired
    DestinationRestController(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
    }

    @GetMapping("/destination/{destinationId}")
    public DestinationDao.DestinationInfo index(@PathVariable("destinationId") String id) {
        return destinationDao.find(id);
    }
}
