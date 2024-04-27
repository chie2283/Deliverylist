package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class HealthCheckController {
    Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private DestinationDao destinationDao;

    record DestinationEditForm(String destinationId, String destinationName, Boolean isEdit) {}

    @Autowired
    HealthCheckController(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
    }

    @GetMapping("/")
    public ResponseEntity index() {
        logger.info("health check recv");
        return ResponseEntity.ok().build();
    }
}
