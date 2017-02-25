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

    class FoundUser {
        private String username;
        private long id;

        public FoundUser(String username, long id) {
            this.username = username;
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView showSearchPage(Model model) {
        Iterable<User> users = usersRepository.findAll();
        this.users = new ArrayList<User>();
        for (User user: users) {
            this.users.add(user);
        }
        return new ModelAndView("search");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    List<FoundUser> search(@RequestParam(value = "username") String username) {
        ArrayList<FoundUser> foundUsers = new ArrayList<FoundUser>();
        for (User user : this.users) {
            if (!username.equals("") && user.getUsername().contains(username)) {
                foundUsers.add(new FoundUser(user.getUsername(), user.getId()));
            }
        }
        return foundUsers;
    }
}
