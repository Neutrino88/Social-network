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

import java.text.DateFormat;

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
        //no empty fields allowed
        if (username.isEmpty() || password.isEmpty() || password_confirm.isEmpty())
            return null;
        //passwords should match
        if (!password.equals(password_confirm))
            return null;
        try {
            User user = new User(username, password);
            users.save(user);
            return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
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
        if (!equalsPassword(cur_password, user)) {
            return new ModelAndView("/settings");
        }

        // change username
        if (username.length() >= 4) {
            try{
                User user_from_db = users.findOne(user.getId());
                user_from_db.setUsername(username);
                user = users.save(user_from_db);
            }catch(Exception e){
                return new ModelAndView("/settings");
            }
        }
        // change password
        if ( new_password.length() >= 6  && new_password.equals(confirm_password) ) {
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
        return password.equals(user.getPassword());
    }
}
