package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnteralNutrientDao {
    Logger logger = LoggerFactory.getLogger(EnteralNutrientDao.class);
    private final JdbcTemplate jdbcTemplate;

    record  EnteralNutrientInfo(
            Integer id,
            String name) {}

    @Autowired
    EnteralNutrientDao(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public  List<EnteralNutrientInfo> findAll() {
        var query = "SELECT * FROM enteral_nutrient";
        List<EnteralNutrientInfo> results = jdbcTemplate.query(query, new DataClassRowMapper<>(EnteralNutrientInfo.class));
        return  results;
    }
}