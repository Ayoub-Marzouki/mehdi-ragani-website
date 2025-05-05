package art.mehdiragani.mehdiragani.core.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class DatabaseService {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns all table names in the database used
     */
    public List<String> getTables() {
        String query = "SELECT table_name " +
        "FROM information_schema.tables " +
        "WHERE table_schema = DATABASE()";
      return jdbcTemplate.queryForList(query, String.class);
    }
}
