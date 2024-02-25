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
            String birthday
    ) {}

    record PatientRecord(
            Integer id,
            String name,
            String birthday
    ) {}

    @Autowired
    PatientDao(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    List<PatientDao.PatientInfo> findAll() {
        var query = """
         SELECT p.id,
                p.name,
                p.birthday
          FROM patient p
      """;
        logger.debug(query);
        List<PatientDao.PatientInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(PatientDao.PatientInfo.class));
        return results;
    }

    PatientDao.PatientInfo find(String id) {
        var query = """
         SELECT p.id,
                p.name,
                p.birthday
          FROM patient p
          WHERE p.id = ?
      """;
        List<PatientDao.PatientInfo> result = jdbcTemplate.query(query, new DataClassRowMapper<>(PatientDao.PatientInfo.class), Integer.valueOf(id));
        return result.get(0);
    }

    void delete(String id) {
        int rows = jdbcTemplate.update("DELETE FROM patient WHERE id = ?", Integer.valueOf(id));
        if (rows !=1) {
            throw new RuntimeException("削除処理で異常が発生しました");
        }
    }

    void update(PatientDao.PatientRecord rec) {
        int rows = jdbcTemplate.update("UPDATE patient SET name = ?, birthday = ?, WHERE id = ?",
                rec.name, rec.birthday, rec.id);
        if (rows != 1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }

    void insert(PatientDao.PatientRecord rec) {
        int rows = jdbcTemplate.update("INSERT INTO patient (name, birthday) VALUES(?, ?)",
                rec.name, rec.birthday);
        if (rows !=1) {
            throw new RuntimeException("更新処理で異常が発生しました");
        }
    }
}
