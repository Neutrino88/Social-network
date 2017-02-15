package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hello.User;
import hello.UserRepository;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{user_id}")
    public ModelAndView signup(@ModelAttribute User new_user, 
                            @ModelAttribute String passwordConfirm,
                            @PathVariable Integer user_id, 
                            BindingResult bindingResult,
                            Model model) {

        if (bindingResult.hasErrors()) {
            //errors processing
        }

        try{
            // Get hash from password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(new_user.getPassword());

            // Save user with hash instead of a password
            new_user.setPassword(hashedPassword);
            User user = userRepository.save(new_user);
    	        
            if (user.getId() == 0) {
                // user already exists
                return new ModelAndView(new RedirectView("/", true));
            }

            model.addAttribute("user", user);
            return new ModelAndView(new RedirectView("/" + user.getId(), true));               
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

    		return "profile";
        }

        return "error";
    }

    @PostMapping("/log_in")
    public ModelAndView login(Model model,
                             @ModelAttribute User user) {
        // Search user with right email
        String rawPassword = user.getPassword();
        user = findOneByEmail( user.getEmail() );

        if (user.getId() == 0) 
            return new ModelAndView(new RedirectView("/login", true));
        
        try{
            // Check password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(rawPassword);
        
            if ( passwordEncoder.matches(rawPassword, user.getPassword()) ){
                model.addAttribute("user", user);
                return new ModelAndView(new RedirectView("/" + user.getId(), true));
            }
        }catch(Exception e){
            // do nothing
        }

        return new ModelAndView(new RedirectView("/login", true));
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("passwordConfirm", new String());
        return "index";
    }

    public User findOneByEmail(String email){
        Iterable <User> iter = userRepository.findAll();

        for(User cur_user : iter)
            if ( email.equals(cur_user.getEmail()) )
                return cur_user;
        
        return new User();
    }
}