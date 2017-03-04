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
        if (!passwordInNormalFormat(password)  ||
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
    public ModelAndView changeData(String curPassword, String username, String newPassword, String confirmPassword, ModelMap model) {
        // Get current user
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = users.findByUsername(principal.getUsername());

        // if current password is wrong
        if (!equalsPassword(curPassword, user))
            return new ModelAndView("/settings");

        // change username
        if (usernameInNormalFormat(username)) {
            try{
                User userFromDB = users.findOne(user.getId());
                userFromDB.setUsername(username);
                user = users.save(userFromDB);
            }catch(Exception e){
                return new ModelAndView("/settings");
            }
        }
        // change password
        if (passwordInNormalFormat(newPassword)  && equalsPassword(newPassword, confirmPassword) ){
            try{
                User userFromDB = users.findOne(user.getId());
                userFromDB.setPassword(newPassword);
                user = users.save(userFromDB);
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
        // Check length and format of username
        return username.matches("\\w{4,20}");
    }
    private boolean passwordInNormalFormat(String password){
        // Check length of username
        return password.matches(".{6,20}");
    }
}
