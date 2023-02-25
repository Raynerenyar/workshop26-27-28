package sg.edu.nus.iss.workshop27.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sg.edu.nus.iss.workshop27.model.Review;

@Controller
public class GameController {

    @GetMapping(path = "/")
    public String landingPage(Review review, Model model) {
        model.addAttribute("review", review);
        return "index";
    }
}
