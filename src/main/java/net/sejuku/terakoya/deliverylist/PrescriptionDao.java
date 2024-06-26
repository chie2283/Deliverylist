package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionDao {
    Logger logger = LoggerFactory.getLogger(PrescriptionDao.class);
    private final JdbcTemplate jdbcTemplate;

    record PrescriptionInfo(
            Integer id,
            Integer destinationId,
            String destinationName,
            Integer patientId,
            String patientName,
            Integer enteralNutrientId,
            String enteralNutrientName,
            String dosage,
            LocalDate dt,
            Integer days,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate deliveryDt,
            Integer doneDays,
            Boolean done
    ) {}

    record PrescriptionRecord(
            Integer id,
            String destinationId,
            String patientId,
            String enteralNutrientId,
            String dosage,
            LocalDate dt,
            Integer days,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate deliveryDt,
            Integer doneDays,
            Boolean done
    ) {}

    @Autowired
    PrescriptionDao(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    List<PrescriptionInfo> findAll() {
        var query = """            
            SELECT rp.id AS id,
                   d.id AS destination_id,
                   d.name AS destination_name,
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteral_nutrient_id,
                   e.name AS enteral_nutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.start_date AS start_date,
                   rp.end_date AS end_date,
                   rp.delivery_dt AS delivery_dt,
                   rp.done_days AS done_days,
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN destination d ON(rp.destination_id = d.id)
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteral_nutrient e ON(rp.enteral_nutrient_id = e.id)
        """;
        logger.debug(query);
        List<PrescriptionInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class));
        return results;
    }

    PrescriptionInfo find(String id) {
        var query = """
            SELECT rp.id AS id,
                   d.id AS destination_id,
                   d.name AS destination_name,
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteral_nutrient_id,
                   e.name AS enteral_nutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.start_date AS start_date,
                   rp.end_date AS end_date,
                   rp.delivery_dt AS delivery_dt,
                   rp.done_days AS done_days,
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN destination d ON(rp.destination_id = d.id)
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteral_nutrient e ON(rp.enteral_nutrient_id = e.id)
            WHERE rp.id = ?
        """;
        List<PrescriptionInfo> result = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class), Integer.valueOf(id));
        return result.get(0);
    }

    List<PrescriptionInfo> findDays(String destinationId) {
        var query = """       
            SELECT rp.id AS id,
                   d.id AS destination_id,
                   d.name AS destination_name,
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteral_nutrient_id,
                   e.name AS enteral_nutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.start_date AS start_date,
                   rp.end_date AS end_date,
                   rp.delivery_dt AS delivery_dt,
                   rp.done_days AS done_days,
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN destination d ON(rp.destination_id = d.id)
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteral_nutrient e ON(rp.enteral_nutrient_id = e.id)
            WHERE rp.start_date >= CURRENT_DATE - INTERVAL '2' MONTH AND rp.end_date <= CURRENT_DATE + INTERVAL '3' MONTH AND rp.destination_id = ?
            ORDER BY p.id ASC, rp.start_date ASC;
        """;
        logger.debug(query);
        List<PrescriptionInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class), Integer.valueOf(destinationId));
        return results;
    }

    List<PrescriptionInfo> findDestinationId(String destinationId) {
        var query = """       
            SELECT rp.id AS id,
                   d.id AS destination_id,
                   d.name AS destination_name,
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteral_nutrient_id,
                   e.name AS enteral_nutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.start_date AS start_date,
                   rp.end_date AS end_date,
                   rp.delivery_dt AS delivery_dt,
                   rp.done_days AS done_days,
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN destination d ON(rp.destination_id = d.id)
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteral_nutrient e ON(rp.enteral_nutrient_id = e.id)
            WHERE rp.destination_id = ?
            ORDER BY p.id ASC, rp.start_date ASC;
        """;
        logger.debug(query);
        List<PrescriptionInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class), Integer.valueOf(destinationId));
        return results;
    }

    void delete(String id) {
        int rows = jdbcTemplate.update("DELETE FROM prescription WHERE id = ?", Integer.valueOf(id));
        if (rows != 1) {
            throw new RuntimeException("削除処理で異常が発生しました");
        }
    }

    void update(PrescriptionRecord rec) {
        int rows = jdbcTemplate.update("UPDATE prescription SET destination_id = ?, patient_id = ?, enteral_nutrient_id = ?, dosage = ?, dt = ?, days = ?, start_date = ?, end_date = ?, delivery_dt = ?, done_days = ?, done = ? WHERE id = ?",
                rec.destinationId, rec.patientId, rec.enteralNutrientId, rec.dosage, rec.dt, rec.days, rec.startDate, rec.endDate, rec.deliveryDt, rec.doneDays, rec.done, rec.id);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }

    void insert(PrescriptionRecord rec) {
        int rows = jdbcTemplate.update("INSERT INTO prescription (destination_id, patient_id, enteral_nutrient_id, dosage, dt, days, start_date, end_date, delivery_dt, done_days, done) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                rec.destinationId, rec.patientId, rec.enteralNutrientId, rec.dosage, rec.dt, rec.days, rec.startDate, rec.endDate, rec.deliveryDt, rec.doneDays, rec.done);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }
}
