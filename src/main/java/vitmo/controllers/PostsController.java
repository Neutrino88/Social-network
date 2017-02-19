package vitmo.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vitmo.entities.Post;
import vitmo.entities.User;
import vitmo.repositories.PostsRepository;
import vitmo.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/post")
public class PostsController {
    @Autowired
    UsersRepository users;
    @Autowired
    PostsRepository posts;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView addPost(Model model) {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = users.findByUsername(principal.getUsername());
            model.addAttribute("user", user);
            return new ModelAndView("newPost");
        } catch (Exception e) {
            return new ModelAndView("/");
        }
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ModelAndView savePost(String post_name, String content) {
        //no empty fields allowed
        if (post_name.isEmpty() || content.isEmpty())
            return null;
        try {
            UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = users.findByUsername(principal.getUsername());

            Post post = new Post(post_name, content, user);
            posts.save(post);
            return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
        } catch (Exception e) {
            System.out.println("Error!\n");
        }
        return new ModelAndView("/");
    }
}
