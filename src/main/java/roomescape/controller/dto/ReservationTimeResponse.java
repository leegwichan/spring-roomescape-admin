package roomescape.controller.dto;

import roomescape.domain.ReservationTime;

public record ReservationTimeResponse(
        Long id,
        String startAt) {

    public static ReservationTimeResponse from(ReservationTime time) {
        return new ReservationTimeResponse(time.id(), time.startAt());
    }

}
