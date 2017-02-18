package vitmo.controllers;

import vitmo.entities.User;
import vitmo.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class UsersController {
    @Autowired
    UsersRepository users;

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
    public ModelAndView addUser(String username, String password, String password_confirm)
    {
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
    public void delete(@PathVariable("id") Long id)
    {
        users.delete(id);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ModelAndView getUser(@PathVariable("id") Long id, ModelMap model)
    {
        model.addAttribute("user", users.findOne(id));
        return new ModelAndView("profile");
    }
}