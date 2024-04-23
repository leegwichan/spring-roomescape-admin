package roomescape.controller.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

public record ReservationRequest(String date, String name, Long timeId) {

    public Reservation toDomain() {
        return new Reservation(name, date, new ReservationTime(timeId));
    }

}
