package roomescape;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/reservation")
    public String showReservationPage() {
        return "admin/reservation-legacy";
    }

}