package roomescape.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

public record ReservationTimeResponse(
        Long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt) {

    public static ReservationTimeResponse from(ReservationTime time) {
        return new ReservationTimeResponse(time.id(), time.startAt());
    }

}
