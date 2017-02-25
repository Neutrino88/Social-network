package vitmo.controllers;

import org.springframework.web.bind.annotation.*;
import vitmo.entities.User;
import vitmo.repositories.PostsRepository;
import vitmo.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class UsersController {
    @Autowired
    UsersRepository users;
    @Autowired
    PostsRepository posts;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(ModelMap model) {
        try {
            UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = users.findByUsername(principal.getUsername());
            return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
        } catch (Exception e) {
            return new ModelAndView("index");
        }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView addUser(String username, String password, String password_confirm) {
        // Check format fields
        if (    !passwordInNormalFormat(password)  ||
                !password.equals(password_confirm) ||
                !usernameInNormalFormat(username) )
            return new ModelAndView("index");
        // Save new user
        try {
            User user = new User(username, password);
            users.save(user);
            return new ModelAndView("login");
        } catch (Exception e) {
            return new ModelAndView("index");
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
        users.delete(id);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ModelAndView getUser(@PathVariable("id") Long id, ModelMap model) {
        User user = users.findOne(id);
        java.util.Collections.reverse(user.getPosts());
        model.addAttribute("user", user);
        model.addAttribute("posts", user.getPosts());
        return new ModelAndView("profile");
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ModelAndView changeData(ModelMap model) {
        // Get current user
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = users.findByUsername(principal.getUsername());

        model.addAttribute("user", user);
        return new ModelAndView("/settings");
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public ModelAndView changeData(String cur_password, String username, String new_password, String confirm_password, ModelMap model) {
        // Get current user
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = users.findByUsername(principal.getUsername());

        // if current password is wrong
        if (!equalsPassword(cur_password, user))
            return new ModelAndView("/settings");

        // change username
        if (usernameInNormalFormat(username)) {
            try{
                User user_from_db = users.findOne(user.getId());
                user_from_db.setUsername(username);
                user = users.save(user_from_db);
            }catch(Exception e){
                return new ModelAndView("/settings");
            }
        }
        // change password
        if (    passwordInNormalFormat(new_password)  &&
                equalsPassword(new_password, confirm_password) ){
            try{
                User user_from_db = users.findOne(user.getId());
                user_from_db.setPassword(new_password);
                user = users.save(user_from_db);
            }catch(Exception e){
            }
        }

        model.addAttribute("user", user);
        return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
    }

    private boolean equalsPassword(String password, User user){
    // Compare password with user.password
        return password.equals(user.getPassword());
    }
    private boolean equalsPassword(String password, String confirm_password){
        // Compare password with confirm_password
        return password.equals(confirm_password);
    }

    private boolean usernameInNormalFormat(String username){
        // Check length of username
        if (username.length() < 4 || username.length() > 20)
            return false;

        // Check format every characters in username
        for (char symbol : username.toCharArray()) {
            int code = (int) symbol;

            if (  !(48 <= code && code <= 57 ||     // if not in 0..9 and
                    65 <= code && code <= 90 ||     // if not in A..Z and
                    97 <= code && code <= 122 ||    // if not in a..z and
                    (int) '_' == code))             // if is not '_'
                return false;
        }
        return true;
    }
    private boolean passwordInNormalFormat(String password){
        // Check length of username
        if (password.length() < 6 || password.length() > 20)
            return false;

        return true;
    }
}
