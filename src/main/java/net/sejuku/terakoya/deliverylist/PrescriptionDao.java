package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionDao {
    Logger logger = LoggerFactory.getLogger(PrescriptionDao.class);
    private final JdbcTemplate jdbcTemplate;

    record PrescriptionInfo(
            Integer id,
            Integer patientId,
            String patientName,
            Integer enteralNutrientId,
            String enteralNutrientName,
            String dosage,
            String dt,
            Integer days,
            String deliveryDt,
            Integer doneDays,
            Boolean done
    ) {}

    record PrescriptionRecord(
            Integer id,
            String patientId,
            String enteralNutrientId,
            String dosage,
            String dt,
            Integer days,
            String deliveryDt,
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
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteralNutrient_id,
                   e.name AS enteralNutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.deliveryDt AS deliveryDt,
                   rp.doneDays AS doneDays
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteralNutrient e ON(rp.enteralNutrient_id = e.id)
        """;
        logger.debug(query);
        List<PrescriptionInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class));
        return results;
    }

    PrescriptionInfo find(String id) {
        var query = """
            SELECT rp.id AS id,
                   p.id AS patient_id,
                   p.name AS patient_name,
                   e.id AS enteralNutrient_id,
                   e.name AS enteralNutrient_name,
                   rp.dosage AS dosage,
                   rp.dt AS dt,
                   rp.days AS days,
                   rp.deliveryDt AS deliveryDt,
                   rp.doneDays AS doneDays
                   rp.done AS done
            FROM prescription rp
            LEFT JOIN patient p ON(rp.patient_id = p.id)
            LEFT JOIN enteralNutrient e ON(rp.enteralNutrient_id = e.id)
            WHERE rp.id = ?
        """;
        List<PrescriptionInfo> result = jdbcTemplate.query(query, new DataClassRowMapper<>(PrescriptionInfo.class), Integer.valueOf(id));
        return result.get(0);
    }

    void delete(String id) {
        int rows = jdbcTemplate.update("DELETE FROM prescription WHERE id = ?", Integer.valueOf(id));
        if (rows != 1) {
            throw new RuntimeException("削除処理で異常が発生しました");
        }
    }

    void update(PrescriptionRecord rec) {
        int rows = jdbcTemplate.update("UPDATE prescription SET patient_id = ?, enteralNutrient_id = ?, WHERE id = ?",
                rec.patientId, rec.enteralNutrientId, rec.id);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }

    void insert(PrescriptionRecord rec) {
        int rows = jdbcTemplate.update("INSERT INTO prescription (patient_id, enteralNutrient_id) VALUES(?, ?)",
                rec.patientId, rec.enteralNutrientId);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }
}
