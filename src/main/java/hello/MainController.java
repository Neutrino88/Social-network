package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndView;

import hello.User;
import hello.UserRepository;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{user_id}")
    public ModelAndView signup(@ModelAttribute User new_user, @PathVariable Integer user_id, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            //errors processing
        }

        try{
        	// Save new user
	        int id = userRepository.save(new_user);
	        if (id == 0) {
                // user already exists
                return "index";
            }

            model.addAttribute("user", new_user);
            return new ModelAndView(new RedirectView("/" + id, true));
        }catch(Exception e){
        	// do nothing
        }

        return new ModelAndView(new RedirectView("/", true));
    }

    @GetMapping("/{user_id}")
    public String myPage(@PathVariable Integer user_id, Model model) {
        if (userRepository.exists(user_id)){
    		User user = userRepository.findOne(user_id);
    		model.addAttribute("user", user);
    		return "result";
        }

        return "error";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }
}