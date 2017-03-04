package vitmo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vitmo.entities.User;
import vitmo.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    UsersRepository usersRepository;
    List<User> users;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView showSearchPage(Model model) {
        this.users = new ArrayList<User>();
        usersRepository.findAll().iterator().forEachRemaining(this.users::add);

        return new ModelAndView("search");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    List<User> search(@RequestParam(value = "username") String username) {
        ArrayList<User> foundUsers = new ArrayList<User>();

        if (!username.isEmpty())
            for (User user : users) {
                if (user.getUsername().contains(username)) {
                    foundUsers.add(user);
                }
            }
        return foundUsers;
    }
}
