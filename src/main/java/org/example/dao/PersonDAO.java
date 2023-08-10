package org.example.dao;

import org.example.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author AdamReign
 */
@Component
public class PersonDAO {
    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
//        for (int i = 11; i <= 2010; i++) {
//            delete(i);
//        }

        String sql = "SELECT * FROM person";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Person.class));
    }

    public Optional<Person> show(String email) {
        String sql = "SELECT * FROM person WHERE email = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Person.class), email)
                .stream().findAny();
    }

    public Person show(int id) {
        String sql = "SELECT * FROM person WHERE id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Person.class), id)
                .stream().findAny().orElse(null);
    }

    public void save(Person person) {
        String sql = "INSERT INTO Person (name, age, email, address) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, person.getName(), person.getAge(), person.getEmail(), person.getAddress());
    }

    public void update(int id, Person person) {
        String sql = "UPDATE person SET name = ?, age = ?, email = ?, address = ? WHERE id = ?";
        jdbcTemplate.update(sql, person.getName(), person.getAge(), person.getEmail(), person.getAddress(), id);
    }

    public void delete(int id) {
        String sql = "DELETE FROM person WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void testMultipleUpdate() {
        List<Person> people = create100People();
        long before = System.currentTimeMillis();

        people.forEach(this::save);

        long after = System.currentTimeMillis();

        System.out.println("Time: " + (after-before));
    }

    public void testBatchUpdate() {
        List<Person> people = create100People();
        long before = System.currentTimeMillis();

        String sql = "INSERT INTO Person (name, age, email, address) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, people.get(i).getName());
                ps.setInt(2, people.get(i).getAge());
                ps.setString(3, people.get(i).getEmail());
                ps.setString(4, people.get(i).getAddress());
            }

            @Override
            public int getBatchSize() {
                return people.size();
            }
        });

        long after = System.currentTimeMillis();

        System.out.println("Time: " + (after-before));
    }

    private List<Person> create100People() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            people.add(new Person("Name_" + i, 30, "test" + i + "@gmail.com", "Some address"));
        }
        return people;
    }
}