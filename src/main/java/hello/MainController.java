package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*; 
import org.springframework.beans.factory.annotation.Autowired;

import hello.User;
import hello.UserRepository;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            //errors processing
        }
        userRepository.save( new User(user.getName(), user.getEmail()) );
        model.addAttribute("user", user);
        return "result";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }
}