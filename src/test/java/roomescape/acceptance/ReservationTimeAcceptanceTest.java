package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.dto.ReservationTimeRequest;
import roomescape.controller.dto.ReservationTimeResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationTimeAcceptanceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        insertDefaultData();
    }

    private void insertDefaultData() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES (?)", "09:30");
    }

    @DisplayName("예약 시간 전체 조회")
    @Test
    void get_reservationTimes() {
        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("예약 시간 생성")
    @Test
    void post_reservationTime() {
        ReservationTimeRequest request = new ReservationTimeRequest("10:00");
        ReservationTimeResponse expectedResponse = new ReservationTimeResponse(2L, "10:00");

        ReservationTimeResponse actualResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/times/2")
                .extract().as(ReservationTimeResponse.class);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        assertThat(countReservationTimes()).isEqualTo(2);
    }

    @DisplayName("예약 시간 삭제")
    @Test
    void delete_reservationTime() {
        RestAssured.given().log().all()
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(204);

        assertThat(countReservationTimes()).isZero();
    }

    private Integer countReservationTimes() {
        return jdbcTemplate.queryForObject("SELECT count(1) from reservation_time", Integer.class);
    }

}
