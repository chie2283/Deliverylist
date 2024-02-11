package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientDao {
    Logger logger = LoggerFactory.getLogger(PatientDao.class);
    private final JdbcTemplate jdbcTemplate;

    record PatientInfo(
            Integer id,
            String name,
            Integer destinationId,
            String destinationName
            ) {}

    record PatientRecord(
            Integer id,
            String name,
            String destinationId
    ) {}

    @Autowired
    PatientDao(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    List<PatientInfo> findAll() {
        var query = """
            SELECT p.id AS id,
                   p.name AS name,
                   d.id AS destination_id,
                   d.name AS destination_name
            FROM patient p
            LEFT JOIN destination d ON(p.destination_id = d.id)
        """;
        logger.debug(query);
        List<PatientInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PatientInfo.class));
        return results;
    }

    PatientInfo find(String id) {
        var query = """
            SELECT p.id AS id,
                   p.name AS name,
                   d.id AS destination_id,
                   d.name AS destination_name
            FROM patient p
            LEFT JOIN destination d ON(p.destination_id = d.id)
            WHERE p.id = ?
        """;
        List<PatientInfo> result = jdbcTemplate.query(query, new DataClassRowMapper<>(PatientInfo.class), Integer.valueOf(id));
        return result.get(0);
    }

    void delete(String id) {
        int rows = jdbcTemplate.update("DELETE FROM patient WHERE id = ?", Integer.valueOf(id));
        if (rows != 1) {
            throw new RuntimeException("削除処理で異常が発生しました");
        }
    }

    void update(PatientRecord rec) {
        int rows = jdbcTemplate.update("UPDATE patient SET name = ?, destination_id = ?, WHERE id = ?",
                rec.name, rec.destinationId, rec.id);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }

    void insert(PatientRecord rec) {
        int rows = jdbcTemplate.update("INSERT INTO patient (name, destination_id) VALUES(?, ?)",
                rec.name, rec.destinationId);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }
}
