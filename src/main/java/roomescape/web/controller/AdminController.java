package roomescape.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String showMainPage() {
        return "index";
    }

    @GetMapping("/reservation")
    public String showReservationPage() {
        return "admin/reservation";
    }

    @GetMapping("/time")
    public String showTimePage() {
        return "admin/time";
    }

}
