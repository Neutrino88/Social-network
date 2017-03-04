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
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = users.findByUsername(principal.getUsername());

        try {
            model.addAttribute("user", user);
            return new ModelAndView("newPost");
        } catch (Exception e) {
            return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
        }
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ModelAndView savePost(String post_name, String content) {
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = users.findByUsername(principal.getUsername());

        // Check post_name and content formats
        if (postNameInNormalFormat(post_name) && contentInNormalFormat(content) ){
            try {
                Post post = new Post(post_name, content, user);
                posts.save(post);
            } catch (Exception e) { }

            return new ModelAndView(new RedirectView("/user/" + user.getId(), true));
        }
        return new ModelAndView(new RedirectView("/post/new/", true));
    }

    private boolean postNameInNormalFormat(String postName){
        // Check format post_name
        return postName.matches("(.*)\\w(.*)");
    }

    private boolean contentInNormalFormat(String content){
        return true;
    }
}
