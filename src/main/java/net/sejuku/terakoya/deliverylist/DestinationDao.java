package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationDao {
   Logger logger = LoggerFactory.getLogger(DestinationDao.class);
   private final JdbcTemplate jdbcTemplate;

   record DestinationInfo(
           Integer id,
           String name
   ) {}

   record DestinationRecord(
           Integer id,
           String name
   ) {}

   @Autowired
   DestinationDao(JdbcTemplate template) {
      this.jdbcTemplate = template;
   }

   List<DestinationInfo> findAll() {
      var query = """
         SELECT d.id,
                d.name
          FROM destination d
      """;
      logger.debug(query);
      List<DestinationInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(DestinationInfo.class));
      return results;
   }

   DestinationInfo find(String id) {
      var query = """
         SELECT d.id,
                d.name
          FROM destination d
          WHERE d.id = ?
      """;
      List<DestinationInfo> result = jdbcTemplate.query(query, new DataClassRowMapper<>(DestinationInfo.class), Integer.valueOf(id));
      return result.get(0);
   }

   void delete(String id) {
      int rows = jdbcTemplate.update("DELETE FROM destination WHERE id = ?", Integer.valueOf(id));
      if (rows !=1) {
         throw new RuntimeException("削除処理で異常が発生しました");
      }
   }

   void update(DestinationRecord rec) {
      int rows = jdbcTemplate.update("UPDATE destination SET name = ? WHERE id = ?",
              rec.name, rec.id);
      if (rows != 1) {
         throw new RuntimeException("更新処理で異常が発生しました");
      }
   }

   void insert(DestinationRecord rec) {
      int rows = jdbcTemplate.update("INSERT INTO destination (name) VALUES(?, ?)",
              rec.name);
      if (rows !=1) {
         throw new RuntimeException("更新処理で異常が発生しました");
      }
   }
}
