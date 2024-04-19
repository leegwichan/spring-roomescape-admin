package roomescape.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepository {

    private static final String TABLE_NAME = "reservation";
    private static final String TABLE_NAME2 = "reservation2";
    private static final RowMapper<Reservation> ROW_MAPPER = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("date"),
            resultSet.getString("time"));
    private static final RowMapper<Reservation2> ROW_MAPPER2 = (resultSet, rowNum) -> new Reservation2(
            resultSet.getLong("id"),
            resultSet.getString("date"),
            resultSet.getString("name"),
            new ReservationTime(resultSet.getLong("time_id"), resultSet.getString("start_at")));

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query("SELECT id, name, date, time FROM %s".formatted(TABLE_NAME), ROW_MAPPER);
    }

    public Reservation2 create(Reservation2 reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertQuery(connection, reservation), keyHolder);

        Long id = keyHolder.getKey().longValue();
        return findById2(id);
    }

    private PreparedStatement insertQuery(Connection connection, Reservation2 reservation) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO %s (name, date, time_id) VALUES (?, ?, ?)".formatted(TABLE_NAME2), new String[]{"id"});
        preparedStatement.setString(1, reservation.name());
        preparedStatement.setString(2, reservation.date());
        preparedStatement.setLong(3, reservation.time().id());
        return preparedStatement;
    }

    private Reservation2 findById2(Long id) {
        Reservation2 reservation = jdbcTemplate.queryForObject("""
                SELECT
                    r.id AS id,
                    name,
                    date,
                    t.id AS time_id,
                    start_at
                FROM reservation2 AS r
                INNER JOIN reservation_time AS t
                on r.time_id = t.id
                WHERE r.id = ?""".formatted(TABLE_NAME2), ROW_MAPPER2, id);

        if (reservation == null) {
            throw new IllegalStateException("해당 예약이 없습니다.");
        }
        return reservation;
    }

    public void deleteById(Long id) {
        Reservation foundReservation = findById(id);
        jdbcTemplate.update("DELETE FROM %s WHERE id = ?".formatted(TABLE_NAME), foundReservation.id());
    }

    private Reservation findById(Long id) {
        Reservation reservation = jdbcTemplate.queryForObject(
                "SELECT id, name, date, time FROM %s WHERE id = ?".formatted(TABLE_NAME), ROW_MAPPER, id);

        if (reservation == null) {
            throw new IllegalStateException("해당 예약이 없습니다.");
        }
        return reservation;
    }

}
